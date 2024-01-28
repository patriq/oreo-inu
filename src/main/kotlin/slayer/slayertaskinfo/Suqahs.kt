package slayer.slayertaskinfo

import api.containsPlayer
import api.teleport.NexusPortal
import org.rspeer.game.House
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.script.Task
import org.rspeer.game.web.Web
import slayer.ScriptContext
import slayer.data.Bracelet
import slayer.data.ItemConfig
import slayer.data.Settings
import slayer.data.Settings.FOOD
import slayer.teleportHouse
import slayer.travelNexusPortal

class Suqahs(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.rectangular(2110, 3950, 2122, 3937, 0)
        private val LUNAR_ISLE_AREA = Area.rectangular(2121, 3902, 2103, 3954, 0)
    }

    override fun monsterNames(): Array<String> = arrayOf("Suqah")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MAGIC)

    override fun items(): List<ItemConfig> = listOf(
        ItemConfig(Settings.COMBAT_BOOSTING_POTION.allDoseNames, 1, 0),
        ItemConfig(Settings.PRAYER_POTION.allDoseNames, 5, 0),
        ItemConfig(intArrayOf(FOOD), 15, 0),
    )

    override fun bracelet(): Bracelet = Bracelet.EXPEDITIOUS

    override fun walk(executingTask: Task) {
        if (LUNAR_ISLE_AREA.containsPlayer()) {
            Web.pathTo(Position(2114, 3947, 0))?.step()
            return
        }

        if (House.isInside()) {
            travelNexusPortal(NexusPortal.Location.LUNAR_ISLE)
            return
        }

        teleportHouse()
    }
}