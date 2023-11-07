package knights.task

import org.rspeer.commons.math.Random
import org.rspeer.game.adapter.component.inventory.Inventory
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Opening pouch!",
)
class OpenPouchTask : Task() {
    override fun execute(): Boolean {
        val coinPouch = Inventory.backpack().query().names("Coin pouch").results().firstOrNull() ?: return false
        if (coinPouch.stackSize == 28 || coinPouch.stackSize > Random.nextInt(20)) {
            coinPouch.interact("Open-all")
            return true
        }
        return false
    }
}