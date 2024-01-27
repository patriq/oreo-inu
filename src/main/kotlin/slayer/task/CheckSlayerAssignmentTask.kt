package slayer.task

import org.rspeer.game.component.Inventories
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import slayer.data.Constants
import javax.inject.Inject

@TaskDescriptor(
    name = "Check slayer assignment",
    blocking = true,
)
class CheckSlayerAssignmentTask @Inject constructor(private val ctx: ScriptContext): Task() {
    override fun execute(): Boolean {
        if (ctx.getTask() != slayer.data.SlayerTask.UNKNOWN) {
            return false
        }

        // Check task with helm, followed by gem
        val slayerItem = Inventories.equipment().query().names(Constants.SLAYER_HELM).results().firstOrNull() ?: return true

        // Check task
        slayerItem.interact("Check")
        return true
    }
}