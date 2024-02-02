package slayer.task

import org.rspeer.commons.logging.Log
import org.rspeer.event.Subscribe
import org.rspeer.game.event.ChatMessageEvent
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import javax.inject.Inject


@TaskDescriptor(stoppable = true, priority = 2000, register = true)
class StopScriptTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    private var outOfMoney = false

    override fun execute(): Boolean {
        if (ctx.hasTask() && ctx.shouldBlockTask()) {
            Log.severe("Block task")
            return true
        }
        if (ctx.hasInvalidTask() || (ctx.hasTask() && !ctx.shouldSkipTask() && !ctx.isTaskSupported())) {
            Log.severe("The current task is not supported")
            return true
        }
        if (outOfMoney) {
            Log.severe("Out of money")
            return true
        }
        return false
    }

    @Subscribe(async = true)
    fun onChatMessage(event: ChatMessageEvent) {
        if (event.contents.contains("You haven't got enough.", true)) {
            outOfMoney = true
        }
    }
}