package slayer.slayertaskinfo

import api.teleport.NexusPortal
import org.rspeer.game.House
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.area.Area
import org.rspeer.game.script.Task
import slayer.ScriptContext
import slayer.data.Bracelet
import slayer.dungeon.KourendCatacombs
import slayer.teleportHouse
import slayer.travelNexusPortal

class Dagannoth(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    override fun monsterNames(): Array<String> = arrayOf("Dagannoth")

    override fun standingArea(): Area = KourendCatacombs.Location.DAGANNOTHS_SOUTH.area

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)

    override fun bracelet(): Bracelet = Bracelet.SLAUGHTER

    override fun walk(executingTask: Task) {
        if (KourendCatacombs.outsideMainEntrance() || KourendCatacombs.isInsideDungeon()) {
            KourendCatacombs.walk(KourendCatacombs.Location.DAGANNOTHS_SOUTH)
            return
        }

        if (House.isInside()) {
            travelNexusPortal(NexusPortal.Location.KOUREND_CASTLE)
            return
        }

        teleportHouse()
    }
}