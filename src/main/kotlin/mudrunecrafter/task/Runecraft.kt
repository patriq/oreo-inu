package mudrunecrafter.task

import api.pouch.Pouch
import mudrunecrafter.MudRunecrafter.Companion.isInWaterAltar
import mudrunecrafter.MudRunecrafter.Companion.isMagicImbueActive
import mudrunecrafter.MudRunecrafter.Companion.isTeleporting
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.component.tdi.Magic
import org.rspeer.game.component.tdi.Spell
import org.rspeer.game.movement.Movement
import org.rspeer.game.position.Position
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Runecrafting", blocking = true)
class Runecraft : Task() {
    private var tick = 0

    @OptIn(ExperimentalStdlibApi::class)
    override fun execute(): Boolean {
        if (!isInWaterAltar()) {
            tick = 0
            return false
        }

        val altar = SceneObjects.query().names("Altar").results().nearest()
        val earthRune = Backpack.backpack().query().names("Earth rune").results().firstOrNull() ?: return false

        when (tick) {
            0 -> {
                Movement.walkTowards(Position(2718, 4835, 0))
                if (!isMagicImbueActive()) {
                    Magic.cast(Spell.Lunar.MAGIC_IMBUE)
                }
            }
            1 -> {
                Backpack.backpack().use(earthRune, altar)
            }
            2, 3, 4 -> {
                // Wait while walking
            }
            5, 6 -> {
                Pouch.emptyPouches()
                Backpack.backpack().use(earthRune, altar)
                // Force mark pouches as empty since we know they are
                if (tick == 6) {
                    Pouch.entries.forEach { it.holding = 0 }
                }
            }
            else -> {
                if (!isTeleporting()) {
                    Backpack.backpack().query().nameContains("Crafting cape").results().firstOrNull()?.interact("Teleport")
                }
            }
        }
        tick++
        return true
    }
}