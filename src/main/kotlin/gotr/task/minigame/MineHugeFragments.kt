package gotr.task.minigame

import api.pouch.Pouch
import gotr.MinigameContext.Companion.MINIGAME_MAIN_AREA
import gotr.MinigameContext.Companion.isInsideHugeRemainsMiningArea
import gotr.MinigameContext.Companion.isMining
import gotr.MinigameState
import org.rspeer.game.position.Position
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Mine huge fragments")
class MineHugeFragments : MinigameTask() {
    private companion object {
        private val CORNER_GUARDIAN_REMAINS_POSITION = Position(3601, 9490, 0)
    }

    override fun minigameExecute(): Boolean {
        if (context.state != MinigameState.MINE_HUGE_FRAGMENTS) {
            return false
        }

        // Mine huge fragments
        if (isInsideHugeRemainsMiningArea()) {
            // Fill pouches whenever you can
            val interacted = Pouch.fillPouch()

            // Don't do anything while mining if we didn't fill a pouch this tick
            if (isMining() && !interacted) {
                return true
            }

            val hugeFragments =
                SceneObjects.query().names("Huge guardian remains").results().minBy { it.position.y } ?: return false
            return hugeFragments.interact("Mine")
        }

        // Enter portal
        val portal = SceneObjects.query().names("Portal").within(MINIGAME_MAIN_AREA).results().nearest()
        if (portal != null) {
            return portal.interact("Enter")
        }

        // Mine small remains while waiting for portal to appear
        if (!isMining()) {
            SceneObjects.query().names("Guardian remains").on(CORNER_GUARDIAN_REMAINS_POSITION)
                .results().firstOrNull()?.interact("Mine")
        }
        return true
    }
}