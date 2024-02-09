package slayer.task

import org.rspeer.commons.logging.Log
import org.rspeer.event.Subscribe
import org.rspeer.game.component.InterfaceComposite
import org.rspeer.game.component.Interfaces
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
            Log.severe("Out of money to restock")
            return true
        }
        if (ctx.missingItems.isNotEmpty()) {
            Log.severe("Out of ${ctx.missingItems.joinToString()}")
            return true
        }
        if (ctx.died) {
            Interfaces.query(InterfaceComposite.LOGOUT_TAB, InterfaceComposite.WORLD_SELECT)
                .actions("Logout")
                .results()
                .firstOrNull()?.interact("Logout")
            Log.severe("Oh dear, you are dead!")
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