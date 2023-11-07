package gotr.task.minigame.blocking

import gotr.MinigameContext.Companion.isInsideLargeRemainsMiningArea
import gotr.MinigameState
import gotr.task.minigame.MinigameTask
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Leave large fragment mine", blocking = true, blockIfSleeping = true)
class LeaveLargeFragmentMine : MinigameTask() {
    override fun minigameExecute(): Boolean {
        if (context.state == MinigameState.MINE_INITIAL_FRAGMENTS) {
            return false
        }

        if (!isInsideLargeRemainsMiningArea()) {
            return false
        }

        // Leave the mine
        val ladder = SceneObjects.query().names("Rubble").results().nearest() ?: return false
        val result = ladder.interact("Climb")
        if (result) {
            sleepWhile({ Players.self().isMoving }, 3)
        }
        return result
    }
}
