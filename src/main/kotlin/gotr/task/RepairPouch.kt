package gotr.task

import api.pouch.Pouch
import org.rspeer.game.component.Dialog
import org.rspeer.game.component.tdi.Magic
import org.rspeer.game.component.tdi.Spell
import org.rspeer.game.scene.Players
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Repair pouch", blocking = true, blockIfSleeping = true)
@OptIn(ExperimentalStdlibApi::class)
class RepairPouch : Task() {
    override fun execute(): Boolean {
        if (Pouch.entries.none { it.degraded }) {
            return false
        }

        // Wait for dialog to pop
        if (isNpcContacting()) {
            return true
        }

        // Wait process dialog
        if (Dialog.getOpenType() != null || Dialog.isProcessing() || Dialog.isViewingChatOptions()) {
            Dialog.process(1)
            return true
        }

        // Cast repair pouch spell
        Magic.interact(Spell.Lunar.NPC_CONTACT, "Dark Mage")
        return true
    }

    private fun isNpcContacting(): Boolean {
        return Players.self().animationId == 4413
    }
}