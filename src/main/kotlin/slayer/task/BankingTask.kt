package slayer.task

import api.containsPlayer
import api.teleport.JewelleryBox
import org.rspeer.commons.logging.Log
import org.rspeer.game.House
import org.rspeer.game.adapter.component.StockMarketTransaction
import org.rspeer.game.adapter.component.StockMarketable
import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.component.*
import org.rspeer.game.config.item.entry.ItemEntry
import org.rspeer.game.config.item.loadout.BackpackLoadout
import org.rspeer.game.movement.Movement
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import org.rspeer.game.script.tools.RestockTask
import org.rspeer.game.service.inventory.InventoryCache
import org.rspeer.game.service.stockmarket.StockMarketEntry
import org.rspeer.game.service.stockmarket.StockMarketService
import slayer.*
import slayer.data.Constants.HERB_SACK
import slayer.data.Constants.SEED_BOX
import java.util.*
import java.util.function.Consumer
import javax.inject.Inject


@TaskDescriptor(
    name = "Banking",
    blocking = true,
    blockIfSleeping = true,
)
class BankingTask @Inject constructor(
    private val ctx: ScriptContext,
    private val restockTask: RestockTask,
    private val stockMarket: StockMarketService,
    private val inventoryCache: InventoryCache
) : Task() {
    private companion object {
        private val BANKING_AREA = Area.rectangular(2434, 3100, 2447, 3079, 0)
    }

    init {
        val original = restockTask.walkToGrandExchangeFunction
        restockTask.walkToGrandExchangeFunction = WalkToGe(original)
        restockTask.collectionStrategy = RestockTask.CollectionStrategy { StockMarketTransaction.CollectionAction.BANK }
    }

    override fun execute(): Boolean {
        if (!shouldRun()) {
            return false
        }

        val taskInfo = ctx.currentTaskInfo() ?: return false

        // Travel to bank
        if (!BANKING_AREA.containsPlayer()) {
            if (House.isInside()) {
                // Restore stats
                if (shouldRestoreStats()) {
                    restoreStats()
                    return true
                }

                // Use jewelry box
                teleportJewelleryBox(JewelleryBox.Location.CASTLE_WARS)
                return true
            }

            teleportHouse()
            return true
        }

        // Get all gear first
        val equipmentLoadout = taskInfo.equipmentLoadout()
        if (!equipmentLoadout.isWorn) {
            // Equip gear if we have it
            if (equipmentLoadout.bagged.isNotEmpty()) {
                equipmentLoadout.equip()
                return true
            }

            // Withdraw gear otherwise
            val backpack = equipmentLoadout.toBackpackLoadout("slayer")
            Bank.open()
            backpack.withdraw(Inventories.bank())
            return true
        }

        // Get rest of loadout
        val backpackLoadout = taskInfo.backpackLoadout()
        backpackLoadout.outOfItemListener = OutOfItemListener(backpackLoadout, stockMarket, inventoryCache)
        if (!backpackLoadout.isBagged) {
            if (!Bank.isOpen()) {
                Bank.open()
                return true
            }

            // Deposit seeds and herbs
            Inventories.backpack().getItems(HERB_SACK).firstOrNull()?.interact("Empty")
            Inventories.backpack().getItems(SEED_BOX).firstOrNull()?.interact("Empty")

            // Withdraw loadout
            backpackLoadout.withdraw(Inventories.bank())
            return true
        }
        return true
    }

    private fun shouldRun(): Boolean {
        val taskInfo = ctx.currentTaskInfo() ?: return false
        if (!BANKING_AREA.containsPlayer()) {
            if (taskInfo.standingArea().containsPlayer()) {
                return !taskInfo.readyForFight()
            }
        }

        val inventoryReady = taskInfo.backpackReady()
        if (inventoryReady && Bank.isOpen()) {
            Interfaces.closeSubs()
        }
        return !inventoryReady
    }

    private class WalkToGe(private val original: Consumer<Task>): Consumer<Task> {
        private companion object {
            private val GE_AREA = Area.rectangular(3145, 3510, 3184, 3466, 0)
        }

        override fun accept(t: Task) {
            if (Bank.isOpen()) {
                Interfaces.closeSubs()
            }

            if (GE_AREA.containsPlayer()) {
                original.accept(t)
                return
            }

            if (House.isInside()) {
                teleportJewelleryBox(JewelleryBox.Location.GRAND_EXCHANGE)
                return
            }

            teleportHouse()
        }
    }

    private class OutOfItemListener(
        private val loadout: BackpackLoadout,
        private val stockMarket: StockMarketService,
        private val cache: InventoryCache
    ) : Consumer<ItemEntry> {
        override fun accept(t: ItemEntry) {
            for (entry in loadout) {
                val meta = entry.restockMeta ?: continue

                if (cache.isLoaded(InventoryType.BANK)) {
                    val remaining: Int = entry.getContained(cache.query(InventoryType.BANK))
                        .limit(1)
                        .stream()
                        .mapToInt(Item::getStackSize).sum()
                    val amount = meta.purchaseAmount - remaining
                    val fraction = (meta.purchaseAmount * 0.50).toInt()
                    Log.info("Remaining ${entry.key} in bank: $remaining.")
                    if (amount < fraction) {
                        //don't buy if we already have 50% of the purchase amount banked
                        continue
                    }

                    Log.info("Submitting buy order for ${entry.key} x $amount")
                    val adjusted = StockMarketEntry(meta.itemId, amount, meta.pricePerItem)
                    stockMarket.submit(StockMarketable.Type.BUY, adjusted)
                }
            }
        }
    }
}