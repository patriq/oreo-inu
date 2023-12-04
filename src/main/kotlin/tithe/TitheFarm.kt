package tithe

import org.rspeer.commons.StopWatch
import org.rspeer.commons.logging.Log
import org.rspeer.event.Subscribe
import org.rspeer.game.Game
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.component.tdi.Magic
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.component.tdi.Spell
import org.rspeer.game.event.AnimationEvent
import org.rspeer.game.event.ChatMessageEvent
import org.rspeer.game.event.InventoryEvent
import org.rspeer.game.event.RenderEvent
import org.rspeer.game.event.SkillEvent
import org.rspeer.game.event.TickEvent
import org.rspeer.game.movement.Movement
import org.rspeer.game.position.Position
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.Projection
import org.rspeer.game.script.Script
import org.rspeer.game.script.event.ScriptConfigEvent
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme
import org.rspeer.game.script.meta.ui.ScriptOption
import org.rspeer.game.script.meta.ui.ScriptUI
import java.awt.Color
import java.util.function.Supplier

@ScriptMeta(
    developer = "Oreo",
    name = "Tithe Farm",
    desc = "Plants seeds at the Tithe Farm",
    version = 1.0,
    paint = PaintScheme::class,
    regions = [-3]
)
@ScriptUI(
    value = [
        ScriptOption(name = "Seed", type = Seed::class),
        ScriptOption(name = "Farming outfit", type = Boolean::class)
    ]
)
class TitheFarm: Script() {
    companion object {
        // 25x4 patches
        private val PATCH_ORDER = listOf(
            Patch(Position(1820, 3482, 0), Position(1820, 3485, 0), true),
            Patch(Position(1820, 3488, 0), Position(1819, 3489, 0), true),
            Patch(Position(1815, 3488, 0), Position(1818, 3490, 0), true),
            Patch(Position(1815, 3491, 0), Position(1818, 3491, 0), false),
            Patch(Position(1820, 3491, 0), Position(1819, 3493, 0), true),
            Patch(Position(1820, 3494, 0), Position(1819, 3494, 0), false),
            Patch(Position(1815, 3494, 0), Position(1818, 3496, 0), true),
            Patch(Position(1815, 3497, 0), Position(1818, 3497, 0), false),
            Patch(Position(1820, 3497, 0), Position(1819, 3499, 0), true),
            Patch(Position(1820, 3503, 0), Position(1819, 3503, 0), true),
            Patch(Position(1815, 3503, 0), Position(1818, 3505, 0), true),
            Patch(Position(1815, 3506, 0), Position(1818, 3506, 0), false),
            Patch(Position(1820, 3506, 0), Position(1819, 3508, 0), true),
            Patch(Position(1820, 3509, 0), Position(1819, 3509, 0), false),
            Patch(Position(1815, 3509, 0), Position(1818, 3511, 0), true),
            Patch(Position(1815, 3512, 0), Position(1818, 3512, 0), false),
            Patch(Position(1820, 3512, 0), Position(1819, 3514, 0), true),
            Patch(Position(1825, 3512, 0), Position(1824, 3514, 0), true),
            Patch(Position(1825, 3509, 0), Position(1824, 3510, 0), true),
            Patch(Position(1825, 3506, 0), Position(1824, 3508, 0), true),
            Patch(Position(1825, 3503, 0), Position(1824, 3504, 0), true),
            Patch(Position(1825, 3497, 0), Position(1824, 3498, 0), true),
            Patch(Position(1825, 3494, 0), Position(1824, 3494, 0), true),
            Patch(Position(1825, 3491, 0), Position(1824, 3492, 0), true),
            Patch(Position(1825, 3488, 0), Position(1824, 3488, 0), true),
        )
        private const val WATERING_ANIMATION = 2293
        private const val HUMIDIFY_ANIMATION = 6294
        private const val HARVEST_ANIMATION = 830
    }

    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Seed")
    private var seed: Seed = Seed.NONE

    @PaintBinding("Experience", rate = true)
    private var totalXp =
        Supplier<Int> { gainedXp + ((fruitsGained.toFloat() / 100.0f) * seed.batchExperience * xpMultiplier).toInt() }

    @PaintBinding("Fruits harvested")
    private var fruitsGained = 0

    @PaintBinding("Can charges")
    private var canCharges = Supplier<Int> { wateringCanCharges() }

    private var gricollerCanCharges = -1
    private var lastGricollerMessageTick = -1

    private var gainedXp = 0
    private var xpMultiplier = 1f

    @Subscribe
    fun onConfigure(event: ScriptConfigEvent) {
        seed = event.source.get("Seed")
        xpMultiplier = if (event.source.getBoolean("Farming outfit")) 1.025f else 1f
    }

    override fun loop(): Int {
        return 20000
    }

