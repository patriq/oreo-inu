package slayer.slayertaskinfo

import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.adapter.scene.Npc
import org.rspeer.game.component.Inventories
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder
import org.rspeer.game.config.item.loadout.BackpackLoadout
import org.rspeer.game.config.item.loadout.EquipmentLoadout
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.Npcs
import org.rspeer.game.scene.Players
import org.rspeer.game.script.Task
import slayer.ScriptContext
import slayer.data.Constants.MUST_HAVE_INVENTORY
import slayer.data.ItemConfig

abstract class SlayerTaskInfo(protected val ctx: ScriptContext) {
    abstract fun monsterNames(): Array<String>

    abstract fun standingArea(): Area

    abstract fun prayers(): Array<Prayer>

    abstract fun equipment(): Map<Equipment.Slot, String>

    abstract fun items(): List<ItemConfig>

    abstract fun walk(executingTask: Task)

    open fun useItems(): Array<String> = arrayOf()

    /**
     * Gets the task NPC that the player is currently targetting.
     */
    fun targetSlayerNpc(): Npc? {
        val interacting = Players.self().target ?: return null
        return Npcs.query().names(*monsterNames()).results().firstOrNull { it == interacting }
    }

    fun slayerNpcsTargettingMe(): List<Npc> {
        return Npcs.query().names(*monsterNames()).targeting(Players.self()).results().toList()
    }

    fun backpackReady(): Boolean = items().all { it.backpackCount() >= it.amount } && containsAllEquipment()

    fun readyForFight(): Boolean = items().all { it.backpackCount() >= it.minAmount } && containsAllEquipment()

    fun equipmentLoadout(): EquipmentLoadout {
        val loadout = EquipmentLoadout(this.javaClass.simpleName)
        equipment().forEach { (slot, item) ->
            loadout.add(
                ItemEntryBuilder().key(item).equipmentSlot(slot).quantity(1).build()
            )
        }
        return loadout
    }

    fun backpackLoadout(): BackpackLoadout {
        val loadout = BackpackLoadout(this.javaClass.simpleName)
        MUST_HAVE_INVENTORY.forEach { loadout.add(ItemEntryBuilder().key(it).quantity(1).build()) }
        items().forEach { loadout.add(it.toItemEntry()) }
        return loadout
    }

    private fun containsAllEquipment(): Boolean {
        val equipment = equipment().values.toTypedArray()
        val count = Inventories.equipment()
            .getCount { it.nameContains(*equipment).results() } + Inventories.backpack()
            .getCount { it.nameContains(*equipment).results() }
        return count >= equipment.size
    }
}
