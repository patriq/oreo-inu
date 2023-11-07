package knights.task

import org.rspeer.commons.math.Random
import org.rspeer.game.adapter.component.inventory.Inventory
import org.rspeer.game.effect.Health
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(
    name = "Eating!",
)
class EatTask : Task() {
    override fun execute(): Boolean {
        if (Health.getPercent() > Random.nextInt(50)) {
            return false
        }
        val food = Inventory.backpack().query().names("Jug of wine").results().firstOrNull() ?: return false
        return food.interact("Drink")
    }
}