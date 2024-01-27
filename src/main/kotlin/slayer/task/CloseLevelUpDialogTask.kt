package slayer.task

import org.rspeer.game.component.Dialog
import org.rspeer.game.component.InterfaceComposite
import org.rspeer.game.movement.Movement
import org.rspeer.game.scene.Players
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Skip level up dialog",
    blocking = true,
    blockIfSleeping = true,
)
class CloseLevelUpDialogTask: Task() {
    override fun execute(): Boolean {
        if (Dialog.getOpenType(false) == InterfaceComposite.LEVEL_UP_DIALOG) {
            Movement.walkTowards(Players.self())
            return true
        }
        return false
    }
}