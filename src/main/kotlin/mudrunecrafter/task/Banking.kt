package mudrunecrafter.task

import api.pouch.Pouch
import mudrunecrafter.MudRunecrafter.Companion.inCraftingGuild
import mudrunecrafter.MudRunecrafter.Companion.isTeleporting
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.component.Interfaces
import org.rspeer.game.movement.Movement
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Banking", blocking = true)
class Banking : Task() {
    private var tick = 0

    override fun execute(): Boolean {
        if (!inCraftingGuild()) {
            return false
        }

        // Teleport if ready
        val inventoryReady = isInventoryReady()
        if (inventoryReady) {
            Interfaces.closeSubs()
            if (!isTeleporting()) {
                Backpack.backpack().query().nameContains("Ring of the elements").results().firstOrNull()
                    ?.interact("Last Destination")
            }
            return true
        }

        // Wait for bank to open by OpenBank task
        if (!Bank.isOpen()) {
            tick = 0
            return false
        }

        val bindingNeckEquipped = Equipment.equipment().query().names("Binding necklace").results().isNotEmpty()

        when (tick) {
            0 -> {
                if (!bindingNeckEquipped) {
                    Bank.bank().withdraw("Binding necklace", 1)
                }
                if (Movement.getRunEnergy() <= 25 && !Movement.isStaminaEnhancementActive()) {
                    Bank.bank().withdraw("Stamina potion(1)", 1)
                }
                Bank.bank().withdraw("Pure essence", Int.MAX_VALUE)
            }
            1 -> {
                if (!bindingNeckEquipped) {
                    Backpack.backpack().query().names("Binding necklace").results().firstOrNull()?.interact("Wear")
                }
                Backpack.backpack().query().names("Stamina potion(1)").results().firstOrNull()?.interact("Drink")
                Pouch.COLOSSAL.item()?.interact("Fill")
                Bank.bank().withdraw("Pure essence", Int.MAX_VALUE)
                Pouch.COLOSSAL.item()?.interact("Fill")
            }
            2 -> {
                Bank.bank().depositInventory()
                Bank.bank().withdraw("Pure essence", Int.MAX_VALUE)
                Interfaces.closeSubs()
                Pouch.COLOSSAL.holding = Pouch.COLOSSAL.holdAmount
            }
        }
        tick++
        return true
    }

    private fun isInventoryReady(): Boolean {
        return Backpack.backpack().isFull && Pouch.allPouchesFilled() && Backpack.backpack().query().names("Mud rune")
            .results().isEmpty() && Backpack.backpack().query().names("Pure essence").results().isNotEmpty()
    }
}