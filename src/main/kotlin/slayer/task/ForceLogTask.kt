package slayer.task

import org.rspeer.commons.logging.Log
import org.rspeer.game.Game
import org.rspeer.game.component.Worlds
import org.rspeer.game.scene.Players
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import javax.inject.Inject

@TaskDescriptor(
    name = "Force log",
    blockIfSleeping = true,
    loginScreen = true
)
class ForceLogTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    private companion object {
        private val TOTAL_WORLDS = intArrayOf(363, 415, 450, 526, 349, 361, 396, 428, 527)
    }

    private var worldHopOrder = intArrayOf()

    override fun execute(): Boolean {
        if (Game.getClient().gameState != Game.STATE_IN_GAME) {
            return false
        }
        val area = ctx.currentTaskInfo()?.standingArea() ?: return false
        val self = Players.self() ?: return false
        val otherPlayers = Players.getLoaded().filter { it != self }
        if (otherPlayers.isEmpty()) {
            return false
        }
        if (otherPlayers.any { area.contains(it) }) {
            hop()
            return true
        }
        return false
    }

    private fun hop() {
        // If the world hop order is empty, reset it
        if (worldHopOrder.isEmpty()) {
            println("Resetting world hop order")
            worldHopOrder = TOTAL_WORLDS.shuffleCopy()
        }

        // Pick the next world to hop to and remove it from the list
        val nextWorld = Worlds.query().current(false).ids(*worldHopOrder).results().firstOrNull() ?: return
        worldHopOrder = worldHopOrder.filter { it != nextWorld.id }.toIntArray()

        // Perform the hop
        Log.info("Hopping to world ${nextWorld.id}")
        Game.getClient().gameState = Game.STATE_CREDENTIALS_SCREEN
        Worlds.hopTo(nextWorld)
    }

    private fun IntArray.shuffleCopy(): IntArray {
        val copy = this.copyOf()
        copy.shuffle()
        return copy
    }
}