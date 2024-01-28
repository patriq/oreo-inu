package slayer.task

import api.containsPlayer
import org.rspeer.game.adapter.scene.Npc
import org.rspeer.game.movement.pathfinding.LocalPathfinder
import org.rspeer.game.scene.Npcs
import org.rspeer.game.scene.Players
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import javax.inject.Inject

@TaskDescriptor(
    name = "Attack",
    blocking = true,
    blockIfSleeping = true,
)
class AttackTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    private companion object {
        private val NPC_SORTER = compareBy<Npc>(
            { it.target != Players.self() }, // Prefer npcs that are attacking us
            { it.target != null }, // Prefer npcs that are not attacking anyone
            { it.healthPercent }, // Prefer npcs with lower health
            { LocalPathfinder(Players.self().position, it.position).get().size } // Prefer npcs that are closer
        )

        private fun isSomeoneElseAttacking(npc: Npc): Boolean {
            val local = Players.self()
            if (npc.target == local) {
                return false
            }
            return Players.query().targeting(npc).filter { it != local }.results().isNotEmpty()
        }
    }

    override fun execute(): Boolean {
        val currentTaskInfo = ctx.currentTaskInfo() ?: return false
        val standingArea = currentTaskInfo.standingArea()
        if (!standingArea.containsPlayer()) {
            return false
        }

        // If we are already attacking, and there is no one else attacking it, then we are good
        val attackingMonster = currentTaskInfo.targetSlayerNpc()
        if (attackingMonster != null && attackingMonster.healthPercent > 0 && !isSomeoneElseAttacking(attackingMonster)) {
            return false
        }

        val monster = Npcs.query().names(*currentTaskInfo.monsterNames())
            .reachable().within(standingArea).health(1)
            .filter { !isSomeoneElseAttacking(it) }.results()
            .sortedWith(NPC_SORTER).firstOrNull() ?: return false

        // We interact in-case it's fungi that needs to be picked
        monster.interact { true }
        sleepUntil({ currentTaskInfo.targetSlayerNpc() != null }, 5)
        return true
    }
}