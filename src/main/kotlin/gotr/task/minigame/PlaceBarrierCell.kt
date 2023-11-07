package gotr.task.minigame

import gotr.CellType
import gotr.MinigameContext.Companion.CHARGED_CELL_NAMES
import gotr.MinigameContext.Companion.containsChargedCells
import gotr.MinigameState
import org.rspeer.commons.logging.Log
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Place barrier cell")
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
        Log.info("Charged cell type: $chargedCellType")
        if (chargedCellType == null) {
            return false
        }

        // Place barrier cell
        val cellTiles =
            SceneObjects.query().nameContains("cell tile").results()
                .map { it to CellType.fromCellTileName(it.name) }
                .filter {
                    if (it.second != null) {
                        return@filter true
                    }
                    Log.severe("Unknown cell tile: ${it.first.name}")
                    return@filter false
                }
        val cellTile = cellTiles.sortedWith(compareBy(
            { chargedCellType.ordinal <= it.second!!.ordinal }, // Prefer lower level cells compared to ours
            { it.first.distance() } // Prefer closer cells
        )).firstOrNull()?.first ?: return true
        return cellTile.interact("Place-cell")
    }
}