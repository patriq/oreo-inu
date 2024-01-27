package slayer.slayertaskinfo

import api.containsPlayer
import org.rspeer.game.House
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.script.Task
import org.rspeer.game.web.Web
import slayer.ScriptContext
import slayer.data.Constants
import slayer.data.ItemConfig
import slayer.teleportHouse
import slayer.travelFairyZanaris

class MutadedZygomites(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.rectangular(2405, 4384, 2426, 4366, 0)
        private val ZANARIS_AREA = Area.rectangular(2352, 4454, 2431, 4354, 0)
    }

    override fun monsterNames(): Array<String> = arrayOf("Fungi", "Zygomite")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MISSILES)

    override fun items(): List<ItemConfig> {
        return super.items() + listOf(
            ItemConfig(Constants.FUNGICIDE_SPRAY, 5, 1)
        )
    }

    override fun useItems(): Array<String> = Constants.FUNGICIDE_SPRAY

    override fun walk(executingTask: Task) {
        if (ZANARIS_AREA.containsPlayer()) {
            Web.pathTo(Position(2417, 4373, 0))?.step()
            return
        }

        if (House.isInside()) {
            travelFairyZanaris()
            return
        }

        teleportHouse()
    }
}