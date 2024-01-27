package slayer.slayertaskinfo

import org.rspeer.game.adapter.component.inventory.Equipment
import slayer.ScriptContext
import slayer.data.ItemConfig
import slayer.data.Settings.BASE_GEAR
import slayer.data.Settings.COMBAT_BOOSTING_POTION
import slayer.data.Settings.FOOD
import slayer.data.Settings.PRAYER_POTION

abstract class BaseSlayerTaskInfo(ctx: ScriptContext) : SlayerTaskInfo(ctx) {
    override fun equipment(): Map<Equipment.Slot, String> = BASE_GEAR

    override fun items(): List<ItemConfig> = listOf(
        ItemConfig(COMBAT_BOOSTING_POTION.allDoseNames, 1, 0),
        ItemConfig(PRAYER_POTION.allDoseNames, 8, 0),
        ItemConfig(intArrayOf(FOOD), 5, 0),
    )
}