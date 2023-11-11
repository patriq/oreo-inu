import org.rspeer.commons.StopWatch
import org.rspeer.commons.logging.Log
import org.rspeer.event.Subscribe
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.component.Interfaces
import org.rspeer.game.component.tdi.Magic
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.component.tdi.Spell
import org.rspeer.game.event.SkillEvent
import org.rspeer.game.event.TickEvent
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Script
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme

@ScriptMeta(
    name = "Superglass",
    developer = "Oreo",
    version = 1.00,
    desc = "Makes superglass",
    paint = PaintScheme::class,
    regions = [-3]
)
class Superglass : Script() {
    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Tick")
    private var tick: Int = -1

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.CRAFTING, Skill.MAGIC)

    var lastXpTick = -1

    @Subscribe(async = false)
    fun onTick(event: TickEvent) {
        if ((Backpack.backpack().query().names("Astral rune").results().firstOrNull()?.stackSize ?: 0) < 2) {
            Log.severe("Out of astrals")
            state = State.STOPPED
            return
        }
        if (tick == 0) {
            if (Bank.isOpen()) {
                if (!Bank.bank().withdraw("Giant seaweed", 1)) {
                    Log.severe("Out of seaweed")
                    state = State.STOPPED
                    return
                }
                Bank.bank().withdraw("Giant seaweed", 1)
                if (!Bank.bank().withdraw("Bucket of sand", 18)) {
                    Log.severe("Out of sand")
                    state = State.STOPPED
                    return
                }
                Interfaces.closeSubs()
            }
            openBank() && Magic.cast(Spell.Lunar.SUPERGLASS_MAKE)
        } else if (tick == 4) {
            Bank.bank().depositInventory() && Bank.bank().withdraw("Giant seaweed", 1)
        }
        tick = (tick + 1) % 5
    }

    @Subscribe(async = false)
    fun onSkill(event: SkillEvent) {
        if (event.source == Skill.CRAFTING) {

            Log.info("Ticks: ${event.tick - lastXpTick}")
            lastXpTick = event.tick
        }
    }

    override fun loop(): Int {
        return 10000
    }

    private fun openBank(): Boolean {
        return SceneObjects.query().names("Bank chest").results().firstOrNull()?.interact("Use") == true
    }
}
