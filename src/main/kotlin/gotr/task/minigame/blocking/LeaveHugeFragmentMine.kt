package gotr.task.minigame.blocking

import gotr.MinigameContext
import gotr.MinigameContext.Companion.isInsideHugeRemainsMiningArea
import gotr.MinigameState
import gotr.task.minigame.MinigameTask
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Leave huge fragment mine", blocking = true)
class LeaveHugeFragmentMine : MinigameTask() {
    override fun minigameExecute(): Boolean {
        if (context.state == MinigameState.MINE_HUGE_FRAGMENTS) {
            return false
        }

        if (!isInsideHugeRemainsMiningArea()) {
            return false
        }

        // If inside huge remains mining area, leave
        val portal = SceneObjects.query().names("Portal")
            .within(MinigameContext.HUGE_REMAINS_MINING_AREA).results().nearest() ?: return false
        return portal.interact("Enter")
    }
}