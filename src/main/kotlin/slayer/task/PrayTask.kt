package slayer.task

import api.containsPlayer
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.component.tdi.Prayers
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import javax.inject.Inject

@TaskDescriptor(
    name = "Pray",
)
class PrayTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    override fun execute(): Boolean {
        if (Prayers.getPoints() == 0) {
            return false
        }

        val shouldTurnOn = ctx.currentTaskInfo()?.standingArea()?.containsPlayer() ?: false
        if (shouldTurnOn) {
            val prayers = ctx.currentTaskInfo()?.prayers() ?: return false
            if (Prayers.isActive(*prayers)) {
                return false
            }
            return Prayers.select(true, *prayers)
        } else {
            if (Prayer.Modern.values().none { Prayers.isActive(it) }) {
                return false
            }
            return Prayers.select(false, *Prayer.Modern.values())
        }
    }
}