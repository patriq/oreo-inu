package slayer.task

import org.rspeer.game.adapter.scene.Pickable
import org.rspeer.game.component.Inventories
import org.rspeer.game.component.tdi.Prayers
import org.rspeer.game.effect.Health
import org.rspeer.game.query.scene.PickableQuery
import org.rspeer.game.scene.Pickables
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import slayer.data.PotionType
import slayer.data.Settings
import javax.inject.Inject

@TaskDescriptor(
    name = "Loot",
    blocking = true,
    blockIfSleeping = true,
)
class LootTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    override fun execute(): Boolean {
        // Pick herbs
        val herbs = herbs()
        if (herbs.isNotEmpty()) {
            herbs.first().interact("Take")
            sleepUntil({ herbs().size != herbs.size }, 5)
            return true
        }

        // Pick seeds
        val seeds = seeds()
        if (seeds.isNotEmpty()) {
            seeds.first().interact("Take")
            sleepUntil({ seeds().size != seeds.size }, 5)
            return true
        }
        
        // Pick loot if there is space
        val allLoot = loot()
        if (allLoot.isNotEmpty()) {
            val loot = allLoot.first()
            val definition = loot.definition

            // Check if we need to drop/eat stuff to pick it up
            var needsDrop = Inventories.backpack().isFull
            if (definition != null && definition.isStackable && Inventories.backpack()
                    .contains { it.ids(definition.id).results() }
            ) {
                needsDrop = false
            }

            // If we need to eat/drop, do it
            if (needsDrop) {
                val food = Inventories.backpack().getItems(Settings.FOOD).firstOrNull()
                if (food != null) {
                    if (Health.getCurrent() < Health.getLevel()) {
                        food.interact("Eat")
                    } else {
                        food.interact("Drop")
                    }
                } else {
                    val prayer = PotionType.PRAYER_TYPE.getFirstInInventory()
                    if (prayer != null) {
                        if (Prayers.getPoints() < Prayers.getTotalPoints()) {
                            prayer.interact("Drink")
                        } else {
                            prayer.interact("Drop")
                        }
                        return true
                    }
                }
            }

            // Pick it up
            if (loot.interact("Take")) {
                sleepUntil({ allLoot.size != loot().size }, 5)
                return true
            }
        }
        return false
    }

    private fun loot(): List<Pickable> = Pickables.query()
        .filter {
            // If it's one of the uniques
            if (Settings.UNIQUES.contains(it.name)) {
                return@filter true
            }

            // Pick task related loot
            val currentTaskLoot = Settings.TASK_LOOT[ctx.getTask()]
            if (currentTaskLoot != null && currentTaskLoot.contains(it.name)) {
                return@filter true
            }

            Settings.SHARED_LOOT.contains(it.name)
        }
        .filterInvalidLoots().results().sortByDistance().results

    private fun herbs(): List<Pickable> =
        Pickables.query().names(*Settings.HERBS).filterInvalidLoots().results().toList()

    private fun seeds(): List<Pickable> =
        Pickables.query().names(*Settings.SEEDS).filterInvalidLoots().results().toList()

    private fun PickableQuery.filterInvalidLoots(): PickableQuery = filter {
        // Remove if not in area
        ctx.currentTaskInfo()!!.standingArea().contains(it)
    }
}