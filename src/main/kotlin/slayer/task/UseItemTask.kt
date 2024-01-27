package slayer.task

import org.rspeer.game.component.Inventories
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import slayer.teleportHouse
import javax.inject.Inject

@TaskDescriptor(
    name = "Use item",
    blocking = true,
    blockIfSleeping = true,
)
class UseItemTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    override fun execute(): Boolean {
        val currentTaskInfo = ctx.currentTaskInfo() ?: return false
        val useItems = currentTaskInfo.useItems()

        // No need to use items if we don't have any
        if (useItems.isEmpty()) {
            return false
        }

        val almostDeadSlayerNpc =
            currentTaskInfo.slayerNpcsTargettingMe().firstOrNull { it.healthPercent < 8 }
                ?: return false

        val item = Inventories.backpack().query().nameContains(*useItems).results().firstOrNull()
        if (item == null) {
            teleportHouse()
            return false
        }

        Inventories.backpack().use(item, almostDeadSlayerNpc)
        return true
    }
}