import org.rspeer.commons.StopWatch
import org.rspeer.event.Subscribe
import org.rspeer.game.Game
import org.rspeer.game.component.Interfaces
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.event.TickEvent
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Script
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme
import java.util.function.Supplier


@ScriptMeta(
    developer = "Oreo",
    name = "Prif Agility",
    desc = "Does agility in Prifddinas",
    version = 1.0,
    paint = PaintScheme::class,
    regions = [-3]
)
class PrifAgility : Script() {
    companion object {
        private val OBSTACLES = listOf(
            Obstacle("Dark hole", Area.rectangular(3267, 6120, 3271, 6115, 0)),
            Obstacle("Ladder", Area.rectangular(3235, 6120, 3277, 6100, 0)),
            Obstacle("Tightrope", Area.rectangular(3254, 6112, 3259, 6101, 2)),
            Obstacle("Chimney", Area.rectangular(3270, 6107, 3276, 6101, 2)),
            Obstacle("Roof edge", Area.rectangular(3266, 6116, 3272, 6109, 2)),
            Obstacle("Ladder", Area.rectangular(2267, 3395, 2272, 3386, 0)),
            Obstacle("Rope bridge", Area.rectangular(2264, 3394, 2270, 3388, 2)),
            Obstacle("Tightrope", Area.rectangular(2253, 3391, 2259, 3385, 2)),
            Obstacle("Ladder", Area.rectangular(3265, 6149, 3278, 6139, 0)),
            Obstacle("Rope bridge", Area.rectangular(2242, 3399, 2248, 3393, 2)),
            Obstacle("Tightrope", Area.rectangular(2243, 3410, 2249, 3404, 2)),
            Obstacle("Tightrope", Area.rectangular(2248, 3420, 2254, 3414, 2)),
            Obstacle("Dark hole", Area.rectangular(2257, 3438, 2266, 3422, 0)),
        )
    }

    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.AGILITY)

    @PaintBinding("Obstacle")
    private val obstacle = Supplier<String> { OBSTACLES.firstOrNull { it.atLocation() }?.name ?: "Unknown" }

    private var lastObstacle: Obstacle? = null

    @Subscribe
    fun onTick(event: TickEvent) {
        if (Game.getClient().rootInterfaceIndex == -1) {
            return
        }

        val overlay = Interfaces.getDirect(174, 1)
        if (overlay != null && overlay.provider.alpha < 75) {
            return
        }

        val obstacle = OBSTACLES.firstOrNull { it.atLocation() } ?: return

        // Don't click the obstacle if we're already moving towards it
        if (obstacle == lastObstacle) {
            if (Players.self().isMoving) {
                return
            }
        }

        // Priotize clicking the portal if it's available, otherwise click the obstacle
        SceneObjects.query().names(obstacle.name, "Portal").within(obstacle.area).results()
            .minByOrNull { it.name != "Portal" }?.interact { true }
        lastObstacle = obstacle
    }

    override fun loop(): Int {
        return 20000
    }
}

class Obstacle(val name: String, val area: Area) {
    fun atLocation(): Boolean = area.contains(Players.self().position)
}