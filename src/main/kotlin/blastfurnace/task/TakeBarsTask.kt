package blastfurnace.task

import org.rspeer.commons.logging.Log
import org.rspeer.event.Subscribe
import org.rspeer.game.Game
import org.rspeer.game.Keyboard
import org.rspeer.game.component.Dialog
import org.rspeer.game.component.InterfaceComposite
import org.rspeer.game.component.Inventories
import org.rspeer.game.component.Production
import org.rspeer.game.event.OpenSubInterfaceEvent
import org.rspeer.game.movement.Movement
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Take bars",
    blocking = true,
    register = true
)
class TakeBarsTask: Task() {
    override fun execute(): Boolean {
        val production = Production.getActive()
        if (production != null) {
            production.initiate(0)
            Inventories.backpack().getItems("Goldsmith gauntlets").firstOrNull()?.interact("Wear")
            return true
        }

        // Equip ice gloves
        Inventories.backpack().getItems("Ice gloves").firstOrNull()?.interact("Wear")
        SceneObjects.query().names("Bar dispenser").actions("Take").results().nearest()?.interact("Take")
        return true
    }
}