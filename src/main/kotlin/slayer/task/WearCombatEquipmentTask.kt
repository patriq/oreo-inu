package slayer.task

import api.containsPlayer
import api.equipEach
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import javax.inject.Inject

@TaskDescriptor(
    name = "Wear combat equipment",
)
class WearCombatEquipmentTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    override fun execute(): Boolean {
        val currentTaskInfo = ctx.currentTaskInfo() ?: return false
        val standingArea = currentTaskInfo.standingArea()
        if (!standingArea.containsPlayer()) {
            return false
        }

        // Wear combat equipment if not already worn
        val gearLoadout = currentTaskInfo.equipmentLoadout()
        if (gearLoadout.isWorn) {
            return false
        }

        gearLoadout.equipEach()
        return true
    }
}