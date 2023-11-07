package gotr.task.minigame

import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Drop portal talisman")
class DropPortalTalisman : MinigameTask() {
    override fun minigameExecute(): Boolean {
        val talisman =
            Backpack.backpack().query().nameContains("Portal talisman").results().firstOrNull() ?: return false
        return talisman.interact("Drop")
    }
}