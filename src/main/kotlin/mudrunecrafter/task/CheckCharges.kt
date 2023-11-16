package mudrunecrafter.task

import api.pouch.Pouch
import api.service.ItemChargeService
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import javax.inject.Inject

@TaskDescriptor(name = "Check charges", blocking = true)
class CheckCharges @Inject constructor(private val itemChargeService: ItemChargeService) : Task() {
    override fun execute(): Boolean {
        // Check pouches
        if (Pouch.COLOSSAL.unknown) {
            if (Bank.isOpen()) {
                return false
            }
            Pouch.COLOSSAL.item()?.interact("Check")
        }

        // Check binding necklace if we have one
        if (itemChargeService.bindingNecklaceCharges == -1) {
            if (Bank.isOpen()) {
                return false
            }
            val bindingNecklaceBackpack = Backpack.backpack().query().names("Binding necklace").results().firstOrNull()
            val bindingNecklaceEquipment =
                Equipment.equipment().query().names("Binding necklace").results().firstOrNull()
            bindingNecklaceBackpack?.interact("Check") == true || bindingNecklaceEquipment?.interact("Check") == true
        }
        return false
    }
}