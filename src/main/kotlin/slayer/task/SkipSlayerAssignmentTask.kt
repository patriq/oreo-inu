package slayer.task

import api.containsPlayer
import api.teleport.JewelleryBox
import org.rspeer.game.House
import org.rspeer.game.component.InterfaceComposite
import org.rspeer.game.component.Interfaces
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.Npcs
import org.rspeer.game.scene.Players
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import org.rspeer.game.web.Web
import slayer.*
import javax.inject.Inject

@TaskDescriptor(
    name = "Skip slayer assignment",
    blocking = true,
    blockIfSleeping = true,
)
class SkipSlayerAssignmentTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    private companion object {
        val EDGEVILLE_AREA = Area.rectangular(3118, 3520, 3071, 3462, 0)
    }

    override fun execute(): Boolean {
        // Check if we have a task that we should skip
        if (!ctx.hasTask() || !ctx.shouldSkipTask()) {
            return false
        }

        // Cancel task
        if (isRewardsOpen()) {
            Interfaces.getDirect(426, 12, 6)?.interact("Tasks")
            Interfaces.getDirect(426, 26, 0)?.interact { true }
            Interfaces.getDirect(426, 8, 55)?.interact("Confirm")
            return true
        }

        // Open rewards interface
        if (EDGEVILLE_AREA.containsPlayer()) {
            if (Players.self().isMoving) {
                return true
            }

            val krystilia = Npcs.query().names("Krystilia").results().nearest()
            if (krystilia != null) {
                krystilia.interact("Rewards")
                sleepUntil({ isRewardsOpen() }, 5)
            } else {
                Web.pathTo(Position(3109, 3513, 0))?.step()
            }
            return true
        }

        // Teleport to edgeville if inside house
        if (House.isInside()) {
            // Check if need restore stats
            if (shouldRestoreStats()) {
                restoreStats()
                return true
            }

            // Teleport to edgeville
            teleportJewelleryBox(JewelleryBox.Location.EDGEVILLE)
            sleepUntil({ EDGEVILLE_AREA.containsPlayer() }, 5)
            return true
        }

        teleportHouse()
        return true
    }

    private fun isRewardsOpen(): Boolean = Interfaces.isSubActive(InterfaceComposite.SLAYER_REWARDS)


}