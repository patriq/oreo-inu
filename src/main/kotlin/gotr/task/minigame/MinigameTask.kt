package gotr.task.minigame

import gotr.MinigameContext
import org.rspeer.game.script.Task
import javax.inject.Inject

abstract class MinigameTask : Task() {
    @Inject
    protected lateinit var context: MinigameContext

    final override fun execute(): Boolean {
        if (!MinigameContext.isInsideMinigame()) {
            return false
        }
        return minigameExecute()
    }

    abstract fun minigameExecute(): Boolean
}