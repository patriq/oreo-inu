package slayer.slayertaskinfo

import api.containsPlayer
import org.rspeer.game.House
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.script.Task
import org.rspeer.game.web.Web
import slayer.ScriptContext
import slayer.data.ItemConfig
import slayer.teleportHouse
import slayer.travelFairyRing

class Gargoyles(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.rectangular(3448, 9952, 3427, 9926, 3)
        private val OUTSIDE_AREA = Area.rectangular(3456, 3553, 3402, 3468, 0)
        private val BASEMENT_AREA = Area.rectangular(3449, 9978, 3398, 9924, 3)
    }

    override fun monsterNames(): Array<String> = arrayOf("Gargoyle")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)

    override fun items(): List<ItemConfig> = super.items() + listOf(
        ItemConfig(arrayOf("Rock hammer"), 1, 1),
    )

    override fun useItems(): Array<String> = arrayOf("Rock hammer")

    override fun walk(executingTask: Task) {
        if (BASEMENT_AREA.containsPlayer() || OUTSIDE_AREA.containsPlayer()) {
            Web.pathTo(Position(3436, 9941, 3))?.step()
            return
        }

        if (House.isInside()) {
            travelFairyRing("CKS")
            return
        }

        teleportHouse()
    }
}