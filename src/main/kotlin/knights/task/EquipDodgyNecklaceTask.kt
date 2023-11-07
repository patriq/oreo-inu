package knights.task

import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.adapter.component.inventory.Inventory
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Equipping dodgy necklace!",
)
class EquipDodgyNecklaceTask : Task() {
    override fun execute(): Boolean {
        var dodgyNecklace = Equipment.equipment().query().names("Dodgy necklace").results().firstOrNull()
        if (dodgyNecklace != null) {
            return false
        }
        dodgyNecklace = Inventory.backpack().query().names("Dodgy necklace").results().firstOrNull()
        if (dodgyNecklace == null) {
            return false
        }
        return dodgyNecklace.interact("Wear")
    }
}