package blastfurnace.task

import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.component.EnterInput
import org.rspeer.game.component.Interfaces
import org.rspeer.game.component.Inventories
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder
import org.rspeer.game.config.item.loadout.BackpackLoadout
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Bank",
    blocking = true
)
class OpenBankTask: Task() {
    override fun execute(): Boolean {
        if (Inventories.backpack().getItems("Gold bar").isEmpty() || Bank.isOpen()) {
            return false
        }
        Bank.open()
        return true
    }
}