package mudrunecrafter.task

import mudrunecrafter.MudRunecrafter.Companion.isInWaterAltar
import mudrunecrafter.MudRunecrafter.Companion.isOutsideAltar
import org.rspeer.game.movement.Movement
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Walk to altar", blocking = true)
class WalkToAltar : Task() {
    private companion object {
        private val NEAR_ALTAR_AREA = Area.rectangular(3180, 3165, 3188, 3157, 0)
    }

    override fun execute(): Boolean {
        if (!isOutsideAltar()) {
            return false
        }

        val altar = SceneObjects.query().names("Mysterious ruins").results().nearest()
        if (altar == null) {
            Movement.walkTowards(NEAR_ALTAR_AREA.randomTile)
        } else {
            altar.interact("Enter")
            sleepUntil({ isInWaterAltar() }, 10)
        }
        return true
    }
}