package blastfurnace

import blastfurnace.task.*
import org.rspeer.commons.StopWatch
import org.rspeer.game.Vars
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskScript
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme
import java.util.function.Supplier

@ScriptMeta(
    developer = "Oreo",
    name = "Blast Furnace",
    desc = "Does blast furnace for you",
    version = 1.0,
    paint = PaintScheme::class,
    regions = [-3]
)
class BlastFurnace : TaskScript() {
    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.SMITHING)

    @PaintBinding("Task")
    private var task: Supplier<String> = Supplier { manager.lastTaskName }

    @PaintBinding("Gold bars in forge")
    private var goldBarsInForge: Supplier<Int> = Supplier { Vars.get(546) shr 16 }

    override fun tasks(): Array<Class<out Task>> {
        return arrayOf(
            OpenBankTask::class.java,
            WithdrawTask::class.java,
            PlaceOreTask::class.java,
            WalkDispenserTask::class.java,
//            MakeBarsTask::class.java,
            TakeBarsTask::class.java
        )
    }
}