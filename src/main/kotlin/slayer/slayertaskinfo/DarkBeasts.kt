package slayer.slayertaskinfo

import api.containsPlayer
import api.teleport.ConstructionCape
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.movement.Movement
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.web.Web
import slayer.ScriptContext
import slayer.data.Bracelet
import slayer.data.ItemConfig
import slayer.data.Settings
import slayer.teleportConstructionCape

class DarkBeasts(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.rectangular(3242, 12404, 3212, 12383, 0)
        private val OUTSIDE_AREA = Area.rectangular(3249, 6083, 3217, 6035, 0)
        private val CAVE_AREA = Area.rectangular(3142, 12482, 3267, 12353, 0)
        private val BEFORE_SHORTCUT_AREA = Area.rectangular(3219, 12447, 3231, 12433, 0)
    }

    override fun monsterNames(): Array<String> = arrayOf("Dark beast")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)

    override fun bracelet(): Bracelet = Bracelet.EXPEDITIOUS

    override fun walk(executingTask: Task) {
        if (CAVE_AREA.containsPlayer()) {
            if (BEFORE_SHORTCUT_AREA.containsPlayer()) {
                SceneObjects.query().ids(36692).results().nearest()?.interact("Pass")
                return
            }

            Web.pathTo(Position(3225, 12394, 0))?.step()
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