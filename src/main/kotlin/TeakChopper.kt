import org.rspeer.commons.StopWatch
import org.rspeer.commons.logging.Log
import org.rspeer.event.Subscribe
import org.rspeer.game.Game
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.combat.Combat
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.event.RenderEvent
import org.rspeer.game.event.SkillEvent
import org.rspeer.game.event.TickEvent
import org.rspeer.game.movement.Movement
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.position.area.RectangularArea
import org.rspeer.game.position.area.SingularArea
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.Projection
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Script
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme
import java.awt.Color
import kotlin.math.abs

@ScriptMeta(
    name = "1.5 teak chopper",
    developer = "Oreo",
    version = 1.00,
    desc = "Chops teaks in the Fossil Island",
    paint = PaintScheme::class,
    regions = [-3]
)
class OneHalfTeakChopper : Script() {
    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Tick")
    private var tick: Int = -1

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.WOODCUTTING)

    private var lastXpTick = -1
    private var nextTickChopPosition = emptyList<Position>()
    private var path = emptyList<Position>()

    @Subscribe(async = false)
    fun onTick(event: TickEvent) {
        val shouldDrinkStam = Movement.getRunEnergy() <= 25 && !Movement.isStaminaEnhancementActive()
        val stamina = Backpack.backpack().query().nameContains("Stamina").results().firstOrNull()
        if (shouldDrinkStam && stamina == null) {
            Log.severe("Out of staminas")
            state = State.STOPPED
            return
        }

        val player = Players.self().position
        val trees = SceneObjects.query().names("Teak tree").results().sortByDistance()
        // All the tiles that are next to a tree that we can chop from
        val treeChopTiles = trees.flatMap { Area.surrounding(it.area.center, 2).tiles }
            .filter { trees.any { tree -> tree.area.isInMeleeDistance(it.area) } }
        // A list of all the tiles we can move to in one tick excluding the current tile
        val possibleOneTickMovements =
            Area.surrounding(player, 5).tiles.filter { player.canPathInOneTick(it) }.filter { it != player }
        // A list of all the tiles we can move to in one tick that we can chop from
        nextTickChopPosition = possibleOneTickMovements.filter { it in treeChopTiles }

        // Script here
        if (tick == 0) {
            if (nextTickChopPosition.isEmpty()) {
                if (!Players.self().isMoving) {
                    trees.first()?.interact("Chop down")
                }
                return
            }

            // Use mahogany logs on knife
            val knife = Backpack.backpack().query().names("Knife").results().firstOrNull()
            val mahoganyLogs = Backpack.backpack().query().names("Mahogany logs").results().firstOrNull()
            if (knife != null && mahoganyLogs != null) {
                Backpack.backpack().use(knife, mahoganyLogs)
            }

            // Queue movement next to the tree, if the movement is more than 1 tile, then we should wait for it to finish
            if (nextTickChopPosition.isNotEmpty()) {
                val nextTile = nextTickChopPosition.first()
                Movement.walkTowards(nextTile)
            }
        }

        if (tick == 1) {
            // Drop if needed and requeue chopping
            Backpack.backpack().query().names("Teak logs").results().take(2).forEach { it.interact("Drop") }

            // Cast special if you have it
            if (Combat.getSpecialEnergy() == 100) {
                Combat.toggleSpecial(true)
            }

            // Drink stamina if needed
            if (shouldDrinkStam) {
                stamina?.interact("Drink")
            }

            // Chop tree
            trees.first()?.interact("Chop down")
        }

        tick++
        if (tick >= 3) {
            tick = 0
        }
    }

    @Subscribe(async = false)
    fun onSkill(event: SkillEvent) {
        if (event.source == Skill.WOODCUTTING) {
            Log.info("Ticks: ${event.tick - lastXpTick}")
            lastXpTick = event.tick
        }
    }

    override fun loop(): Int {
        return 20000
    }

    @Subscribe
    fun onRender(event: RenderEvent) {
//        val player = Players.self().position
//        val oneTick = Area.surrounding(player, 5).tiles.filter { player.canPathInOneTick(it) }
//        val trees = SceneObjects.query().names("Teak tree").results()
//
//        oneTick.forEach {
//            val polygon = Projection.getPositionPolygon(
//                Projection.Canvas.VIEWPORT, it
//            ) ?: return@forEach
//            event.source.color = trees.any { tree ->
//                tree.area.isInMeleeDistance(it.area)
//            }.let { if (it) Color.GREEN else Color.YELLOW }
//            event.source.drawPolygon(polygon)
//        }
//
//        val polygon = Projection.getPositionPolygon(
//            Projection.Canvas.VIEWPORT, player
//        ) ?: return
//        event.source.color = Color.RED
//        event.source.drawPolygon(polygon)
        nextTickChopPosition.forEach {
            val polygon = Projection.getPositionPolygon(
                Projection.Canvas.VIEWPORT, it
            ) ?: return@forEach
            event.source.color = Color.GREEN
            event.source.drawPolygon(polygon)
        }

        path.forEach {
            val polygon = Projection.getPositionPolygon(
                Projection.Canvas.VIEWPORT, it
            ) ?: return@forEach
            event.source.color = Color.RED
            event.source.drawPolygon(polygon)
        }
    }
}

