package api

import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.component.Inventories
import org.rspeer.game.component.Item
import org.rspeer.game.config.item.loadout.EquipmentLoadout
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.Players

fun Area.containsPlayer(): Boolean {
    return this.contains(Players.self())
}

fun Backpack.countDoses(name: String): Int {
    return this.getItems(name).results.sumOf { it.name.filter { char -> char.isDigit() }.toInt() }
}

/**
 * Equips each bagged item in the loadout only once. If there are multiple items of the same type in the backpack,
 * it will only equip the first. Ignores quantity because why do we care about that?
 */
fun EquipmentLoadout.equipEach(): Boolean {
    if (this.isWorn) {
        return false
    }

    // Equip doesn't work properly when there are more items in the backpack than needed
    val neededGear = map { it.key }.toSet().toTypedArray()
    val gearInBackpack = Inventories.backpack().getItems(*neededGear).groupBy { it.name }
    for ((_, items) in gearInBackpack) {
        items.first().interact("Wear", "Equip", "Wield")
    }
    return gearInBackpack.isNotEmpty()
}

/**
 * #bagged returns a list of items that are in the backpack and in the loadout even when they are already equipped.
 * This function returns a list of items that are in the backpack and in the loadout but not equipped.
 */
fun EquipmentLoadout.missingBagged(): List<Item> {
    // Calculate what's missing and see if it is bagged
    val missingItems = mutableSetOf<String>()
    for (slot in Equipment.Slot.values()) {
        val currentItem = Inventories.equipment().getItemAt(slot)
        val neededItem = this[slot] ?: continue
        if (currentItem?.name != neededItem.key) {
            missingItems.add(neededItem.key)
        }
    }
    return this.bagged.results.filter { it.name in missingItems }
}