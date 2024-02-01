package slayer.slayertaskinfo

import api.teleport.DigsitePendant
import org.rspeer.game.House
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.area.Area
import org.rspeer.game.position.area.RectangularArea
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import slayer.ScriptContext
import slayer.data.Bracelet
import slayer.data.ItemConfig
import slayer.data.Settings
import slayer.dungeon.Lithkren
import slayer.remainingAntifireSeconds
import slayer.teleportDigsitePendant
import slayer.teleportHouse

class RuneDragons(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    override fun monsterNames(): Array<String> = arrayOf("Rune dragon")

    override fun standingArea(): Area = Lithkren.getArea(Lithkren.Location.RUNE_DRAGONS)

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MAGIC)

    override fun equipment(): Map<Equipment.Slot, String> = Settings.DRAGON_GEAR + mapOf(
        Equipment.Slot.FEET to "Insulated boots"
    )

    override fun items(): List<ItemConfig> = listOf(
        ItemConfig(Settings.COMBAT_BOOSTING_POTION.allDoseNames, 1, 0),
        ItemConfig(Settings.PRAYER_POTION.allDoseNames, 2, 0),
        ItemConfig(intArrayOf(Settings.FOOD), 15, 0),
        ItemConfig(Settings.ANTIFIRE_POTION.allDoseNames, 1, if (remainingAntifireSeconds() > 0) 0 else 1),
    )

    override fun bracelet(): Bracelet = Bracelet.EXPEDITIOUS

    override fun walk(executingTask: Task) {
        if (Lithkren.isInside()) {
            Lithkren.walk(Lithkren.Location.RUNE_DRAGONS)
            return
        }

        if (House.isInside()) {
            teleportDigsitePendant(DigsitePendant.Location.LITHKREN)
            return
        }

        teleportHouse()
    }
}