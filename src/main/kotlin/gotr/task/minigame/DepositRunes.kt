package gotr.task.minigame

import gotr.MinigameState
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Deposit runes")
class DepositRunes : MinigameTask() {
    override fun minigameExecute(): Boolean {
        if (context.state != MinigameState.DEPOSIT_RUNES) {
            return false
        }

        // Deposit runes
        val depositPool = SceneObjects.query().names("Deposit Pool").results().nearest() ?: return false
        return depositPool.interact("Deposit-runes")
    }
}