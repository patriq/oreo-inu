package slayer.slayertaskinfo

import api.containsPlayer
import org.rspeer.game.House
import org.rspeer.game.combat.Combat
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.script.Task
import org.rspeer.game.web.Web
import slayer.ScriptContext
import slayer.data.ItemConfig
import slayer.data.Settings.ANTI_POISON_POTION
import slayer.teleportHouse
import slayer.travelFairyRing

class Kalphite(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.rectangular(3295, 9543, 3322, 9517, 0)
        private val CAVE_AREA = Area.rectangular(3262, 9553, 3342, 9473, 0)
        private val OUTSIDE_AREA = Area.rectangular(3329, 3125, 3244, 3088, 0)
    }

    override fun monsterNames(): Array<String> = arrayOf("Kalphite Soldier")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)

    override fun items(): List<ItemConfig> = super.items() + listOf(
        ItemConfig(ANTI_POISON_POTION.allDoseNames, 2, if (Combat.isPoisoned()) 1 else 0)
    )

    override fun walk(executingTask: Task) {
        // Use web walking to get to the cave area when we are already at the desert.
        if (CAVE_AREA.containsPlayer() || OUTSIDE_AREA.containsPlayer()) {
            Web.pathTo(Position(3308, 9525, 0))?.step()
            return
        }

        // Use house fairy ring to get to the desert.
        if (House.isInside()) {
            travelFairyRing("BIQ")
            return
        }

        teleportHouse()
    }
}