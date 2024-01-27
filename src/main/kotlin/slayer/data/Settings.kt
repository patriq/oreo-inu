package slayer.data

import org.rspeer.game.Game
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.config.item.restock.RestockMeta
import slayer.data.Constants.SLAYER_HELM

object Settings {
    /**
     * Blocking list
     */
    val BLOCKING_TASKS = setOf(
        SlayerTask.ABYSSAL_DEMONS,
//        SlayerTask.HELLHOUNDS,
//        SlayerTask.GREATER_DEMONS,
//        SlayerTask.GARGOYLES,
//        SlayerTask.BLACK_DEMONS
        // Task.CAVE_KRAKEN // Skip since no slots
    )

    /**
     * Skipping tasks
     */
    val SKIPPING_TASKS = setOf(
        // Block
        SlayerTask.CAVE_KRAKEN,

        // Personal skip
        SlayerTask.SMOKE_DEVILS,
        SlayerTask.MITHRIL_DRAGONS,
        SlayerTask.ADAMANT_DRAGONS,
        SlayerTask.RUNE_DRAGONS,

        // Spreadsheet skip (https://docs.google.com/spreadsheets/d/14-rahdjUzFiWe5STNV87GBekIc45FtXwYmH58JPUsVc)
        SlayerTask.DRAKES,
        SlayerTask.WYRMS,
        SlayerTask.SKELETAL_WYVERNS,
        SlayerTask.SPIRITUAL_CREATURES,
        // Task.FIRE_GIANTS,
        // Task.ABERRANT_SPECTRES,
        SlayerTask.IRON_DRAGONS,
        SlayerTask.FOSSIL_ISLAND_WYVERNS,
        SlayerTask.CAVE_HORRORS,
        SlayerTask.ELVES,
        // Task.KURASK,
        SlayerTask.BLUE_DRAGONS, // Could do vork instead maybe?
        SlayerTask.WATERFIENDS
    )

    /**
     * Equipment
     */
    val BASE_GEAR = mapOf(
        Equipment.Slot.HEAD to SLAYER_HELM,
        Equipment.Slot.MAINHAND to "Ghrazi rapier",
        Equipment.Slot.OFFHAND to "Dragon defender",
        Equipment.Slot.CHEST to "Fighter torso",
        Equipment.Slot.NECK to "Amulet of torture",
        Equipment.Slot.QUIVER to "Rada's blessing 1",
        Equipment.Slot.RING to "Berserker ring (i)",
        Equipment.Slot.HANDS to "Barrows gloves",
        Equipment.Slot.LEGS to "Obsidian platelegs",
        Equipment.Slot.CAPE to "Fire cape",
        Equipment.Slot.FEET to "Dragon boots"
    )

    val DRAGON_GEAR = mapOf(
        Equipment.Slot.HEAD to SLAYER_HELM,
        Equipment.Slot.MAINHAND to "Ghrazi rapier",
        Equipment.Slot.OFFHAND to "Anti-dragon shield",
        Equipment.Slot.CHEST to "Fighter torso",
        Equipment.Slot.NECK to "Amulet of torture",
        Equipment.Slot.QUIVER to "Rada's blessing 1",
        Equipment.Slot.RING to "Berserker ring (i)",
        Equipment.Slot.HANDS to "Barrows gloves",
        Equipment.Slot.LEGS to "Obsidian platelegs",
        Equipment.Slot.CAPE to "Fire cape",
        Equipment.Slot.FEET to "Dragon boots"
    )

    val KURASK_GEAR =
        mapOf(
            Equipment.Slot.HEAD to SLAYER_HELM,
            Equipment.Slot.MAINHAND to "Leaf-bladed battleaxe",
            Equipment.Slot.OFFHAND to "Dragon defender",
            Equipment.Slot.CHEST to "Fighter torso",
            Equipment.Slot.NECK to "Amulet of torture",
            Equipment.Slot.QUIVER to "Rada's blessing 1",
            Equipment.Slot.RING to "Berserker ring (i)",
            Equipment.Slot.HANDS to "Barrows gloves",
            Equipment.Slot.LEGS to "Obsidian platelegs",
            Equipment.Slot.CAPE to "Fire cape",
            Equipment.Slot.FEET to "Dragon boots"
        )

