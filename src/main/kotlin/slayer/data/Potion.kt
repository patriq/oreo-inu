package slayer.data

import api.Text
import api.countDoses
import org.rspeer.game.component.Inventories
import org.rspeer.game.component.Item
import org.rspeer.game.component.tdi.Skill

enum class PotionType {
    ANTI_POISON_TYPE,
    ANTI_VENOM_TYPE,
    PRAYER_TYPE,
    ANTI_FIRE_TYPE;

    fun inventoryContains(): Boolean {
        return Inventories.backpack().getItems(getTypeFilter()).isNotEmpty()
    }

    fun getFirstInInventory(): Item? {
        return Inventories.backpack().getItems(getTypeFilter()).firstOrNull()
    }

    private fun getTypeFilter(): (Item) -> Boolean {
        return lambda@{ item ->
            if (item.isNoted) {
                false
            } else {
                for (potion in Potion.values()) {
                    val types = potion.types
                    if (types.contains(this)) {
                        val names = potion.allDoseNames
                        if (names.contains(item.name)) {
                            return@lambda true
                        }
                    }
                }
                false
            }
        }
    }
}

enum class Potion(
    val potionNamePrefix: String,
    val types: Array<PotionType> = emptyArray(),
    val allDoseNames: Array<String> = Text.generateDoseNames(potionNamePrefix, 4)
) {
    ANTIPOISON("Antipoison", arrayOf(PotionType.ANTI_POISON_TYPE)),
    SUPER_ANTIPOISON("Superantipoison", arrayOf(PotionType.ANTI_POISON_TYPE)),
    ANTIDOTE_PLUS("Antidote+", arrayOf(PotionType.ANTI_POISON_TYPE)),
    ANTIDOTE_PLUS_PLUS("Antidote++", arrayOf(PotionType.ANTI_POISON_TYPE)),
    SANFEW_SERUM("Sanfew serum", arrayOf(PotionType.ANTI_POISON_TYPE, PotionType.PRAYER_TYPE)),

    ANTI_VENOM("Anti-venom", arrayOf(PotionType.ANTI_VENOM_TYPE)),
    ANTI_VENOM_PLUS("Anti-venom+", arrayOf(PotionType.ANTI_VENOM_TYPE)),

    PRAYER_POTION("Prayer potion", arrayOf(PotionType.PRAYER_TYPE)),
    SUPER_RESTORE("Super restore", arrayOf(PotionType.PRAYER_TYPE)),

    EXTENDED_SUPER_ANTIFIRE("Extended super antifire", arrayOf(PotionType.ANTI_FIRE_TYPE)),
    EXTENDED_ANTIFIRE("Extended antifire", arrayOf(PotionType.ANTI_FIRE_TYPE)),

    MAGIC_POTION("Magic potion"),
    RANGING_POTION("Ranging potion"),
    BATTLEMAGE_POTION("Battlemage potion"),
    BASTION_POTION("Bastion potion"),
    DEFENCE_POTION("Defence potion"),
    SUPER_DEFENCE_POTION("Super defence"),
    SUPER_COMBAT_POTION("Super combat potion"),
    DIVINE_SUPER_COMBAT_POTION("Divine super combat potion"),
    DIVINE_RANGING_POTION("Divine ranging potion"),
    DIVINE_MAGIC_POTION("Divine magic potion");

    companion object {
        private val ALL_COMBAT_POTIONS = listOf(
            Skill.MAGIC to MAGIC_POTION,
            Skill.MAGIC to BATTLEMAGE_POTION,
            Skill.MAGIC to DIVINE_MAGIC_POTION,

            Skill.RANGED to RANGING_POTION,
            Skill.RANGED to BASTION_POTION,
            Skill.RANGED to DIVINE_RANGING_POTION,

            Skill.DEFENCE to DEFENCE_POTION,
            Skill.DEFENCE to SUPER_DEFENCE_POTION,

            Skill.ATTACK to SUPER_COMBAT_POTION,
            Skill.STRENGTH to SUPER_COMBAT_POTION,
            Skill.DEFENCE to SUPER_COMBAT_POTION,

            Skill.ATTACK to DIVINE_SUPER_COMBAT_POTION,
            Skill.STRENGTH to DIVINE_SUPER_COMBAT_POTION,
            Skill.DEFENCE to DIVINE_SUPER_COMBAT_POTION
        )

        fun inventoryContains(skill: Skill): Boolean {
            for (pair in ALL_COMBAT_POTIONS) {
                if (pair.first == skill && pair.second.countInventory() > 0) {
                    return true
                }
            }
            return false
        }

        fun getFirstInInventory(skill: Skill): Item? {
            for (pair in ALL_COMBAT_POTIONS) {
                if (pair.first == skill) {
                    val item = pair.second.getFirstInInventory()
                    if (item != null) {
                        return item
                    }
                }
            }
            return null
        }
    }

    fun countInventoryDoses(): Int = Inventories.backpack().countDoses(potionNamePrefix)

    fun countInventory(): Int = Inventories.backpack().getCount { it.names(*allDoseNames).results() }

    fun getFirstInInventory(): Item? =
        Inventories.backpack().getItems { !it.isNoted && allDoseNames.contains(it.name) }.firstOrNull()
}
