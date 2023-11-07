package gotr.task.minigame.blocking

import gotr.MinigameContext.Companion.insideAltar
import gotr.MinigameState
import gotr.task.minigame.MinigameTask
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Leave altar", blocking = true, blockIfSleeping = true)
class LeaveAltar : MinigameTask() {
    override fun minigameExecute(): Boolean {
        if (context.state == MinigameState.CRAFT_CHARGED_CELL
            || context.state == MinigameState.CRAFT_GUARDIAN_STONES
        ) {
            return false
        }

        if (!insideAltar()) {
            return false
        }

        // Leave altar
        val exit = SceneObjects.query().names("Portal").results().nearest() ?: return false
        return exit.interact("Use")
    }
}