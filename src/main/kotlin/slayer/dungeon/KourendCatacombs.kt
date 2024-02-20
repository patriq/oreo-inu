package slayer.dungeon

import api.containsPlayer
import org.rspeer.commons.logging.Log
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.component.tdi.Prayers
import org.rspeer.game.movement.Movement
import org.rspeer.game.movement.pathfinding.LocalPathfinder
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.Npcs
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.web.Web


object KourendCatacombs {
    enum class Location(val area: Area, val walkPosition: Position) {
        MUTATED_BLOODVELDS_SOUTH_EAST(Area.rectangular(1684, 10023, 1698, 10009, 0), Position(1691, 10016, 0)),
        DAGANNOTHS_SOUTH(Area.rectangular(1656, 10005, 1678, 9986, 0), Position(1668, 9997, 0)),
        ANKOU_SOUTH(Area.rectangular(1632, 10003, 1653, 9984, 0), Position(1642, 9995, 0)),
        HELLHOUNDS_NORTH_WEST(Area.rectangular(1654, 10076, 1634, 10055, 0), Position(1645, 10064, 0)),
        FIRE_GIANTS_WEST(Area.rectangular(1641, 10073, 1613, 10046, 0), Position(1631, 10056, 0)),
        STEEL_DRAGONS_WEST(Area.rectangular(1596, 10065, 1616, 10048, 0), Position(1608, 10054, 0)),
        DUST_DEVILS_EAST(Area.rectangular(1722, 10038, 1708, 10025, 0), Position(1715, 10032, 0)),
        ABYSSAL_DEMONS_NORTH(Area.rectangular(1670, 10100, 1680, 10093, 0), Position(1675, 10097, 0)),
    }

    private val INSIDE_DUNGEON_AREA = Area.rectangular(1747, 10112, 1589, 9978, 0)
    private val MAIN_ENTRANCE_AREA = Area.rectangular(1630, 3678, 1646, 3668, 0)

    fun isInsideDungeon(): Boolean = INSIDE_DUNGEON_AREA.containsPlayer()

    fun outsideMainEntrance(): Boolean = MAIN_ENTRANCE_AREA.containsPlayer()

    fun walk(location: Location) {
        if (location.area.containsPlayer()) {
            return
        }

        // If we are super close to iron dragons, pray mage
        if (Npcs.query().names("Iron dragon").within(10).results().isNotEmpty()) {
            Log.warn("Praying mage!!!")
            Prayers.toggle(true, Prayer.Modern.PROTECT_FROM_MAGIC)
        }

        // Use web walker to walk through the dungeon
        val path = Web.pathTo(location.walkPosition)
        if (path != null) {
            path.step()
            return
        }

        // If web fails, try to path with local pathfinder
        if (MAIN_ENTRANCE_AREA.containsPlayer()) {
            SceneObjects.query().names("Statue").results().nearest()?.interact("Investigate")
        } else if (INSIDE_DUNGEON_AREA.containsPlayer()) {
            Movement.walkTowards(location.walkPosition)
        }
    }
}