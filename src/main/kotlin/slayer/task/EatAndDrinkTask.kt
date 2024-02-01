package slayer.task

import api.containsPlayer
import org.rspeer.commons.math.Random
import org.rspeer.game.combat.Combat
import org.rspeer.game.component.Inventories
import org.rspeer.game.component.tdi.Prayers
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.component.tdi.Skills
import org.rspeer.game.effect.Health
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.ScriptContext
import slayer.data.Potion
import slayer.data.PotionType
import slayer.data.Settings.FOOD
import slayer.data.SlayerTask
import slayer.remainingAntifireSeconds
import slayer.teleportHouse
import javax.inject.Inject

@TaskDescriptor(
    name = "Eat and drink",
    blockIfSleeping = true,
)
class EatAndDrinkTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    private companion object {
        private val ANTIFIRE_TASKS = setOf(
            SlayerTask.BLACK_DRAGONS,
            SlayerTask.STEEL_DRAGONS,
            SlayerTask.RUNE_DRAGONS,
            SlayerTask.MITHRIL_DRAGONS,
            SlayerTask.ADAMANT_DRAGONS,
            SlayerTask.BLACK_DRAGONS
        )
    }

    override fun execute(): Boolean {
        // Make sure that you are in the slayer standing area
        val currentTaskInfo = ctx.currentTaskInfo() ?: return false
        if (!currentTaskInfo.standingArea().containsPlayer()) {
            return false
        }

        val needPrayer = (Prayers.getPoints() < Random.nextInt(10, 20) && PotionType.PRAYER_TYPE.inventoryContains())
                || Prayers.getPoints() == 0
        val needFood = (Health.getCurrent() < Random.nextInt(20, 40) && Inventories.backpack()
            .contains { it.ids(FOOD).results() }) || Health.getPercent() < 10
        val needAntifire = remainingAntifireSeconds() < Random.nextInt(1, 2) && ANTIFIRE_TASKS.contains(ctx.getTask())
        val needAntipoison = Combat.isPoisoned()
        // Check boosting potions
        val needCombatPotion = Skills.getLevel(Skill.ATTACK) == Skills.getCurrentLevel(Skill.ATTACK) &&
                Potion.inventoryContains(Skill.ATTACK)

        // Panic means teleporting out because out of resources
        var panic = false

        // Drink anti-fire
        if (needAntifire) {
            val antifire = PotionType.ANTI_FIRE_TYPE.getFirstInInventory()
            if (antifire != null) {
                antifire.interact { true }
            } else {
                panic = true
            }
        }

        // Cure anti-poison
        if (needAntipoison) {
            if (PotionType.ANTI_POISON_TYPE.inventoryContains()) {
                Combat.curePoison()
            } else {
                panic = true
            }
        }

        // Drink prayer
        if (needPrayer) {
            val prayerPotion = PotionType.PRAYER_TYPE.getFirstInInventory()
            if (prayerPotion != null) {
                prayerPotion.interact { true }
            } else {
                panic = true
            }
        }

        // Eat
        if (needFood) {
            val food = Inventories.backpack().getItems(FOOD).firstOrNull()
            if (food != null) {
                food.interact { true }
            } else {
                panic = true
            }
        }

        // Get boosting potions
        if (needCombatPotion) {
            Potion.getFirstInInventory(Skill.ATTACK)?.interact { true }
        }

        // If we run out of essential items, just teleport away
        if (panic) {
            teleportHouse()
            sleep(3)
            return true
        }
        return needPrayer || needFood || needAntifire || needAntipoison || needCombatPotion
    }
}