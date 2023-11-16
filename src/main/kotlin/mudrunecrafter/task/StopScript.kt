package mudrunecrafter.task

import org.rspeer.commons.logging.Log
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Stop script", stoppable = true)
class StopScript : Task() {
    override fun execute(): Boolean {
        if (!Bank.isOpen()) {
            return false
        }

        val essenceAmount = Bank.bank().getCount({ it.names("Pure essence").results() }, true)
        val earthsCount = Backpack.backpack().getCount({ it.names("Earth rune").results() }, true)
        val bindingNecklace = Bank.bank().getCount({ it.names("Binding necklace").results() }, true)
        val staminas = Bank.bank().getCount({ it.names("Stamina potion(1)").results() }, true)
        val unchargedRingOfElements = Backpack.backpack().contains { it.ids(26815).results() }
        if (essenceAmount < 200) {
            Log.severe("Out of essence")
            return true
        }
        if (earthsCount < 200) {
            Log.severe("Out of earths")
            return true
        }
        if (bindingNecklace == 0) {
            Log.severe("Out of binding necklaces")
            return true
        }
        if (staminas == 0) {
            Log.severe("Out of staminas")
            return true
        }
        if (unchargedRingOfElements) {
            Log.severe("Out of uncharged rings of elements")
            return true
        }
        return false
    }
}