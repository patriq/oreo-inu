package gotr.task

import gotr.MinigameContext
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Enter minigame")
class EnterMinigame : Task() {
    private companion object {
        private const val MINIGAME_MAIN_REGION = 14484
        private const val OPEN_BARRIED_ID = 43700
    }

    override fun execute(): Boolean {
        if (!MinigameContext.isInsideMinigame() && Players.self().position.regionId == MINIGAME_MAIN_REGION) {
            val barrier = SceneObjects.query().ids(OPEN_BARRIED_ID).results().nearest() ?: return false
            return barrier.interact("Pass")
        }
        return false
    }
}