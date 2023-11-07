package knights.task

import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Banking!",
    blocking = true
)
class BankTask : Task() {
    override fun execute(): Boolean {
        if (Backpack.backpack().query().names("Dodgy necklace").results().isNotEmpty()
            && Backpack.backpack().query().names("Jug of wine").results().isNotEmpty()
        ) {
            return false
        }

        if (!Bank.isOpen()) {
            Backpack.backpack().query().names("Coin pouch").results().firstOrNull()?.interact("Open-all")
            Bank.open()
            return true
        }

        if (!Backpack.backpack().isEmpty) {
            Bank.bank().depositInventory()
            return true
        }

        Bank.bank().withdraw("Dodgy necklace", 5)
        Bank.bank().withdraw("Jug of wine", 21)
        return false
    }
}