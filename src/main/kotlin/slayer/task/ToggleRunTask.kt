package slayer.task

import org.rspeer.game.movement.Movement
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Toggle run",
)
class ToggleRunTask: Task() {
    override fun execute(): Boolean {
        if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 30) {
            Movement.toggleRun(true)
            return true
        }
        return false
    }
}