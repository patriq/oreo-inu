package slayer.data

object Constants {
    /**
     * Items
     */
    const val SLAYER_HELM = "Slayer helmet (i)"
    const val CONSTRUCTION_CAPE = "Construct. cape(t)"
    const val DRAMEN_STAFF = "Dramen staff"
    const val HERB_SACK = "Open herb sack"
    const val SEED_BOX = "Open seed box"

    val MUST_HAVE_INVENTORY = listOf(
        "Rune pouch",
//        "Holy wrench",
        DRAMEN_STAFF,
        CONSTRUCTION_CAPE,
        HERB_SACK,
        SEED_BOX
    )

    val FUNGICIDE_SPRAY = arrayOf(
        "Fungicide spray 1", "Fungicide spray 2", "Fungicide spray 3",
        "Fungicide spray 4", "Fungicide spray 5", "Fungicide spray 6",
        "Fungicide spray 7", "Fungicide spray 8", "Fungicide spray 9",
        "Fungicide spray 10"
    )

    val SLAYER_RING = listOf(
        "Slayer ring (8)", "Slayer ring (7)", "Slayer ring (6)",
        "Slayer ring (5)", "Slayer ring (4)", "Slayer ring (3)",
        "Slayer ring (2)"
    )

    const val ROCK_HAMMER = "Rock hammer"

    /**
     * POH
     */
    const val REJUVENATION_POOL = "Ornate pool of Rejuvenation"
    const val ORNATE_JEWELLERY_BOX = "Ornate Jewellery Box"
    const val FAIRY_RING_SPIRIT_TREE = "Spiritual Fairy Tree"
    const val NEXUS_PORTAL = "Portal Nexus"
    const val OCCULT_ALTAR = "Altar of the Occult"
    const val XERICS_TALISMAN = "Xeric's Talisman"
    const val MYTHICAL_CAPE = "Mounted Mythical Cape"

    /**
     * Chat messages
     */
    // Slayer related
    val CHAT_GEM_PROGRESS_MESSAGE = Regex("You're assigned to kill (.+); only (\\d+) more to go.")
    const val CHAT_CANCEL_MESSAGE = "Your task has been cancelled."
    const val CHAT_GEM_COMPLETE_MESSAGE = "You need something new to hunt."

    /**
     * Dialogue messages
     */
    val NPC_ASSIGN_MESSAGE = Regex("Your new task is to kill (\\d+) (.+)\\.")

    /**
     * Projectiles
     */
    const val MARBLE_GARGOYLE_AOE = 1453
    const val NIGHT_BEAST_AOE = 130
}