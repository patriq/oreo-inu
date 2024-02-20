package slayer

import org.rspeer.commons.StopWatch
import org.rspeer.event.ScriptService
import org.rspeer.game.Game
import org.rspeer.game.component.InventoryType
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskScript
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme
import org.rspeer.game.script.tools.RestockTask
import org.rspeer.game.service.inventory.InventoryCache
import org.rspeer.game.service.stockmarket.StockMarketService
import slayer.data.SlayerTask
import slayer.listener.SlayerChatListener
import slayer.task.*
import java.util.function.Supplier

@ScriptMeta(
    developer = "Oreo",
    name = "Slayer",
    desc = "Does slayer for you",
    version = 1.0,
    paint = PaintScheme::class,
    regions = [-3]
)
@ScriptService(StockMarketService::class, InventoryCache::class)
class Slayer : TaskScript() {
    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Slayer task")
    private var slayerTask: Supplier<SlayerTask> = Supplier { scriptContext.getTask() }

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.SLAYER)

    @PaintBinding("Task")
    private var task: Supplier<String> = Supplier { manager.lastTaskName }

    override fun initialize() {
        val cache = injector.getInstance(InventoryCache::class.java)
        cache.submit(InventoryType.BANK, 1220)

        val dispatcher = Game.getEventDispatcher()
        dispatcher.subscribe(SlayerChatListener(scriptContext))
    }

    override fun shutdown() {
        val dispatcher = Game.getEventDispatcher()
        dispatcher.unsubscribe(SlayerChatListener(scriptContext))
    }

    override fun tasks(): Array<Class<out Task>> {
        return arrayOf(
            StopScriptTask::class.java,
            ForceLogTask::class.java,

            // High priority tasks
            RestockTask::class.java,
            CloseGeTask::class.java,
            CloseLevelUpDialogTask::class.java,
            ToggleRunTask::class.java,

            // Core logic to check/get/skip tasks and handle prayer (in case we need to stop praying)
            CheckSlayerAssignmentTask::class.java,
            GetSlayerAssignmentTask::class.java,
            SkipSlayerAssignmentTask::class.java,

            // Banking and walking to area
            BankingTask::class.java,
            WalkToTaskAreaTask::class.java,

            // Combat related
            PrayTask::class.java,
            WearCombatEquipmentTask::class.java,
            FlickAggressivePrayerTask::class.java,
            EatAndDrinkTask::class.java,
            DodgeAttackTask::class.java,
            LootTask::class.java,
            UseItemTask::class.java,
            AttackSuperiorTask::class.java,
            AttackTask::class.java,
        )
    }

    private val scriptContext: ScriptContext
        get() = injector.getInstance(ScriptContext::class.java)
}