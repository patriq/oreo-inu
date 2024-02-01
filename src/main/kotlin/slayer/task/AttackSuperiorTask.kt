package slayer.task

import org.rspeer.game.scene.Npcs
import org.rspeer.game.scene.Players
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import slayer.data.SlayerTask
import javax.inject.Inject

@TaskDescriptor(
    name = "Attack Superior",
    blocking = true,
    blockIfSleeping = true,
)
class AttackSuperiorTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    private companion object {
        private val SUPERIOR_NAMES = arrayOf(
            "Insatiable mutated Bloodveld",
            "King kurask",
            "Abhorrent spectre",
            "Choke devil",
            "Greater abyssal demon",
            "Nechryarch",
            "Marble gargoyle",
            "Night beast",
        )
        private val UNSUPPORTED = arrayOf(
            SlayerTask.GARGOYLES,
            SlayerTask.DARK_BEASTS,
        )
    }

    override fun execute(): Boolean {
        if (!ctx.isSuperiorAlive()) {
            return false
        }

        val superior = Npcs.query().names(*SUPERIOR_NAMES).results().firstOrNull()
        if (superior == null) {
            ctx.killedSuperior()
            return false
        }

        if (ctx.getTask() in UNSUPPORTED) {
            java.awt.Toolkit.getDefaultToolkit().beep()
            return false
        }

        // Attack
        val player = Players.self()
        if (player.target != superior) {
            superior.interact("Attack")
        }
        return true
    }
}