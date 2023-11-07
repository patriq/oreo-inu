package knights

import api.script.GuiceTaskScript
import knights.task.*
import org.rspeer.commons.StopWatch
import org.rspeer.event.Subscribe
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.event.ChatMessageEvent
import org.rspeer.game.scene.Players
import org.rspeer.game.script.Task
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme
import java.util.function.Supplier


@ScriptMeta(
    name = "Ardy Knights",
    developer = "Oreo",
    version = 1.00,
    desc = "Steals from Ardy Knights",
    paint = PaintScheme::class,
    regions = [-3]
)
class ArdyKnights : GuiceTaskScript() {
    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Task")
    private val taskName: Supplier<String> = Supplier { manager.lastTaskName }

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.THIEVING)

    @PaintBinding("Coins", rate = true)
    private var coins: Int = 0

    override fun tasks(): Array<Class<out Task>> {
        return arrayOf(
            StopTask::class.java,
            EquipDodgyNecklaceTask::class.java,
            OpenPouchTask::class.java,
            BankTask::class.java,
            PickpocketTask::class.java,
            EatTask::class.java
        )
    }

    @Subscribe
    fun onChatMessage(event: ChatMessageEvent) {
        val message = event.contents
        if (message.startsWith("You open all of the pouches")) {
            coins += event.contents.filter { it.isDigit() }.toInt()
        }
        if (message.contains("steal twice as much loot")) {
            coins += 50
        }
    }

    companion object {
        fun isPlayerStunned(): Boolean {
            return Players.self()?.effects?.any { it.id == 245 } ?: false
        }
    }
}