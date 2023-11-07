package api.pouch

import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.component.Item
import kotlin.math.min


internal enum class Pouch(
    private val baseHoldAmount: Int,
    private val degradedBaseHoldAmount: Int = -1
) {
    SMALL(3),
    MEDIUM(6, 3),
    LARGE(9, 7),
    GIANT(12, 9),
    COLOSSAL(40, 35);

    var holding = 0
    var degraded = false
    var unknown = true

    val holdAmount: Int
        get() = if (degraded) degradedBaseHoldAmount else baseHoldAmount

    val remaining: Int
        get() {
            val holdAmount = if (degraded) degradedBaseHoldAmount else baseHoldAmount
            return holdAmount - holding
        }

    val isFull: Boolean
        get() {
            return holding == holdAmount
        }

    fun addHolding(delta: Int) {
        holding += delta
        val holdAmount = if (degraded) degradedBaseHoldAmount else baseHoldAmount
        if (holding < 0) {
            holding = 0
        }
        if (holding > holdAmount) {
            holding = holdAmount
        }
    }

    fun degrade(state: Boolean) {
        if (state != degraded) {
            degraded = state
            val holdAmount = if (degraded) degradedBaseHoldAmount else baseHoldAmount
            holding = min(holding, holdAmount)
        }
    }

    fun item(): Item? {
        val itemIds = when (this) {
            SMALL -> intArrayOf(5509)
            MEDIUM -> intArrayOf(5510, 5511)
            LARGE -> intArrayOf(5512, 5513)
            GIANT -> intArrayOf(5514, 5515)
            COLOSSAL -> intArrayOf(26784, 26786, 26906)
        }
        return Backpack.backpack().query().ids(*itemIds).results().firstOrNull()
    }

    companion object {
        fun forItem(itemId: Int): Pouch? {
            return when (itemId) {
                5509 -> SMALL
                5510, 5511 -> MEDIUM
                5512, 5513 -> LARGE
                5514, 5515 -> GIANT
                26784, 26786, 26906 -> COLOSSAL
                else -> null
            }
        }

        fun forName(name: String): Pouch? {
            return when (name) {
                "Small pouch" -> SMALL
                "Medium pouch" -> MEDIUM
                "Large pouch" -> LARGE
                "Giant pouch" -> GIANT
                "Colossal pouch" -> COLOSSAL
                else -> null
            }
        }

        fun pouchesInBackpack(): List<Pouch> {
            return Backpack.backpack().items.map { forItem(it.id) }.filterNotNull()
        }

        fun allPouchesFilled() = pouchesInBackpack().all { it.remaining == 0 }

        fun allPouchesEmpty() = pouchesInBackpack().all { it.holding == 0 }

        fun fillPouch(): Boolean {
            val smallestRemainingPouch = Pouch.pouchesInBackpack().filter { !it.isFull }.minByOrNull { it.holdAmount }
            if (smallestRemainingPouch != null) {
                // Check if we have enough to fill it
                val essenceInBackpack = Backpack.backpack().query().names("Guardian essence").results().size
                if (essenceInBackpack >= smallestRemainingPouch.remaining) {
                    return smallestRemainingPouch.item()?.interact("Fill") ?: false
                }
            }
            return false
        }

        fun emptyPouches() {
            Pouch.pouchesInBackpack().forEach {
                if (it.holding > 0) {
                    it.item()?.interact("Empty")
                }
            }
        }
    }
}