fun Area.getAxisDistances(other: Area): Pair<Int, Int> {
    fun Area.toRectangular(): RectangularArea {
        return when (this) {
            is RectangularArea -> this
            is SingularArea -> Area.surrounding(this.center, 0) as RectangularArea
            else -> throw IllegalArgumentException("Only singular/rectangular areas are supported")
        }
    }

    fun RectangularArea.getComparisonPoint(other: RectangularArea): Pair<Int, Int> {
        val thisX = this.xs.minimum()
        val thisY = this.ys.minimum()
        val thisWidth = this.xs.maximum() - thisX + 1
        val thisHeight = this.ys.maximum() - thisY + 1

        val otherX = other.xs.minimum()
        val otherY = other.ys.minimum()

        val x = if (otherX <= thisX) {
            thisX
        } else if (otherX >= thisX + thisWidth - 1) {
            thisX + thisWidth - 1
        } else {
            otherX
        }
        val y = if (otherY <= thisY) {
            thisY
        } else if (otherY >= thisY + thisHeight - 1) {
            thisY + thisHeight - 1
        } else {
            otherY
        }
        return Pair(x, y)
    }

    val thisRectangular = this.toRectangular()
    val otherRectangular = other.toRectangular()
    val (p1X, p1Y) = thisRectangular.getComparisonPoint(otherRectangular)
    val (p2X, p2Y) = otherRectangular.getComparisonPoint(thisRectangular)
    return Pair(abs(p1X - p2X), abs(p1Y - p2Y))
}

fun Area.isInMeleeDistance(other: Area): Boolean {
    val (x, y) = getAxisDistances(other)
    return x + y == 1
}

@OptIn(ExperimentalStdlibApi::class)
fun Position.canPathInOneTick(to: Position): Boolean {
    fun oneTickPath(from: Position, to: Position): List<Position> {
        if (from == to) {
            return listOf(from)
        }
        val distance = from.distance(to).toInt()
        if (distance > 2) {
            return emptyList()
        }
        val dx = to.x - from.x
        val dy = to.y - from.y
        val signX = Integer.signum(dx)
        val signY = Integer.signum(dy)
        val possiblePaths = mutableListOf<List<Position>>()

        // If it's only one tile away, check if we can travel in that direction
        if (distance == 1) {
            possiblePaths.add(listOf(from, to))
            // You can also sometimes move in an L shape when moving diagonally
            if (abs(dx) == abs(dy)) {
                possiblePaths.add(listOf(from, from + Position(signX, 0, 0), to))
                possiblePaths.add(listOf(from, from + Position(0, signY, 0), to))
            }
        } else {
            // If it's only two tiles away, check all possible paths
            // If it's fully diagonal or fully cardinal there is only one possible path
            if (abs(dx) == abs(dy) || (dx == 0 || dy == 0)) {
                possiblePaths.add(listOf(from, from + Position(signX, signY, 0), to))
            } else {
                // Move in the cardinal direction first and then move diagonally
                if (abs(dx) > abs(dy)) {
                    possiblePaths.add(listOf(from, from + Position(signX, 0, 0), to))
                    possiblePaths.add(listOf(from, from + Position(signX, signY, 0), to))
                } else {
                    possiblePaths.add(listOf(from, from + Position(0, signY, 0), to))
                    possiblePaths.add(listOf(from, from + Position(signX, signY, 0), to))
                }
            }
        }

        // From the possible paths, filter out the ones that are not possible
        val validPaths = mutableListOf<List<Position>>()
        possiblePaths.forEach { path ->
            // Go through each tile in the path and check if we can travel in that direction from the previous tile
            var last = path.first()
            for (i in 1..<path.size) {
                val next = path.elementAt(i)
                if (!last.canTravelInDirection(next.x - last.x, next.y - last.y)) {
                    return@forEach
                }
                last = next
            }
            validPaths.add(path)
        }
        return validPaths.firstOrNull() ?: emptyList()
    }

    return oneTickPath(this, to).isNotEmpty()
}


