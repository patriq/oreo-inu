package slayer.task

import isInMeleeDistance
import org.rspeer.game.adapter.scene.Projectile
import org.rspeer.game.movement.Movement
import org.rspeer.game.position.area.Area
import org.rspeer.game.query.results.SceneNodeQueryResults
import org.rspeer.game.scene.Npcs
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.Projectiles
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Dodge Attack",
    blocking = true,
    blockIfSleeping = true,
)
class DodgeAttackTask : Task() {
    private companion object {
        private val AOE_ATTACKS = listOf(
            AoeAttack(1486, 5), // Adamant dragon poison projectile
            AoeAttack(1488, 4) // Rune dragon electric projectile
        )
    }

    override fun execute(): Boolean {
        val aoeProjectiles = aoeProjectiles()
        if (aoeProjectiles.isEmpty()) {
            return false
        }

        val dangerTiles = aoeProjectiles.map {
            val aoeAttack = AOE_ATTACKS.first { aoeAttack -> aoeAttack.projectileId == it.id }
            return@map Area.surrounding(it.targetPosition, aoeAttack.radius).tiles
        }.flatten().toSet()

        val playerTile = Players.self().position
        if (!dangerTiles.contains(playerTile)) {
            return false
        }

        // Get a safe tile that's melee distance from the targetting npc
        val projectileSenderArea =
            Npcs.query().targeting(Players.self()).results().firstOrNull()?.area ?: Area.rectangular(0, 0, 0, 0, 0)
        val safeTiles = Area.surrounding(Players.self().position, 10).tiles
            .filter { !dangerTiles.contains(it) }
            .sortedWith(compareBy({ !projectileSenderArea.isInMeleeDistance(it.area) }, { it.distance() }))
        Movement.walkTowards(safeTiles.first())
        // In case we weren't able to move safely towards melee distance, sleep to avoid attacking
        if (!projectileSenderArea.isInMeleeDistance(safeTiles.first().area)) {
            sleepUntil({ aoeProjectiles().isEmpty() }, 5)
        }
        return true
    }

    private fun aoeProjectiles(): SceneNodeQueryResults<Projectile> {
        return Projectiles.query().ids(*AOE_ATTACKS.map { it.projectileId }.toIntArray()).results()
    }
}

data class AoeAttack(val projectileId: Int, val radius: Int)