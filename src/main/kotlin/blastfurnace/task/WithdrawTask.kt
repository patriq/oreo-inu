package blastfurnace.task

import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.component.EnterInput
import org.rspeer.game.component.Interfaces
import org.rspeer.game.component.Inventories
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder
import org.rspeer.game.config.item.loadout.BackpackLoadout
import org.rspeer.game.movement.Movement
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Withdraw",
    blocking = true
)
class WithdrawTask : Task() {
    private companion object {
        val LOADOUT = BackpackLoadout("withdrawing").apply {
            add(ItemEntryBuilder().key("Ice gloves").quantity(1).build())
            add(ItemEntryBuilder().key("Gold ore").quantity(27).build())
        }
    }

    override fun execute(): Boolean {
        if (!Bank.isOpen()) {
            return false
        }

        if (Movement.getRunEnergy() <= 30 && !Movement.isStaminaEnhancementActive()) {
            val staminaPotion = Inventories.backpack().getItems("Stamina potion(4)").firstOrNull()
            if (staminaPotion == null) {
                Inventories.bank().depositAll { it.names("Gold bar").results() }
                Inventories.bank().withdraw("Stamina potion(4)", 1)
                return true
            } else {
                staminaPotion.interact("Drink")
            }
        }

        LOADOUT.withdraw(Inventories.bank())
        Interfaces.closeSubs()
        EnterInput.asyncClose()
        return true
    }

}