object CollisionDataFlag {
    /**
     * Directional movement blocking flags.
     */
    const val BLOCK_MOVEMENT_NORTH_WEST = 0x1
    const val BLOCK_MOVEMENT_NORTH = 0x2
    const val BLOCK_MOVEMENT_NORTH_EAST = 0x4
    const val BLOCK_MOVEMENT_EAST = 0x8
    const val BLOCK_MOVEMENT_SOUTH_EAST = 0x10
    const val BLOCK_MOVEMENT_SOUTH = 0x20
    const val BLOCK_MOVEMENT_SOUTH_WEST = 0x40
    const val BLOCK_MOVEMENT_WEST = 0x80

    /**
     * Movement blocking type flags.
     */
    const val BLOCK_MOVEMENT_OBJECT = 0x100
    const val BLOCK_MOVEMENT_FLOOR_DECORATION = 0x40000
    const val BLOCK_MOVEMENT_FLOOR = 0x200000 // Eg. water
    const val BLOCK_MOVEMENT_FULL = BLOCK_MOVEMENT_OBJECT or
            BLOCK_MOVEMENT_FLOOR_DECORATION or
            BLOCK_MOVEMENT_FLOOR

    /**
     * Directional line of sight blocking flags.
     */
    const val BLOCK_LINE_OF_SIGHT_NORTH = BLOCK_MOVEMENT_NORTH shl 9 // 0x400
    const val BLOCK_LINE_OF_SIGHT_EAST = BLOCK_MOVEMENT_EAST shl 9 // 0x1000
    const val BLOCK_LINE_OF_SIGHT_SOUTH = BLOCK_MOVEMENT_SOUTH shl 9 // 0x4000
    const val BLOCK_LINE_OF_SIGHT_WEST = BLOCK_MOVEMENT_WEST shl 9 // 0x10000
    const val BLOCK_LINE_OF_SIGHT_FULL = 0x20000
}

