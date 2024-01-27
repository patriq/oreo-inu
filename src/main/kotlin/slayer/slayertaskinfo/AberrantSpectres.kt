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
import slayer.teleportHouse
import slayer.travelFairyRing

class AberrantSpectres(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.polygonal(
            Position(3420, 3555, 1),
            Position(3420, 3547, 1),
            Position(3432, 3547, 1),
            Position(3432, 3542, 1),
            Position(3446, 3542, 1),
            Position(3446, 3555, 1),
        )
        private val OUTSIDE_AREA = Area.rectangular(3456, 3553, 3402, 3468, 0)
    }

    override fun monsterNames(): Array<String> = arrayOf("Aberrant spectre")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MAGIC)

    override fun walk(executingTask: Task) {
        val chain = SceneObjects.query().names("Spikey chain").results().nearest()
        if (chain != null) {
            chain.interact("Climb-up")
            return
        }

        if (OUTSIDE_AREA.containsPlayer()) {
            Web.pathTo(Position(3421, 3548, 0))?.step()
            return
        }

        if (House.isInside()) {
            travelFairyRing("CKS")
            return
        }

        teleportHouse()
    }
}