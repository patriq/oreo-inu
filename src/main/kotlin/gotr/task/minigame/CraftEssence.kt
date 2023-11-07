package gotr.task.minigame

import api.pouch.Pouch
import gotr.MinigameContext.Companion.isClimbingLadder
import gotr.MinigameContext.Companion.isCraftingGuardianEssence
import gotr.MinigameState
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Craft essence")
class CraftEssence : MinigameTask() {
    override fun minigameExecute(): Boolean {
        if (context.state != MinigameState.CRAFT_WITHOUT_FILLING_POUCHES
            && context.state != MinigameState.CRAFT_WITH_FILLING_POUCHES) {
            return false
        }

        // Don't craft essence if game is about to end
        if (context.guardianPower >= 97) {
            return false
        }

        // Don't do anything while climbing/lowering the ladder
        if (isClimbingLadder()) {
            return true
        }

        // Fill pouches whenever you can
        var filledPouch = false
        if (context.state == MinigameState.CRAFT_WITH_FILLING_POUCHES) {
            filledPouch = Pouch.fillPouch()
        }

        // Don't do anything while crafting if we didn't fill a pouch this tick
        if (isCraftingGuardianEssence() && !filledPouch) {
            return true
        }

        // Craft essence
        val workbench = SceneObjects.query().names("Workbench").results().nearest() ?: return false
        val result = workbench.interact("Work-at")
        if (result) {
            sleepWhile({ Players.self().isMoving }, 20)
        }
        return result
    }
}