private fun Position.canTravelInDirection(dx: Int, dy: Int): Boolean {
    val dx = Integer.signum(dx)
    val dy = Integer.signum(dy)
    val width = 1
    val height = 1

    if (dx == 0 && dy == 0) {
        return true
    }

    val startX: Int = this.toScene().x + dx
    val startY: Int = this.toScene().y + dy
    val checkX: Int = startX + if (dx > 0) width - 1 else 0
    val checkY: Int = startY + if (dy > 0) height - 1 else 0
    val endX: Int = startX + width - 1
    val endY: Int = startY + height - 1

    var xFlags: Int = CollisionDataFlag.BLOCK_MOVEMENT_FULL
    var yFlags: Int = CollisionDataFlag.BLOCK_MOVEMENT_FULL
    var xyFlags: Int = CollisionDataFlag.BLOCK_MOVEMENT_FULL
    var xWallFlagsSouth: Int = CollisionDataFlag.BLOCK_MOVEMENT_FULL
    var xWallFlagsNorth: Int = CollisionDataFlag.BLOCK_MOVEMENT_FULL
    var yWallFlagsWest: Int = CollisionDataFlag.BLOCK_MOVEMENT_FULL
    var yWallFlagsEast: Int = CollisionDataFlag.BLOCK_MOVEMENT_FULL

    if (dx < 0) {
        xFlags = xFlags or CollisionDataFlag.BLOCK_MOVEMENT_EAST
        xWallFlagsSouth = xWallFlagsSouth or (CollisionDataFlag.BLOCK_MOVEMENT_SOUTH or
                CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_EAST)
        xWallFlagsNorth = xWallFlagsNorth or (CollisionDataFlag.BLOCK_MOVEMENT_NORTH or
                CollisionDataFlag.BLOCK_MOVEMENT_NORTH_EAST)
    }
    if (dx > 0) {
        xFlags = xFlags or CollisionDataFlag.BLOCK_MOVEMENT_WEST
        xWallFlagsSouth = xWallFlagsSouth or (CollisionDataFlag.BLOCK_MOVEMENT_SOUTH or
                CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_WEST)
        xWallFlagsNorth = xWallFlagsNorth or (CollisionDataFlag.BLOCK_MOVEMENT_NORTH or
                CollisionDataFlag.BLOCK_MOVEMENT_NORTH_WEST)
    }
    if (dy < 0) {
        yFlags = yFlags or CollisionDataFlag.BLOCK_MOVEMENT_NORTH
        yWallFlagsWest = yWallFlagsWest or (CollisionDataFlag.BLOCK_MOVEMENT_WEST or
                CollisionDataFlag.BLOCK_MOVEMENT_NORTH_WEST)
        yWallFlagsEast = yWallFlagsEast or (CollisionDataFlag.BLOCK_MOVEMENT_EAST or
                CollisionDataFlag.BLOCK_MOVEMENT_NORTH_EAST)
    }
    if (dy > 0) {
        yFlags = yFlags or CollisionDataFlag.BLOCK_MOVEMENT_SOUTH
        yWallFlagsWest = yWallFlagsWest or (CollisionDataFlag.BLOCK_MOVEMENT_WEST or
                CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_WEST)
        yWallFlagsEast = yWallFlagsEast or (CollisionDataFlag.BLOCK_MOVEMENT_EAST or
                CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_EAST)
    }
    if (dx < 0 && dy < 0) {
        xyFlags = xyFlags or CollisionDataFlag.BLOCK_MOVEMENT_NORTH_EAST
    }
    if (dx < 0 && dy > 0) {
        xyFlags = xyFlags or CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_EAST
    }
    if (dx > 0 && dy < 0) {
        xyFlags = xyFlags or CollisionDataFlag.BLOCK_MOVEMENT_NORTH_WEST
    }
    if (dx > 0 && dy > 0) {
        xyFlags = xyFlags or CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_WEST
    }

    val collisionDataFlags: Array<IntArray> = Game.getClient().collisionMaps.get(Game.getClient().floorLevel).flags

    if (dx != 0) {
        // Check that the area doesn't bypass a wall
        for (y in startY..endY) {
            if (collisionDataFlags[checkX][y] and xFlags != 0
            ) {
                // Collision while attempting to travel along the x axis
                return false
            }
        }

        // Check that the new area tiles don't contain a wall
        for (y in startY + 1..endY) {
            if (collisionDataFlags[checkX][y] and xWallFlagsSouth != 0) {
                // The new area tiles contains a wall
                return false
            }
        }
        for (y in endY - 1 downTo startY) {
            if (collisionDataFlags[checkX][y] and xWallFlagsNorth != 0) {
                // The new area tiles contains a wall
                return false
            }
        }
    }
    if (dy != 0) {
        // Check that the area tiles don't bypass a wall
        for (x in startX..endX) {
            if (collisionDataFlags[x][checkY] and yFlags != 0
            ) {
                // Collision while attempting to travel along the y axis
                return false
            }
        }

        // Check that the new area tiles don't contain a wall
        for (x in startX + 1..endX) {
            if (collisionDataFlags[x][checkY] and yWallFlagsWest != 0) {
                // The new area tiles contains a wall
                return false
            }
        }
        for (x in endX - 1 downTo startX) {
            if (collisionDataFlags[x][checkY] and yWallFlagsEast != 0) {
                // The new area tiles contains a wall
                return false
            }
        }
    }
    if (dx != 0 && dy != 0) {
        if (collisionDataFlags[checkX][checkY] and xyFlags != 0
        ) {
            // Collision while attempting to travel diagonally
            return false
        }

        // When the areas edge size is 1 and it attempts to travel
        // diagonally, a collision check is done for respective
        // x and y axis as well.
        if (width == 1) {
            if (collisionDataFlags[checkX][checkY - dy] and xFlags != 0
            ) {
                return false
            }
        }
        if (height == 1) {
            if (collisionDataFlags[checkX - dx][checkY] and yFlags != 0
            ) {
                return false
            }
        }
    }
    return true
}

private operator fun Position.plus(position: Position): Position {
    return Position(x + position.x, y + position.y, floorLevel)
}
