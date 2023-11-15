package gotr

import api.service.PouchTracker
import api.script.GuiceTaskScript
import gotr.task.EnterMinigame
import gotr.task.RepairPouch
import gotr.task.minigame.*
import gotr.task.minigame.blocking.*
import org.rspeer.commons.StopWatch
import org.rspeer.event.ScriptService
import org.rspeer.game.Game
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.script.Task
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme
import java.util.function.Supplier

@ScriptMeta(
    name = "Guardians of the Rift",
    developer = "Oreo",
    version = 1.00,
    desc = "Completes the Guardians of the Rift minigame",
    paint = PaintScheme::class,
    regions = [-3]
)
@ScriptService(PouchTracker::class)
class GuardiansOfTheRift : GuiceTaskScript() {
    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.RUNECRAFTING)

    @PaintBinding("Task")
    private val taskName = Supplier { manager.lastTaskName }

    @PaintBinding("State")
    var minigameState = Supplier { minigameContext.state }

    @PaintBinding("Games completed")
    var gamesCompleted = Supplier { minigameContext.gamesCompleted }

    @PaintBinding("Average reward points per game")
    var averagePointsPerGame = Supplier { minigameContext.averagePointsPerGame.toFloat() }

    @PaintBinding("Current reward points")
    var rewardPoints =
        Supplier { "${minigameContext.elementalRewardPoints} / ${minigameContext.catalyticRewardPoints}" }

    @PaintBinding("Potential reward points")
    var potentialRewardPoints =
        Supplier { "${minigameContext.potentialElementalRewadPoints} / ${minigameContext.potentialCatalyticRewardPoints}" }

    override fun initialize() {
        val eventDispatcher = Game.getEventDispatcher()
        eventDispatcher.subscribe(minigameContext)
    }

    override fun loop(): Int {
        return 2000
    }

    override fun shutdown() {
        val eventDispatcher = Game.getEventDispatcher()
        eventDispatcher.unsubscribe(minigameContext)
    }

    override fun tasks(): Array<Class<out Task>> {
        return arrayOf(
            RepairPouch::class.java,
            EnterMinigame::class.java,

            // Minigames tasks
            EvaluateMinigameState::class.java,

            DropPortalTalisman::class.java,
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