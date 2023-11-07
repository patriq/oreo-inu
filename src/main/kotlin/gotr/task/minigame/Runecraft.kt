package gotr.task.minigame

import api.pouch.Pouch
import gotr.Altar
import gotr.MinigameContext.Companion.containsChargedCells
import gotr.MinigameContext.Companion.containsGuardianEssence
import gotr.MinigameContext.Companion.insideAltar
import gotr.MinigameContext.Companion.unchargedCellCount
import gotr.MinigameState
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.component.tdi.Skills
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Runecrafing essence/charged cells")
class Runecraft : MinigameTask() {
    private var currentAltar: Altar? = null

    override fun minigameExecute(): Boolean {
        if (context.state != MinigameState.CRAFT_GUARDIAN_STONES
            && context.state != MinigameState.CRAFT_CHARGED_CELL
        ) {
            currentAltar = null
            return false
        }

        // Check if inside an altar
        if (insideAltar()) {
            val altar = SceneObjects.query().names("Altar").results().nearest() ?: return false
            if (containsGuardianEssence() || (!containsChargedCells() && unchargedCellCount() > 0)) {
                altar.interact("Craft-rune")
            } else if (!Pouch.allPouchesEmpty()) {
                Pouch.emptyPouches()
            }
            return true
        }

        // Enter any of the active altars we can do
        val activeAltars = context.activeAltars
        if (currentAltar == null || activeAltars.none { it == currentAltar }) {
            currentAltar = calculateNextAltar(context.activeAltars)
        }

        // Interact with the altar
        val guardian = currentAltar?.guardian() ?: return false
        return guardian.interact("Enter")
    }

    private fun calculateNextAltar(currentAltars: List<Altar>): Altar? {
        val currentRunecraftingLevel = Skills.getCurrentLevel(Skill.RUNECRAFTING)
        return currentAltars
            .filter { it.runecraftingLevel <= currentRunecraftingLevel }
            .sortedWith(compareBy(
                {
                    if (context.state == MinigameState.CRAFT_CHARGED_CELL) {
                        -it.cellType.ordinal
                    } else {
                        false
                    }
                }, // Prioritize the best cell type when crafting charged cells
                { it.guardian()?.distance() ?: 0.0 } // Prioritize the closest altar
            ))
            .firstOrNull()
    }
}