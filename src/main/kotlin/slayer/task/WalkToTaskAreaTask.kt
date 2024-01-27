package slayer.task

import api.containsPlayer
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import javax.inject.Inject

@TaskDescriptor(
    name = "Walk to task area",
    blocking = true,
    blockIfSleeping = true,
)
class WalkToTaskAreaTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    override fun execute(): Boolean {
        val currentTaskInfo = ctx.currentTaskInfo() ?: return false
        if (currentTaskInfo.standingArea().containsPlayer()) {
            return false
        }

        currentTaskInfo.walk(this)
        return true
    }
}