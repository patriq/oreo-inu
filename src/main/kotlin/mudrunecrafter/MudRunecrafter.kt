package mudrunecrafter

import api.MoreVars
import api.script.GuiceTaskScript
import api.service.ItemChargeService
import api.service.PouchTracker
import mudrunecrafter.task.*
import org.rspeer.commons.PriceCheck
import org.rspeer.commons.StopWatch
import org.rspeer.event.ScriptService
import org.rspeer.event.Subscribe
import org.rspeer.game.Game
import org.rspeer.game.component.EffectBar
import org.rspeer.game.component.EffectType
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.event.InventoryEvent
import org.rspeer.game.event.RenderEvent
import org.rspeer.game.event.TickEvent
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.Projection
import org.rspeer.game.script.Task
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme
import java.util.function.Supplier


@ScriptMeta(
    name = "Runecrafting",
    developer = "Oreo",
    version = 1.00,
    desc = "Runecrafts runes",
    paint = PaintScheme::class,
    regions = [-3]
)
@ScriptService(PouchTracker::class, ItemChargeService::class)
class MudRunecrafter : GuiceTaskScript() {
    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.RUNECRAFTING)

    @PaintBinding("Task")
    private val taskName = Supplier { manager.lastTaskName }

    @PaintBinding("Runes", rate = true)
    private var runesCrafted = 0

    @PaintBinding("GP", rate = true)
    private val gp = Supplier { runesCrafted * PriceCheck.lookup(4698).low }

    @PaintBinding("Runs", rate = true)
    private var runs = 0

    private var inWaterAltar = false

    override fun loop(): Int {
        return 200000
    }

    override fun tasks(): Array<Class<out Task>> {
        return arrayOf(
            StopScript::class.java,
            CheckCharges::class.java,
            Runecraft::class.java,
            WalkToAltar::class.java,
            Banking::class.java,
            OpenBank::class.java,
        )
    }

    @Subscribe
    fun onInventoryChange(event: InventoryEvent) {
        if (event.source == 93 && event.next.left == 4698) {
            val delta = if (event.current.left == 4698) {
                event.next.right - event.current.right
            } else {
                event.next.right
            }
            runesCrafted += delta
        }
    }

    @Subscribe
    fun onTickEvent(event: TickEvent) {
        if (isInWaterAltar() && !inWaterAltar) {
            runs++
        }
        inWaterAltar = isInWaterAltar()
    }

    @Subscribe
    fun onRender(event: RenderEvent) {
        val poly = Projection.getPositionPolygon(Projection.Canvas.VIEWPORT, Players.self()?.position) ?: return
        event.source.drawPolygon(poly)
    }

    companion object {
        fun isInWaterAltar(): Boolean = Game.getClient().mapRegions.any { it == 10827 }
        fun isOutsideAltar(): Boolean = Game.getClient().mapRegions.any { it == 12593 }
        fun inCraftingGuild(): Boolean = Game.getClient().mapRegions.any { it == 11571 }
        fun isTeleporting(): Boolean = Players.self()?.let { it.animationId == 714 } ?: false
        fun isMagicImbueActive(): Boolean {
            val imbueTick = EffectBar.getValue(EffectType.MAGIC_IMBUE)
            return imbueTick > 0 && (imbueTick + 22) >= MoreVars.getServerTick()
        }
    }
}