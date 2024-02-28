package herblore

import api.script.GuiceTaskScript
import com.google.inject.AbstractModule
import com.google.inject.Module
import com.google.inject.name.Names
import org.rspeer.commons.StopWatch
import org.rspeer.commons.logging.Log
import org.rspeer.event.ScriptService
import org.rspeer.event.Subscribe
import org.rspeer.game.Game
import org.rspeer.game.Keyboard
import org.rspeer.game.adapter.component.inventory.Bank
import org.rspeer.game.component.*
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder
import org.rspeer.game.config.item.loadout.BackpackLoadout
import org.rspeer.game.event.AnimationEvent
import org.rspeer.game.event.ChatMessageEvent
import org.rspeer.game.event.OpenSubInterfaceEvent
import org.rspeer.game.scene.Players
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import org.rspeer.game.script.event.ScriptConfigEvent
import org.rspeer.game.script.meta.ScriptMeta
import org.rspeer.game.script.meta.paint.PaintBinding
import org.rspeer.game.script.meta.paint.PaintScheme
import org.rspeer.game.script.meta.ui.ScriptOption
import org.rspeer.game.script.meta.ui.ScriptUI
import org.rspeer.game.service.inventory.InventoryCache
import org.rspeer.game.service.stockmarket.StockMarketService
import java.util.function.Supplier
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider


@ScriptMeta(
    name = "Herblore",
    developer = "Oreo",
    version = 1.00,
    desc = "Makes potions",
    paint = PaintScheme::class,
    regions = [-3]
)
@ScriptUI(
    ScriptOption(name = "Recipe", type = Recipe::class),
    ScriptOption(name = "Chemistry amulet", type = Boolean::class)
)
@ScriptService(StockMarketService::class, InventoryCache::class)
class Herblore : GuiceTaskScript() {
    @PaintBinding("Runtime")
    private val runtime = StopWatch.start()

    @PaintBinding("Task")
    private val taskName: Supplier<String> = Supplier { manager.lastTaskName }

    @PaintBinding("Experience")
    private val skills = arrayOf(Skill.HERBLORE)

    @PaintBinding("Potions made", rate = true)
    private var potionsMade: Int = 0

    @PaintBinding("Extra doses", rate = true)
    private var extraDoses: Int = 0

    @PaintBinding("Recipe")
    private val recipe = Supplier { config.recipe }

    @PaintBinding("Chemistry amulet")
    private val chemistryAmulet = Supplier { config.chemistry }

    private var config = Config(Recipe.NONE, false)

    override fun tasks(): Array<Class<out Task>> {
        return arrayOf(
            MakePotionTask::class.java,
            BankTask::class.java
        )
    }

    @Subscribe
    fun onConfigure(event: ScriptConfigEvent) {
        config = Config(event.source.get("Recipe"), event.source.get("Chemistry amulet"))
    }

    @Subscribe
    fun onMessageEvent(event: ChatMessageEvent) {
        val message = event.contents
        if (message.contains("mix")) {
            potionsMade++
        }

        if (message.contains("amulet of chemistry helps")) {
            extraDoses++
        }
    }

    override fun modules(): List<Module> {
        return listOf(object : AbstractModule() {
            override fun configure() {
                bind(Config::class.java).toProvider { config }
            }
        })
    }

    @TaskDescriptor(
        name = "Make potion",
        blocking = true,
        blockIfSleeping = true,
        register = true,
    )
    class MakePotionTask @Inject constructor(private val config: Provider<Config>) : Task() {
        private var lastAnimationTick = 0

        override fun execute(): Boolean {
            val recipe = config.get().recipe
            if (recipe == Recipe.NONE) {
                return true
            }

            if (!recipe.loadout.hasMinimumAmounts()) {
                lastAnimationTick = 0
                return false
            }

            // Sleep until animation is finished
            if (Game.getTick() - lastAnimationTick < 6) {
                return true
            }

            val production = Production.getActive()
            if (production == null) {
                val first = Inventories.backpack().getItems(recipe.firstItem).firstOrNull() ?: return false
                val second = Inventories.backpack().getItems(recipe.secondItem).firstOrNull() ?: return false
                Inventories.backpack().use(first, second)
                return true
            }

            production.amount = Production.Amount.ALL
            production.initiate { it.results().first() }
            return true
        }

        @Subscribe
        fun onAnimationChanged(event: AnimationEvent) {
            if (event.source == Players.self() && event.current != -1) {
                lastAnimationTick = Game.getTick()
            }
        }

