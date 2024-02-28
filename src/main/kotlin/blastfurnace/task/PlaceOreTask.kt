package blastfurnace.task

import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.component.Inventories
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Place Ore",
    blocking = true
)
class PlaceOreTask: Task() {
    override fun execute(): Boolean {
        if (Inventories.backpack().getItems("Gold ore").isEmpty() || !Inventories.backpack().isFull) {
            return false
        }

        val belt = SceneObjects.query().names("Conveyor belt").actions("Put-ore-on").results().nearest() ?: return false
        belt.interact("Put-ore-on")
        return true
    }
}