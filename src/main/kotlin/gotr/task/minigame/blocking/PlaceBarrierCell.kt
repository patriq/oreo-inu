package gotr.task.minigame.blocking

import gotr.CellType
import gotr.MinigameContext.Companion.CHARGED_CELL_NAMES
import gotr.MinigameContext.Companion.containsChargedCells
import gotr.MinigameContext.Companion.hugeFragmentsPortal
import gotr.MinigameState
import gotr.task.minigame.MinigameTask
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Place barrier cell", blocking = true)
class PlaceBarrierCell : MinigameTask() {
    override fun minigameExecute(): Boolean {
        if (context.state != MinigameState.FEED_GUARDIAN_STONES_AND_PLACE_BARRIER_CELL
            && context.state != MinigameState.PLACE_BARRIER_CELL) {
            return false
        }

        if (!containsChargedCells()) {
            return false
        }

        // Check what charged cell type we have
        val chargedCellType =
            CellType.fromCellName(Backpack.backpack().query().names(*CHARGED_CELL_NAMES).results().first().name)
                ?: return false

        // Place barrier cell
        val cellTile =
            SceneObjects.query().nameContains(" cell tile").results()
                .filter { !it.name.contains("broken") } // Filter broken cell tiles
                .map { it to CellType.fromCellTileName(it.name)!! }
                .sortedWith(compareBy(
                    { chargedCellType.ordinal <= it.second.ordinal }, // Prefer lower level cells compared to ours
                    {
                        val portal = hugeFragmentsPortal() ?: return@compareBy false
                        return@compareBy it.first.distance(portal)
                    }, // Prefer cells closer to the portal if it is up
                    { it.first.distance() } // Prefer closer cells
                )).firstOrNull()?.first ?: return true
        return cellTile.interact("Place-cell")
    }
}