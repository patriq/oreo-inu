package slayer.slayertaskinfo

import api.containsPlayer
import api.teleport.ConstructionCape
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.web.Web
import slayer.ScriptContext
import slayer.data.Settings.KURASK_GEAR
import slayer.teleportConstructionCape

class Kurasks(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.rectangular(3265, 12395, 3243, 12365, 0)
        private val CAVE_AREA = Area.rectangular(3142, 12482, 3267, 12353, 0)
        private val OUTSIDE_AREA = Area.rectangular(3249, 6083, 3217, 6035, 0)
    }

    override fun monsterNames(): Array<String> = arrayOf("Kurask")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)

    override fun equipment(): Map<Equipment.Slot, String> = KURASK_GEAR

    override fun walk(executingTask: Task) {
        if (CAVE_AREA.containsPlayer()) {
            Web.pathTo(Position(3254, 12384, 0))?.step()
            return
        }

        if (OUTSIDE_AREA.containsPlayer()) {
            val cave = SceneObjects.query().names("Cave").results().nearest()
            if (cave != null) {
                cave.interact("Enter")
            } else {
                Web.pathTo(Position(3225, 6046, 0))?.step()
            }
            return
        }

        teleportConstructionCape(ConstructionCape.Location.PRIFFDINAS)
    }
}