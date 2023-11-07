package knights.task

import org.rspeer.commons.logging.Log
import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Missing resources!",
    stoppable = true
)
class StopTask : Task() {
    override fun execute(): Boolean {
        if (Bank.isOpen()) {
            val missing = Bank.bank().query().names("Dodgy necklace").results().isEmpty()
                    || Bank.bank().query().names("Jug of wine").results().isEmpty()
            Log.severe("Missing resources!")
            return missing
        }
        return false
    }
}