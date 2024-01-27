package slayer.slayertaskinfo

import org.rspeer.game.House
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.area.Area
import org.rspeer.game.script.Task
import slayer.ScriptContext
import slayer.dungeon.ChasmOfFire
import slayer.teleportHouse
import slayer.travelFairyRing

class BlackDemons(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    override fun monsterNames(): Array<String> = arrayOf("Black demon")

    override fun standingArea(): Area = ChasmOfFire.getArea(ChasmOfFire.Location.BLACK_DEMONS)

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)

    override fun walk(executingTask: Task) {
        if (ChasmOfFire.isInsideDungeon()) {
            ChasmOfFire.walk(ChasmOfFire.Location.BLACK_DEMONS)
            return
        }

        if (ChasmOfFire.isOutside()) {
            ChasmOfFire.enter()
            return
        }

        if (House.isInside()) {
            travelFairyRing("DJR")
            return
        }

        teleportHouse()
    }
}