package slayer.task

import org.rspeer.commons.logging.Log
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import javax.inject.Inject


@TaskDescriptor(stoppable = true)
class StopTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    override fun execute(): Boolean {
        if (ctx.hasTask() && ctx.shouldBlockTask()) {
            Log.severe("Block task")
            return true
        }
        if (ctx.hasInvalidTask() || (ctx.hasTask() && !ctx.shouldSkipTask() && !ctx.isTaskSupported())) {
            Log.severe("The current task is not supported")
            return true
        }
        return false
    }
}