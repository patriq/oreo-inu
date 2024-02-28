package blastfurnace.task

import org.rspeer.commons.logging.Log
import org.rspeer.event.Subscribe
import org.rspeer.game.Game
import org.rspeer.game.Vars
import org.rspeer.game.component.Inventories
import org.rspeer.game.event.ChatMessageEvent
import org.rspeer.game.movement.Movement
import org.rspeer.game.position.Position
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Walk to Dispenser",
    blocking = true,
    register = true
)
class WalkDispenserTask : Task() {
    private companion object {
        private val STANDING_TILE = Position(1940, 4962, 0)
    }

    private var tickPlaced = 0

    override fun execute(): Boolean {
        if (!shouldExecute()) {
            return false
        }

        Movement.walkTowards(STANDING_TILE)
        Inventories.backpack().getItems("Goldsmith gauntlets").firstOrNull()?.interact("Wear")
        return true
    }

    private fun shouldExecute(): Boolean {
        return Inventories.backpack().getItems("Gold ore").isEmpty() && (Game.getTick() - tickPlaced) <= 8
                && goldBarsInForge() < 3
    }

    @Subscribe
    fun onChatMessageEvent(event: ChatMessageEvent) {
        if (event.contents.contains("All your ore goes onto the conveyor belt.")) {
            tickPlaced = Game.getTick()
        }
    }

    private fun goldBarsInForge(): Int {
        return Vars.get(546) shr 16
    }
}