package slayer.slayertaskinfo

import api.containsPlayer
import org.rspeer.game.component.Inventories
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.area.Area
import org.rspeer.game.script.Task
import slayer.ScriptContext
import slayer.data.ItemConfig

class Elves(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.rectangular(2182, 3265, 2215, 3236, 0)
    }

    private var lastPrayers: Array<Prayer> = arrayOf()

    override fun monsterNames(): Array<String> = arrayOf("Iorwerth Archer", "Iorwerth Warrior")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> {
        val attacking = targetSlayerNpc() ?: return lastPrayers
        lastPrayers = if (attacking.name == "Iorwerth Archer") {
            arrayOf(Prayer.Modern.PROTECT_FROM_MISSILES)
        } else {
            arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)
        }
        return lastPrayers
    }

    override fun items(): List<ItemConfig> {
        return super.items() + listOf(
            ItemConfig(arrayOf("Iorwerth camp teleport"), 1, 0)
        )
    }

    override fun walk(executingTask: Task) {
        val teleport = Inventories.backpack().getItems("Iorwerth camp teleport").firstOrNull() ?: return
        if (teleport.interact("Teleport")) {
            executingTask.sleepUntil({ STANDING_AREA.containsPlayer() }, 5)
        }
    }
}