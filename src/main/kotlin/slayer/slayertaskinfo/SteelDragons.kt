package slayer.slayertaskinfo

import api.teleport.NexusPortal
import org.rspeer.game.House
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.area.Area
import org.rspeer.game.script.Task
import slayer.ScriptContext
import slayer.data.Bracelet
import slayer.data.ItemConfig
import slayer.data.Settings
import slayer.dungeon.KourendCatacombs
import slayer.remainingAntifireSeconds
import slayer.teleportHouse
import slayer.travelNexusPortal

class SteelDragons(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    override fun monsterNames(): Array<String> = arrayOf("Steel dragon")

    override fun standingArea(): Area = KourendCatacombs.Location.STEEL_DRAGONS_WEST.area

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)

    override fun equipment(): Map<Equipment.Slot, String> = Settings.DRAGON_GEAR

    override fun items(): List<ItemConfig> {
        return super.items() + listOf(
            ItemConfig(Settings.ANTIFIRE_POTION.allDoseNames, 2, if (remainingAntifireSeconds() > 0) 0 else 1),
        )
    }

    override fun bracelet(): Bracelet = Bracelet.EXPEDITIOUS

    override fun walk(executingTask: Task) {
        if (KourendCatacombs.outsideMainEntrance() || KourendCatacombs.isInsideDungeon()) {
            KourendCatacombs.walk(KourendCatacombs.Location.STEEL_DRAGONS_WEST)
            return
        }

        if (House.isInside()) {
            travelNexusPortal(NexusPortal.Location.KOUREND_CASTLE)
            return
        }

        teleportHouse()
    }
}