    val WYRMS_GEAR = listOf(
        SLAYER_HELM,
        "Dragon hunter lance",
        "Dragon defender",
        "Bandos chestplate",
        "Honourable blessing",
        "Amulet of torture",
        "Berserker ring (i)",
        "Ferocious gloves",
        "Bandos tassets",
        "Fire cape",
        "Boots of stone"
    )

    /**
     * Food
     */
    const val MANTA_RAY = true
    const val SHARK = false

    val FOOD = if (MANTA_RAY) 391 else 385 // Manta ray or Shark
    val HEAL_FOOD_AMOUNT = if (MANTA_RAY) 22 else 20

    /**
     * Potions
     */
    val PRAYER_POTION = Potion.PRAYER_POTION
    val COMBAT_BOOSTING_POTION = Potion.SUPER_COMBAT_POTION
    val ANTI_POISON_POTION = Potion.ANTIDOTE_PLUS_PLUS
    val ANTIFIRE_POTION = Potion.EXTENDED_SUPER_ANTIFIRE


    /**
     * Restocking
     */
    val RESTOCK_STRATEGIES = mapOf<String, RestockMeta>(
        "Prayer potion(4)" to RestockMeta(2434, 200, -2),
        Game.getClient().getItemDefinition(FOOD).name to RestockMeta(FOOD, 1000, -2),
        "Super combat potion(4)" to RestockMeta(12695, 50, -2),
        "Antidote++(4)" to RestockMeta(5952, 20, -2),
        "Extended super antifire(4)" to RestockMeta(22209, 20, -2),
    )

    /**
     * Looting
     */
    const val LOOT_TAB = 0

    // From https://oldschool.runescape.wiki/w/Drop_table#Seed_tables
    val SEEDS = arrayOf(
        // Seeds
        "Snapdragon seed",
        "Torstol seed",
        "Ranarr seed",
        "Dragonfruit tree seed",
        "Magic seed",
        "Yew seed"
    )

    // From https://oldschool.runescape.wiki/w/Drop_table#Herb_tables
    val HERBS = arrayOf(
        // Herbs
        "Grimy ranarr weed",
        "Grimy snapdragon",
        "Grimy torstol",
        "Grimy avantoe",
        "Grimy cadantine",
        "Grimy lantadyme"
    )

    val UNIQUES = setOf(
        // Uniques
        "Imbued heart",
        "Eternal gem",
        "Smouldering stone",
        "Draconic visage"
    )

    val TASK_LOOT = mapOf(
        SlayerTask.KURASK to setOf(
            "Leaf-bladed battleaxe",
            "Mystic robe top (light)",
            "Leaf-bladed sword",
            "Rune longsword",
            "Coconut",
            "Papaya fruit",
            "Coins",
            "Big bones"
        ),
        SlayerTask.WYRMS to setOf(
            "Dragon harpoon",
            "Dragon knife",
            "Dragon thrownaxe",
            "Dragon sword"
        ),
        SlayerTask.GARGOYLES to setOf(
            "Mystic robe top (dark)",
            "Granite maul",
            "Mithril bar",
            "Steel bar",
            "Gold ore",
            "Gold bar"
        ),
        SlayerTask.DUST_DEVILS to setOf(
            "Adamantite bar",
            "Mithril bar"
        ),
        SlayerTask.DARK_BEASTS to setOf(
            "Dark bow",
            "Death talisman"
        )
    )

    val SHARED_LOOT = setOf(
        // Stackables
        "Crystal shard",
        "Ancient shard",
        "Rune dart(p)",
        "Nature rune",
        "Blood rune",
        "Death rune",
        "Law rune",
        "Chaos rune",
        "Soul rune",

        // Untradables
        "Dark totem top",
        "Dark totem middle",
        "Dark totem base",

        // Items
        "Dragon plateskirt",
        "Dragon platelegs",
        "Dragon med helm",
        "Shield left half",
        "Dragon spear",
        "Dragon dagger",
        "Dragon javelin heads",

        "Mist battlestaff",
        "Dust battlestaff",
        "Lava battlestaff",
        "Mystic air staff",
        "Mystic earth staff",

        "Rune platelegs",
        "Rune kiteshield",
        "Rune longsword",
        "Rune 2h sword",
        "Rune full helm",
        "Rune med helm",
        "Rune battleaxe",
        "Rune chainbody",
        "Runite bar",
        "Runite ore",

        "Tooth half of key",
        "Loop half of key"
    )
}