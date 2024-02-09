package slayer.listener

import org.rspeer.commons.math.Random
import org.rspeer.event.Subscribe
import org.rspeer.game.event.ChatMessageEvent
import slayer.ScriptContext
import slayer.data.Constants.CHAT_CANCEL_MESSAGE
import slayer.data.Constants.CHAT_GEM_COMPLETE_MESSAGE
import slayer.data.Constants.CHAT_GEM_PROGRESS_MESSAGE
import slayer.data.SlayerTask

class SlayerChatListener(private val ctx: ScriptContext) {
    @Subscribe
    fun onChatMessage(message: ChatMessageEvent) {
        if (message.type != ChatMessageEvent.Type.GAME) {
            return
        }

        // Get the message
        val messageText = message.contents

        // Check if we died
        if (messageText.contains("Oh dear, you are dead!")) {
            ctx.died = true
            return
        }

        // Check for superiors
        if (messageText.contains("A superior foe has appeared")) {
            ctx.superiorSpawned()
            return
        }

        // Slayer task finished
        if (messageText.contains("; return to a Slayer master.")) {
            // Simulate that we missed the chat message by scheduling removal after some milliseconds
            ctx.removeTaskAfterMillis(Random.nextLong(6000, 10000))
        }

        // No slayer assignment or skipped
        if (messageText == CHAT_GEM_COMPLETE_MESSAGE || messageText == CHAT_CANCEL_MESSAGE) {
            ctx.removeTask()
        }

        // If contains the task
        val match = CHAT_GEM_PROGRESS_MESSAGE.find(messageText)
        if (match != null) {
            // Parse the text and set the task
            val name = match.groupValues[1]
            val amount = match.groupValues[2].toInt()
            val slayerTask = SlayerTask.fromTaskName(name)
            ctx.setTask(slayerTask)
        }
    }
}