package gotr

import api.pouch.PouchTracker
import api.script.GuiceTaskScript
import gotr.task.EnterMinigame
import gotr.task.RepairPouch
import gotr.task.minigame.*
import gotr.task.minigame.blocking.LeaveAltar
import gotr.task.minigame.blocking.LeaveHugeFragmentMine
import gotr.task.minigame.blocking.LeaveLargeFragmentMine
import org.rspeer.commons.StopWatch
import org.rspeer.game.Game
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.script.Task
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme
import java.util.function.IntSupplier
import java.util.function.Supplier

@ScriptMeta(
    name = "Guardians of the Rift",
    developer = "Oreo",
    version = 1.00,
    desc = "Completes the Guardians of the Rift minigame",
    paint = PaintScheme::class,
    regions = [-3]
)
class GuardiansOfTheRift : GuiceTaskScript() {
    private val pouchTracker = PouchTracker()

    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.RUNECRAFTING)

    @PaintBinding("Task")
    private val taskName = Supplier { manager.lastTaskName }

    @PaintBinding("State")
    var minigameState = Supplier { minigameContext.state }

    @PaintBinding("Is minigame playing")
    var isMinigameRunning = Supplier { minigameContext.isMinigameRunning }

    @PaintBinding("Ticks til portal spawn")
    val ticksTilPortalSpawn = IntSupplier { minigameContext.ticksTilPortalSpawn }

    @PaintBinding("Altars")
    var activeAltars = Supplier { minigameContext.activeAltars }

    @PaintBinding("Guardian power")
    var guardianPower = Supplier { minigameContext.guardianPower }

    @PaintBinding("Portal up")
    var portalUp = Supplier { minigameContext.portalUp }

    @PaintBinding("Seconds til altar spawn")
    var nextRunecraftingAltarSpawnSeconds = Supplier { minigameContext.nextRunecraftingAltarSpawnSeconds }

    @PaintBinding("Reward points")
    var rewardPoints = Supplier { "${minigameContext.elementalRewardPoints} / ${minigameContext.catalyticRewardPoints}" }

    @PaintBinding("Potential reward points")
    var potentialRewardPoints = Supplier { "${minigameContext.potentialElementalRewadPoints} / ${minigameContext.potentialCatalyticRewardPoints}" }

    override fun initialize() {
        val eventDispatcher = Game.getEventDispatcher()
        eventDispatcher.subscribe(pouchTracker)
        eventDispatcher.subscribe(minigameContext)
    }

    override fun loop(): Int {
        return 2000
    }

    override fun shutdown() {
        val eventDispatcher = Game.getEventDispatcher()
        eventDispatcher.unsubscribe(minigameContext)
        eventDispatcher.unsubscribe(pouchTracker)
    }

    override fun tasks(): Array<Class<out Task>> {
        return arrayOf(
            EnterMinigame::class.java,

            // Generic tasks
            RepairPouch::class.java,
            DropPortalTalisman::class.java,

            // Minigames tasks
            EvaluateMinigameState::class.java,

            LeaveAltar::class.java,
            LeaveHugeFragmentMine::class.java,
            LeaveLargeFragmentMine::class.java,

            DepositRunes::class.java,
            CollectUnchargedCells::class.java,
            MineInitialFragments::class.java,
            CraftEssence::class.java,
            Runecraft::class.java,
            FeedGuardian::class.java,
            PlaceBarrierCell::class.java,
            MineHugeFragments::class.java,
        )
    }

    private val minigameContext
        get() = injector.getInstance(MinigameContext::class.java)

}