        @Subscribe
        fun onWidgetOpen(event: OpenSubInterfaceEvent) {
            if (event.composite == InterfaceComposite.CREATION) {
                Keyboard.pressEventKey(32)
                lastAnimationTick = Game.getTick() + 1
            }
        }
    }

    @TaskDescriptor(
        name = "Bank",
        blocking = true,
        blockIfSleeping = true,
        stoppable = true
    )
    class BankTask @Inject constructor(private val config: Provider<Config>) : Task() {
        private companion object {
            private const val AMULET_OF_CHEMISTRY = "Amulet of chemistry"
        }

        private var stop = false

        override fun execute(): Boolean {
            if (stop) {
                Game.getClient().gameState = Game.STATE_CREDENTIALS_SCREEN
                Log.warn("Out of items")
                return true
            }

            if (!Bank.isOpen()) {
                Bank.open()
            } else {
                // Withdraw and wear chemistry amulet
                if (config.get().chemistry) {
                    // Check if equipped
                    val hasAmulet = Inventories.equipment().getItems(AMULET_OF_CHEMISTRY).isNotEmpty()
                    if (!hasAmulet) {
                        // Equip amulet if in backpack
                        val amulet = Inventories.backpack().getItems(AMULET_OF_CHEMISTRY).firstOrNull()
                        if (amulet != null) {
                            amulet.interact("Wear")
                        } else {
                            // Withdraw amulet
                            val amulet = Inventories.bank().getItems(AMULET_OF_CHEMISTRY).firstOrNull()
                            if (amulet != null) {
                                Inventories.bank().depositInventory()
                                amulet.interact("Withdraw-1")
                                return false
                            } else {
                                Log.severe("No chemistry amulet")
                                return true
                            }
                        }
                    }
                }

                val loadout = config.get().recipe.loadout.clone()
                loadout.setOutOfItemListener { stop = true }
                loadout.withdraw(Inventories.bank())
                Interfaces.closeSubs()
                EnterInput.asyncClose()
            }
            return false
        }
    }

}


fun BackpackLoadout.hasMinimumAmounts(): Boolean {
    val backpack = Inventories.backpack()
    return this.all { entry ->
        backpack.getCount(
            { it.names(entry.key).results() },
            entry.isStackable
        ) >= entry.minimumQuantity
    }
}

data class Config(val recipe: Recipe, val chemistry: Boolean)

enum class Recipe(val loadout: BackpackLoadout, val firstItem: String, val secondItem: String) {
    NONE(
        BackpackLoadout("none"),
        "",
        ""
    ),
    SUPER_COMBAT(
        BackpackLoadout("super_combat").apply {
            add(ItemEntryBuilder().key("Torstol").quantity(1, 7).build())
            add(ItemEntryBuilder().key("Super attack(4)").quantity(1, 7).build())
            add(ItemEntryBuilder().key("Super strength(4)").quantity(1, 7).build())
            add(ItemEntryBuilder().key("Super defence(4)").quantity(1, 7).build())
        },
        "Torstol",
        "Super attack(4)"
    ),
    STAMINA_4(
        BackpackLoadout("stamina").apply {
            add(ItemEntryBuilder().key("Amylase crystal").quantity(4, 108).stackable(true).build())
            add(ItemEntryBuilder().key("Super energy(4)").quantity(1, 27).build())
        },
        "Amylase crystal",
        "Super energy(4)"
    ),
    STAMINA_3(
        BackpackLoadout("stamina").apply {
            add(ItemEntryBuilder().key("Amylase crystal").quantity(3, 81).stackable(true).build())
            add(ItemEntryBuilder().key("Super energy(3)").quantity(1, 27).build())
        },
        "Amylase crystal",
        "Super energy(3)"
    ),
    PRAYER(
        BackpackLoadout("prayer").apply {
            add(ItemEntryBuilder().key("Ranarr potion (unf)").quantity(1, 14).build())
            add(ItemEntryBuilder().key("Snape grass").quantity(1, 14).build())
        },
        "Ranarr potion (unf)",
        "Snape grass"
    ),
    DIVINE_SUPER_COMBAT_3(
        BackpackLoadout("divine").apply {
            add(ItemEntryBuilder().key("Crystal dust").quantity(3, 81).stackable(true).build())
            add(ItemEntryBuilder().key("Super combat potion(3)").quantity(1, 27).build())
        },
        "Crystal dust",
        "Super combat potion(3)"
    ),
}