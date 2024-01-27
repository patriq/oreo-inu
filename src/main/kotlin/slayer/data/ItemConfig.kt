package slayer.data

import org.rspeer.game.Game
import org.rspeer.game.component.Inventories
import org.rspeer.game.config.item.entry.ItemEntry
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder

class ItemConfig private constructor(
    val ids: IntArray,
    val names: Array<String>,
    val amount: Int,
    val minAmount: Int
) {
    constructor(ids: IntArray, amount: Int, minAmount: Int) : this(ids, arrayOf<String>(), amount, minAmount)

    constructor(names: Array<String>, amount: Int, minAmount: Int) : this(intArrayOf(), names, amount, minAmount)

    fun backpackCount(): Int {
        return Inventories.backpack()
            .getCount({ query -> query.filter { names.contains(it.name) || ids.contains(it.id) }.results() }, true)
    }

    fun toItemEntry(): ItemEntry {
        val key = if (ids.isNotEmpty()) {
            Game.getClient().getItemDefinition(ids.last()).name
        } else if (names.isNotEmpty()) {
            names.last()
        } else throw IllegalStateException("Item key not found")

        val builder = ItemEntryBuilder()
        builder.key(key)
        builder.quantity(amount)
        Settings.RESTOCK_STRATEGIES[key]?.let {
            builder.restockMeta(it)
        }
        return builder.build()
    }
}