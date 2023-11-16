package mudrunecrafter.task

import api.Animations.isNpcContacting
import api.pouch.Pouch
import api.service.ItemChargeService
import mudrunecrafter.MudRunecrafter.Companion.inCraftingGuild
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.component.Dialog
import org.rspeer.game.component.Interfaces
import org.rspeer.game.component.tdi.Magic
import org.rspeer.game.component.tdi.Spell
import org.rspeer.game.movement.Movement
import org.rspeer.game.position.Position
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import javax.inject.Inject

@TaskDescriptor(name = "Walking to bank", blocking = true, blockIfSleeping = true)
class OpenBank @Inject constructor(private val itemChargeService: ItemChargeService) : Task() {
    private companion object {
        private val NEAR_BANK_POSITION = Position(2936, 3281, 0)
    }

    override fun execute(): Boolean {
        if (!inCraftingGuild()) {
            return false
        }

        val needToRepairPouch = Pouch.COLOSSAL.degraded
        var needToDestroyBindingNecklace = itemChargeService.bindingNecklaceCharges == 1

        // Destroy binding necklace if it has 1 charge
        if (needToDestroyBindingNecklace) {
            Equipment.equipment().query().names("Binding necklace").results().firstOrNull()?.interact("Remove")
            Backpack.backpack().query().names("Binding necklace").results().firstOrNull()?.interact("Destroy")

            val yesButton = Interfaces.getDirect(584, 1)
            if (yesButton != null && yesButton.isVisible) {
                yesButton.interact("Yes")
                needToDestroyBindingNecklace = false
            }
        }

        // Repair pouches
        if (needToRepairPouch) {
            let {
                if (Players.self().position != NEAR_BANK_POSITION) {
                    return@let
                }

                if (Dialog.canContinue()) {
                    Dialog.processContinue()
                    return@let
                }

                // Wait process dialog
                if (Dialog.isProcessing() || Dialog.isViewingChatOptions()) {
                    Dialog.process(1)
                    return@let
                }

                if (isNpcContacting()) {
                    return@let
                }

                // Cast repair pouch spell
                Magic.interact(Spell.Lunar.NPC_CONTACT, "Dark Mage")
            }
        }

        // If we need any actions, walk, else open bank
        if (!needToRepairPouch && !needToDestroyBindingNecklace) {
            val bankChest = SceneObjects.query().names("Bank chest").results().nearest()
            if (bankChest != null) {
                bankChest.interact("Use")
                sleepUntil({ Bank.isOpen() }, 4)
            } else {
                Movement.walkTowards(NEAR_BANK_POSITION)
            }
        } else {
            if (Players.self().position != NEAR_BANK_POSITION) {
                Movement.walkTowards(NEAR_BANK_POSITION)
            }
        }
        return true
    }
}