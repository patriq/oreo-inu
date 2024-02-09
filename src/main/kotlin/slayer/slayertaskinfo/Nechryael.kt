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

class Nechryael(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.polygonal(
            Position(3220, 12434, 0),
            Position(3234, 12426, 0),
            Position(3234, 12410, 0),
            Position(3218, 12406, 0),
            Position(3209, 12413, 0),
            Position(3209, 12429, 0),
        )
        private val OUTSIDE_AREA = Area.rectangular(3249, 6083, 3217, 6035, 0)
        private val CAVE_AREA = Area.rectangular(3142, 12482, 3267, 12353, 0)
        private val BEFORE_SHORTCUT_AREA = Area.rectangular(3219, 12447, 3231, 12433, 0)
    }

    override fun monsterNames(): Array<String> = arrayOf("Greater Nechryael")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)

    override fun items(): List<ItemConfig> = listOf(
        ItemConfig(Settings.COMBAT_BOOSTING_POTION.allDoseNames, 1, 0),
        ItemConfig(Settings.PRAYER_POTION.allDoseNames, 5, 0),
        ItemConfig(intArrayOf(Settings.FOOD), 10, if (ctx.isSuperiorAlive()) 6 else 0),
    )

    override fun bracelet(): Bracelet = Bracelet.SLAUGHTER

    override fun walk(executingTask: Task) {
        if (CAVE_AREA.containsPlayer()) {
            if (BEFORE_SHORTCUT_AREA.containsPlayer()) {
                SceneObjects.query().ids(36692).results().nearest()?.interact("Pass")
                return
            }

            Movement.walkTowards(Position(3222, 12421, 0))
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