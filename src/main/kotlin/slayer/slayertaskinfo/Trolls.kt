package slayer.slayertaskinfo

import api.containsPlayer
import org.rspeer.game.House
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.web.Web
import slayer.ScriptContext
import slayer.data.Bracelet
import slayer.data.Constants.XERICS_TALISMAN
import slayer.teleportHouse

class Trolls(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.rectangular(1257, 3507, 1230, 3525, 0)
        private val OUTSIDE_AREA = Area.rectangular(1207, 3575, 1278, 3507, 0)
    }

    override fun monsterNames(): Array<String> = arrayOf("Mountain troll")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)

    override fun bracelet(): Bracelet = Bracelet.EXPEDITIOUS

    override fun walk(executingTask: Task) {
        if (OUTSIDE_AREA.containsPlayer()) {
            Web.pathTo(Position(1246, 3511, 0))?.step()
            return
        }

        if (House.isInside()) {
            val talisman  = SceneObjects.query().names(XERICS_TALISMAN).results().nearest() ?: return
            talisman.interact("Honour")
            executingTask.sleepUntil({ !House.isInside() }, 5)
            return
        }

        teleportHouse()
    }
}