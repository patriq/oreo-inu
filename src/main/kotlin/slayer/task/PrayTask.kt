package slayer.task

import api.containsPlayer
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.component.tdi.Prayers
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import javax.inject.Inject

@TaskDescriptor(
    name = "Pray",
)
class PrayTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    override fun execute(): Boolean {
        if (Prayers.getPoints() == 0) {
            return false
        }

        if (ctx.currentTaskInfo()?.standingArea()?.containsPlayer() != true) {
            return false
        }
        val prayers = ctx.currentTaskInfo()?.prayers() ?: return false
        prayers.forEach { Prayers.toggle(true, it) }
        return true
    }
}