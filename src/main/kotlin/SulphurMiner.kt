import api.script.GuiceTaskScript
import org.rspeer.event.Subscribe
import org.rspeer.game.Game
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.event.SkillEvent
import org.rspeer.game.position.Position
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintScheme

@ScriptMeta(
    name = "Sulphur miner",
    developer = "Oreo",
    version = 1.00,
    desc = "Mines sulphur in the Lovakengj mine",
    paint = PaintScheme::class,
    regions = [-3]
)
class SulphurMiner : GuiceTaskScript() {
    override fun tasks(): Array<Class<out Task>> {
        return arrayOf(
            MineTask::class.java
        )
    }
}

@TaskDescriptor(name = "Mine sulphur", register = true)
class MineTask : Task() {
    companion object {
        private val ROCK_POSITION = Position(1423, 3872, 0)
    }

    private var lastXpDropTick = -1
    private var dropping = false

    override fun execute(): Boolean {
        val rock = SceneObjects.query().names("Volcanic sulphur").on(ROCK_POSITION).results().firstOrNull()
        if (rock == null || Backpack.backpack().isFull) {
            dropping = true
        }

        val sulphur = Backpack.backpack().query().names("Volcanic sulphur").results().take(5)
        if (dropping) {
            if (sulphur.isEmpty()) {
                dropping = false
            } else {
                sulphur.take(5).forEach { it.interact("Drop") }
                return true
            }
        }

        if (Game.getTick() - lastXpDropTick < 4) {
            return true
        }

        rock?.interact("Mine")
        return true
    }

    @Subscribe
    fun onXpDrop(event: SkillEvent) {
        if (event.source != Skill.MINING) {
            return
        }

        lastXpDropTick = event.tick
    }

}