package slayer.dungeon

import api.containsPlayer
import org.rspeer.game.Game
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.SceneObjects

object ChasmOfFire {
    enum class Location(val area: Area) {
        GREATER_DEMONS(Area.rectangular(1407, 10111, 1470, 10048, 2)),
        BLACK_DEMONS(Area.rectangular(1407, 10111, 1470, 10048, 1)),
    }

    private val outsideArea =  Area.rectangular(1459, 3684, 1424, 3652, 0)

    private val dungeonAreasByPlane = listOf(
        Area.rectangular(1407, 10111, 1470, 10048, 0),
        Area.rectangular(1407, 10111, 1470, 10048, 1),
        Area.rectangular(1407, 10111, 1470, 10048, 2),
        Area.rectangular(1407, 10111, 1470, 10048, 3)
    )

    fun isInsideDungeon(): Boolean = dungeonAreasByPlane[Game.getClient().floorLevel].containsPlayer()

    fun isOutside(): Boolean = outsideArea.containsPlayer()

    fun getArea(location: Location): Area = location.area

    fun enter(): Boolean {
        if (isInsideDungeon()) return true
        val rope = SceneObjects.query().names("Chasm").results().nearest() ?: return false
        return rope.interact("Enter")
    }

    fun walk(location: Location): Boolean {
        if (getArea(location).containsPlayer()) {
            return true
        }
        if (!isInsideDungeon()) {
            return false
        }
        val lift = SceneObjects.query().ids(30258)
            .filter { it.position.y != 10075 } // Unreachable lift
            .results().nearest() ?: return false
        lift.interact("Enter")
        return true
    }
}