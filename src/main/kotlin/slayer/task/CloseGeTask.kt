package slayer.task

import org.rspeer.game.component.InterfaceComposite
import org.rspeer.game.component.Interfaces
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Closing GE",)
class CloseGeTask: Task() {
    override fun execute(): Boolean {
        if (Interfaces.isSubActive(InterfaceComposite.GRAND_EXCHANGE)) {
            Interfaces.closeSubs()
            return true
        }

        return false
    }
}