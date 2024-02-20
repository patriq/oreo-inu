package slayer.data

import org.rspeer.game.Game
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.combat.Combat.WeaponType
import org.rspeer.game.component.tdi.Prayer
import org.rspeer.game.component.tdi.Skill
import org.rspeer.game.config.item.restock.RestockMeta
import slayer.data.Constants.SLAYER_HELM

object Settings {
    /**
     * Blocking list
     */
    val BLOCKING_TASKS = setOf(
        SlayerTask.HELLHOUNDS,
        SlayerTask.GREATER_DEMONS,
//        SlayerTask.BLACK_DEMONS,
//        SlayerTask.DRAKES,
//        SlayerTask.GARGOYLES,
        SlayerTask.WYRMS,
    )

    /**
     * Skipping tasks
     */
    val SKIPPING_TASKS = setOf(
        // Require bursting
        SlayerTask.SMOKE_DEVILS,

        // Block (cant block more :c)
        SlayerTask.CAVE_KRAKEN,
        SlayerTask.DRAKES,
//        SlayerTask.GARGOYLES,
//        SlayerTask.BLACK_DEMONS,

        // Spreadsheet skip (https://docs.google.com/spreadsheets/d/171KtsAiFqC1oKSBsGjUXviu4WBSlMG7gsYkls6PE7tw/edit#gid=943565354)
        SlayerTask.ABERRANT_SPECTRES,
        SlayerTask.FIRE_GIANTS,
        SlayerTask.SKELETAL_WYVERNS,
        SlayerTask.SPIRITUAL_CREATURES,
//        SlayerTask.STEEL_DRAGONS,
//        SlayerTask.TROLLS,
//        SlayerTask.ANKOU,
        SlayerTask.IRON_DRAGONS,
        SlayerTask.BLUE_DRAGONS,
        SlayerTask.CAVE_HORRORS,
        SlayerTask.ELVES,
//        SlayerTask.KURASK,
        SlayerTask.WATERFIENDS,
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

    /**
     * Offensive prayers
     */
    val ATTACKING_ANIMATIONS = listOf(8145, 7004)
    val WEAPON_SPEEDS = mapOf(
        "Leaf-bladed battleaxe" to 5,
    )
    val OFFENSIVE_PRAYERS = mapOf(
        WeaponType.TYPE_1 to Prayer.Modern.PIETY,
        WeaponType.TYPE_17 to Prayer.Modern.PIETY,
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
        "Divine super combat potion(4)" to RestockMeta(23685, 50, -2),
        "Antidote++(4)" to RestockMeta(5952, 20, -2),
        "Extended super antifire(4)" to RestockMeta(22209, 20, -2),
        Bracelet.SLAUGHTER.itemName to RestockMeta(21183, 100, -2),
        Bracelet.EXPEDITIOUS.itemName to RestockMeta(21177, 100, -2),
    )

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
        "Draconic visage",
        "Dragon limbs",
        "Dragon metal slice",
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
        ),
        SlayerTask.ADAMANT_DRAGONS to setOf(
            "Adamantite bar",
            "Adamantite ore",
        ),
        SlayerTask.RUNE_DRAGONS to setOf(
            "Dragonstone", // Noted
        ),
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
        "Wrath rune",
        "Dragon bolts (unf)",
        "Adamant bolts(unf)",
        "Runite bolts (unf)",
        "Rune javelin heads",

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
        "Rune mace",
        "Rune scimitar",
        "Rune warhammer",
        "Rune full helm",
        "Rune med helm",
        "Rune battleaxe",
        "Rune chainbody",
        "Rune platebody",
        "Rune platelegs",
        "Runite bar",
        "Runite ore",

        "Tooth half of key",
        "Loop half of key"
    )
}