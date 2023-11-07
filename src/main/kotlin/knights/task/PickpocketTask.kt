package knights.task

import knights.ArdyKnights.Companion.isPlayerStunned
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.Npcs
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Pickpocketing!",
)
class PickpocketTask: Task() {
    override fun execute(): Boolean {
        if (isPlayerStunned()) {
            return false
        }
        val knight = Npcs.query().within(Area.rectangular(2649, 3287, 2658, 3280, 0))
            .names("Knight of Ardougne").results().firstOrNull() ?: return false
        return knight.interact("Pickpocket")
    }
}