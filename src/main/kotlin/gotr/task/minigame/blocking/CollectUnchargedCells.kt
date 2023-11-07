package gotr.task.minigame.blocking

import gotr.MinigameContext
import gotr.MinigameState
import gotr.task.minigame.MinigameTask
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.component.EnterInput
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Collect uncharged cells", blocking = true)
class CollectUnchargedCells : MinigameTask() {
    override fun minigameExecute(): Boolean {
        if (context.state != MinigameState.COLLECT_UNCHARGED_CELLS) {
            return false
        }

        // Drop charged cells if backpack is full
        if (Backpack.backpack().isFull) {
            Backpack.backpack().query().names(MinigameContext.GUARDIAN_ESSENCE_NAME).results().first().interact("Drop")
        }

        // Collect uncharged cells
        if (EnterInput.isOpen()) {
            EnterInput.initiate(10)
            return true
        } else {
            val unchargedCells = SceneObjects.query().names("Uncharged cells").results().nearest() ?: return false
            return unchargedCells.interact("Take")
        }
    }
}