    @Subscribe(async = false)
    fun onTick(event: TickEvent) {
        // Check gricollier's can charges
        if (gricollerCanCharges == -1) {
            Backpack.backpack().query().nameContains("Gricoller's can").results().firstOrNull()?.interact("Check")
            return
        }

        // Don't do anything if we are paused or if we are not done selecting the seed
        if (state == State.PAUSED || seed == Seed.NONE) {
            return
        }

        // Drop fertiliser
        Backpack.backpack().query().nameContains("fertiliser").results().firstOrNull()?.interact("Drop")

        // Turn run energy on
        if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 25) {
            Movement.toggleRun(true)
        }

        // Get the current patch
        val currentPatch = currentPatch() ?: return

        // If we are about to start a new run, make sure we have enough seeds and water
        if (currentPatch == PATCH_ORDER[0] && currentPatch.getState(seed) == PathState.EMPTY) {
            if (Backpack.backpack().getCount({ it.names(seed.seedName).results() }, true) < 24) {
                Log.severe("Not enough seeds")
                state = State.STOPPED
                return
            }

            // Humidify if we don't have enough watering can charges to do a full run
            if (wateringCanCharges() < 80 && Players.self().animationId != HUMIDIFY_ANIMATION) {
                Magic.cast(Spell.Lunar.HUMIDIFY)
            }
        }

        // Handle the current patch
        currentPatch.handle(seed)
    }

    @Subscribe(async = false)
    fun onChatMessage(event: ChatMessageEvent) {
        if (event.contents.contains("Watering can charges")) {
            val canPercentage = event.contents.filter { it.isDigit() || it == '.' }.toFloat() / 100.0f
            gricollerCanCharges = (1000 * canPercentage).toInt()
            lastGricollerMessageTick = event.tick
        }
        if (event.contents.contains("You need to recharge your watering can")) {
            gricollerCanCharges = 0
        }
    }

    @Subscribe(async = false)
    fun onAnimation(event: AnimationEvent) {
        if (event.source != Players.self()) {
            return
        }
        // Only decrease charges if we didn't get a message from the gricoller's can on the same tick
        if (event.current == WATERING_ANIMATION && event.tick != lastGricollerMessageTick) {
            gricollerCanCharges -= 1
        }
        // Reset charges if we are humidifying
        if (event.current == HUMIDIFY_ANIMATION) {
            gricollerCanCharges = 1000
        }
        if (event.current == HARVEST_ANIMATION) {
            fruitsGained += 1
        }
    }

    @Subscribe
    fun onSkill(event: SkillEvent) {
        if (event.source == Skill.FARMING) {
            gainedXp += event.change
        }
    }

/*    @Subscribe
    fun onRender(event: RenderEvent) {
        // Draw patches
        PATCH_ORDER.forEach { patch ->
            event.source.color = Color.PINK
            Projection.getPositionPolygon(Projection.Canvas.VIEWPORT, patch.patchPosition)?.let {
                event.source.drawPolygon(it)
            }

            val state = patch.getState(seed)
            Projection.toScreen(Projection.Canvas.VIEWPORT, patch.patchPosition)?.let { point ->
                event.source.drawString(state.name, point.x, point.y)
            }

            event.source.color = if (patch.forceWalk) Color.YELLOW else Color.CYAN
            Projection.getPositionPolygon(Projection.Canvas.VIEWPORT, patch.actionPosition)?.let {
                event.source.drawPolygon(it)
            }
        }
    }*/

    private fun currentPatch(): Patch? {
        // If any patch is an unwatered seedling, water it (has priority over all)
        PATCH_ORDER.forEach { patch ->
            if (patch.getState(seed) == PathState.UNWATERED_SEEDLING) {
                return patch
            }
        }

        // If a patch is empty and there are no fruits to harvest, plant a seed
        val anyHarvestable = PATCH_ORDER.any { it.getState(seed) == PathState.PLANT_HARVEST }
        if (!anyHarvestable) {
            PATCH_ORDER.forEach { patch ->
                if (patch.getState(seed) == PathState.EMPTY) {
                    return patch
                }
            }
        }

        // Return the patch with lowest state that requires any handling
        return PATCH_ORDER.filter {
            val state = it.getState(seed)
            state == PathState.UNWATERED_PLANT_FLOWER || state == PathState.UNWATERED_PLANT_VEG || state == PathState.PLANT_HARVEST
        }.minByOrNull { it.getState(seed).ordinal }
    }

    private fun wateringCanCharges(): Int {
        if (Backpack.backpack().query().nameContains("Gricoller's can").results().isNotEmpty()) {
            return gricollerCanCharges
        }
        return Backpack.backpack().query().nameContains("Watering can(").results()
            .sumOf { item -> item.name.filter { it.isDigit() }.toInt() }
    }
}