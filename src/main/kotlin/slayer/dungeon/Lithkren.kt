package slayer.dungeon

import org.rspeer.game.position.area.Area
import org.rspeer.game.position.area.RectangularArea
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects

object Lithkren {
    private const val INSIDE_AREA_REGION = 6223
    private const val OUTSIDE_AREA_REGION = 14243

    enum class Location(internal val nonInstancedArea: RectangularArea, internal val barrierSceneX: Int) {
        ADAMANT_DRAGONS(Area.rectangular(1539, 5086, 1560, 5063, 0) as RectangularArea, 41),
        RUNE_DRAGONS(Area.rectangular(1574, 5085, 1596, 5063, 0) as RectangularArea, 54),
    }

    fun isInside(): Boolean =
        Players.self().position.regionId.let { it == INSIDE_AREA_REGION || it == OUTSIDE_AREA_REGION }

    fun getArea(location: Location): Area {
        if (Players.self().position.regionId != INSIDE_AREA_REGION) {
            return location.nonInstancedArea
        }
        // Convert area to instanced area
        val topLeft = location.nonInstancedArea.topLeft.instancePositions.first()
        val bottomRight = location.nonInstancedArea.bottomRight.instancePositions.first()
        return Area.rectangular(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y, 0)
    }

    fun walk(location: Location) {
        val currentRegionId = Players.self().position.regionId
        if (currentRegionId == INSIDE_AREA_REGION) {
            val barrier = SceneObjects.query().names("Barrier")
                .filter { it.position.toScene().x == location.barrierSceneX }.results().nearest() ?: return
            barrier.interact("Pass")
            return
        }

        if (currentRegionId == OUTSIDE_AREA_REGION) {
            val stairCase = SceneObjects.query().ids(32113).results().nearest() ?: return
            if (stairCase.position.y > Players.self().position.y) {
                stairCase.interact("Climb")
                return
            }

            val door = SceneObjects.query().names("Broken Doors").results().nearest() ?: return
            door.interact("Enter")
            return
        }
    }
}