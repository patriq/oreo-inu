package gotr.task.minigame.blocking

import gotr.MinigameContext.Companion.containsGuardianStones
import gotr.MinigameState
import gotr.task.minigame.MinigameTask
import org.rspeer.game.scene.Npcs
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Feed guardian", blocking = true)
class FeedGuardian : MinigameTask() {
    override fun minigameExecute(): Boolean {
        if (context.state != MinigameState.FEED_GUARDIAN_STONES_AND_PLACE_BARRIER_CELL) {
            return false
        }

        if (!containsGuardianStones()) {
            return false
        }

        // Feed guardian
        val guardian = Npcs.query().names("The Great Guardian").results().nearest() ?: return false
        return guardian.interact("Power-up")
    }
}