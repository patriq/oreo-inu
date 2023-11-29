import org.rspeer.commons.StopWatch
import org.rspeer.commons.logging.Log
import org.rspeer.event.Subscribe
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.combat.Combat
import org.rspeer.game.component.tdi.Magic
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.component.tdi.Spell
import org.rspeer.game.component.tdi.Tab
import org.rspeer.game.component.tdi.Tabs
import org.rspeer.game.event.SkillEvent
import org.rspeer.game.event.TickEvent
import org.rspeer.game.position.Position
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Script
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme

@ScriptMeta(
    name = "3 tick granite miner",
    developer = "Oreo",
    version = 1.00,
    desc = "Mines granite in the desert quarry and drops it",
    paint = PaintScheme::class,
    regions = [-3]
)
class GraniteMiner : Script() {
    private companion object {
        private val ROCK_POSITIONS = arrayOf(
            Position(3165, 2908, 0),
            Position(3165, 2909, 0),
            Position(3165, 2910, 0),
            Position(3167, 2911, 0),
        )
    }

    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Tick")
    private var tick: Int = -1

    @PaintBinding("Rock index")
    private var rockIndex = 0

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.MINING)

    private var lastXpTick = -1

    @Subscribe(async = false)
    fun onTick(event: TickEvent) {
        val waterskins = Backpack.backpack().query().nameContains("Waterskin").results()
        if (waterskins.all { it.name.contains("(0)") }) {
            Magic.cast(Spell.Lunar.HUMIDIFY)
            return
        }

        val rocks = SceneObjects.query().names("Granite rocks").on(*ROCK_POSITIONS).results()
            .sortedBy { ROCK_POSITIONS.indexOf(it.position) }

        // Find the rock with the current index
        val rock = rocks.find { it.position == ROCK_POSITIONS[rockIndex] } ?: return

        // Drop if backpack somehow gets full
        if (Backpack.backpack().isFull) {
            Tabs.open(Tab.INVENTORY)
            Backpack.backpack().query().nameContains("Granite").results().take(5).forEach { it.interact("Drop") }
            return
        }

        // Reset tick if we are doing the mining animation to force using the knife on the logs
        if (Players.self().animationId == 7139) {
            tick = 0
            return
        }

        // Main logic
        if (tick == 0) {
            val knife = Backpack.backpack().query().names("Knife").results().firstOrNull()
            val mahoganyLogs = Backpack.backpack().query().names("Mahogany logs").results().firstOrNull()
            if (knife == null || mahoganyLogs == null) {
                Log.severe("No knife or mahogany logs found")
                state = State.STOPPED
                return
            }

            // Use mahogany logs on knife
            Backpack.backpack().use(knife, mahoganyLogs)

            // Queue movement next to the rock
            rock.interact("Mine")
        }

        if (tick == 1) {
            // Drop if needed and requeue chopping
            Tabs.open(Tab.INVENTORY)
            Backpack.backpack().query().nameContains("Granite").results().take(2).forEach { it.interact("Drop") }

            // Cast special if you have it
            if (Combat.getSpecialEnergy() == 100) {
                Combat.toggleSpecial(true)
            }

            // Chop tree
            rock.interact("Mine")
        }

        if (tick == 2) {
            rockIndex = (rockIndex + 1) % ROCK_POSITIONS.size
        }

        tick = (tick + 1) % 3
    }

    @Subscribe(async = false)
    fun onSkill(event: SkillEvent) {
        if (event.source == Skill.MINING) {
            Log.info("Ticks: ${event.tick - lastXpTick}")
            lastXpTick = event.tick
        }
    }

    override fun loop(): Int {
        return 20000
    }
}