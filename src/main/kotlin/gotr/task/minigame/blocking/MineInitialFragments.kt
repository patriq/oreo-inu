package gotr.task.minigame.blocking

import gotr.MinigameContext.Companion.isClimbingLadder
import gotr.MinigameContext.Companion.isInsideLargeRemainsMiningArea
import gotr.MinigameContext.Companion.isMining
import gotr.MinigameContext.Companion.pickaxeSpec
import gotr.MinigameState
import gotr.task.minigame.MinigameTask
import org.rspeer.event.Subscribe
import org.rspeer.game.event.ChatMessageEvent
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Mine initial fragments", blocking = true, register = true)
class MineInitialFragments : MinigameTask() {
    override fun minigameExecute(): Boolean {
        if (context.state != MinigameState.MINE_INITIAL_FRAGMENTS) {
            return false
        }

        // We are descending into the mine or mining
        if (isClimbingLadder()) {
            return true
        }

        // We are mining
        val specced = context.isMinigameRunning && pickaxeSpec()
        if (isMining() && !specced) {
            return true
        }

        // Mine small fragments
        if (isInsideLargeRemainsMiningArea()) {
            val remains = SceneObjects.query().names("Large guardian remains").results()
                .minBy { it.position.y } ?: return false
            // Walk near the remains (only start mining when the game starts)
            if (context.isMinigameRunning || remains.distance() > 3) {
                remains.interact("Mine")
            }
            return true
        }

        val ladder = SceneObjects.query().names("Rubble").results().nearest() ?: return false
        return ladder.interact("Climb")
    }

    @Subscribe(async = false)
    fun onChatMessage(event: ChatMessageEvent) {
        if (event.contents.contains("The rift will become active in 5 seconds")) {
            pickaxeSpec()
        }
    }
}