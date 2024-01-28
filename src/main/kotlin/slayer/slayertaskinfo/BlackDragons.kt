package slayer.slayertaskinfo

import api.containsPlayer
import org.rspeer.game.House
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.position.Position
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.web.Web
import slayer.ScriptContext
import slayer.data.Bracelet
import slayer.data.ItemConfig
import slayer.data.Settings.ANTIFIRE_POTION
import slayer.data.Settings.DRAGON_GEAR
import slayer.remainingAntifireSeconds
import slayer.teleportHouse
import slayer.teleportMythicalCape

class BlackDragons(ctx: ScriptContext) : BaseSlayerTaskInfo(ctx) {
    private companion object {
        private val STANDING_AREA = Area.rectangular(1957, 8978, 1932, 8963, 1)
        private val MYTHIC_DUNGEON = Area.rectangular(1960, 9022, 1912, 8962, 1)
        private val MYTHS_GUILD = Area.rectangular(2451, 2854, 2463, 2842, 0)
    }

    override fun monsterNames(): Array<String> = arrayOf("Black dragon")

    override fun standingArea(): Area = STANDING_AREA

    override fun prayers(): Array<Prayer> = arrayOf(Prayer.Modern.PROTECT_FROM_MELEE)

    override fun equipment(): Map<Equipment.Slot, String> = DRAGON_GEAR

    override fun items(): List<ItemConfig> {
        return super.items() + listOf(
            ItemConfig(ANTIFIRE_POTION.allDoseNames, 1, if (remainingAntifireSeconds() > 0) 0 else 1),
        )
    }

    override fun bracelet(): Bracelet = Bracelet.EXPEDITIOUS

    override fun walk(executingTask: Task) {
        if (MYTHIC_DUNGEON.containsPlayer()) {
            Web.pathTo(Position(1944, 8970, 1))?.step()
            return
        }

        if (MYTHS_GUILD.containsPlayer()) {
            SceneObjects.query().names("Mythic Statue").results().nearest()?.interact("Enter")
            return
        }

        if (House.isInside()) {
            teleportMythicalCape()
            return
        }

        teleportHouse()
    }
}