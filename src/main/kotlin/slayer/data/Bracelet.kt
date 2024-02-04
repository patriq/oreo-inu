package slayer.data

import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.config.item.entry.ItemEntry
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder

enum class Bracelet(val itemName: String) {
    NONE(""),
    EXPEDITIOUS("Expeditious bracelet"),
    SLAUGHTER("Bracelet of slaughter");

    fun toItemEntry(quantity: Int = 1): ItemEntry? {
        if (this == NONE) {
            return null
        }
        return ItemEntryBuilder().key(itemName).quantity(quantity).equipmentSlot(Equipment.Slot.HANDS).build()
    }
}