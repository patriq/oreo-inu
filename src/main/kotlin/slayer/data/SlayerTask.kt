package slayer.data

enum class SlayerTask(val taskName: String) {
    UNKNOWN("Unknown"),
    NONE("None"),
    INVALID("Invalid"),

    ABERRANT_SPECTRES("Aberrant spectres"),
    ABYSSAL_DEMONS("Abyssal demons"),
    ABYSSAL_SIRE("Abyssal Sire"),
    ADAMANT_DRAGONS("Adamant dragons"),
    ALCHEMICAL_HYDRA("Alchemical Hydra"),
    ANKOU("Ankou"),
    AVIANSIES("Aviansies"),
    BANSHEES("Banshees"),
    BARROWS_BROTHERS("Barrows Brothers"),
    BASILISKS("Basilisks"),
    BATS("Bats"),
    BEARS("Bears"),
    ENTS("Ents"),
    LAVA_DRAGONS("Lava Dragons"),
    BIRDS("Birds"),
    BLACK_DEMONS("Black demons"), // 30
    BLACK_DRAGONS("Black dragons"),
    BLOODVELD("Bloodveld"),
    BLUE_DRAGONS("Blue dragons"),
    BRINE_RATS("Brine rats"),
    BRONZE_DRAGONS("Bronze dragons"),
    CALLISTO("Callisto"),
    CATABLEPON("Catablepon"),
    CAVE_BUGS("Cave bugs"),
    CAVE_CRAWLERS("Cave crawlers"),
    CAVE_HORRORS("Cave horrors"),
    CAVE_KRAKEN("Cave kraken"),
    CAVE_SLIMES("Cave slimes"),
    CERBERUS("Cerberus"),
    CHAOS_ELEMENTAL("Chaos Elemental"),
    CHAOS_FANATIC("Chaos Fanatic"),
    COCKATRICE("Cockatrice"),
    COWS("Cows"),
    CRAWLING_HANDS("Crawling hands"),
    CRAZY_ARCHAEOLOGIST("Crazy Archaeologists"),
    CROCODILES("Crocodiles"),
    DAGANNOTH("Dagannoth"),
    DAGANNOTH_KINGS("Dagannoth Kings"),
    DARK_BEASTS("Dark beasts"),
    DARK_WARRIORS("Dark warriors"),
    DERANGED_ARCHAEOLOGIST("Deranged Archaeologist"),
    DOGS("Dogs"),
    DRAKES("Drakes"),
    DUST_DEVILS("Dust devils"),
    DWARVES("Dwarves"),
    EARTH_WARRIORS("Earth warriors"),
    ELVES("Elves"), // 56
    FEVER_SPIDERS("Fever spiders"),
    FIRE_GIANTS("Fire giants"), // 16
    REVENANTS("Revenants"),
    FLESH_CRAWLERS("Fleshcrawlers"),
    FOSSIL_ISLAND_WYVERNS("Fossil island wyverns"),
    GARGOYLES("Gargoyles"),
    GENERAL_GRAARDOR("General Graardor"),
    GHOSTS("Ghosts"),
    GIANT_MOLE("Giant Mole"),
    GHOULS("Ghouls"),
    GOBLINS("Goblins"),
    GREATER_DEMONS("Greater demons"),
    GREEN_DRAGONS("Green dragons"),
    GROTESQUE_GUARDIANS("Grotesque Guardians"),
    HARPIE_BUG_SWARMS("Harpie bug swarms"),
    HELLHOUNDS("Hellhounds"),
    HILL_GIANTS("Hill giants"),
    HOBGOBLINS("Hobgoblins"),
    HYDRAS("Hydras"),
    ICE_GIANTS("Ice giants"),
    ICE_WARRIORS("Ice warriors"),
    ICEFIENDS("Icefiends"),
    INFERNAL_MAGES("Infernal mages"),
    IRON_DRAGONS("Iron dragons"),
    TzTok_Jad("TzTok-Jad"),
    JELLIES("Jellies"),
    JUNGLE_HORROR("Jungle horrors"),
    KALPHITE("Kalphite"),
    MAMMOTHS("Mammoths"),
    KALPHITE_QUEEN("Kalphite Queen"),
    KILLERWATTS("Killerwatts"),
    KING_BLACK_DRAGON("King Black Dragon"),
    KRAKEN("Kraken"),
    KREEARRA("Kree'arra"),
    KRIL_TSUTSAROTH("K'ril Tsutsaroth"),
    KURASK("Kurask"),
    ROGUES("Rogues"),
    LESSER_DEMONS("Lesser demons"),
    LIZARDS("Lizards"),
    LIZARDMEN("Lizardmen"),
    MINIONS_OF_SCABARAS("Minions of scabaras"),
    MINOTAURS("Minotaurs"),
    MITHRIL_DRAGONS("Mithril dragons"),
    MOGRES("Mogres"),
    MOLANISKS("Molanisks"),
    MONKEYS("Monkeys"),
    MOSS_GIANTS("Moss giants"),
    MUTATED_ZYGOMITES("Mutated zygomites"),
    NECHRYAEL("Nechryael"),
    OGRES("Ogres"),
    OTHERWORLDLY_BEING("Otherworldly beings"),
    PYREFIENDS("Pyrefiends"),
    RATS("Rats"),
    RED_DRAGONS("Red dragons"),
    ROCKSLUGS("Rockslugs"),
    RUNE_DRAGONS("Rune dragons"),
    SCORPIA("Scorpia"),
    CHAOS_DRUIDS("Chaos druids"),
    BANDITS("Bandits"),
    MAGIC_AXES("Magic axes"),
    SARACHNIS("Sarachnis"),
    SCORPIONS("Scorpions"),
    SEA_SNAKES("Sea snakes"),
    SHADES("Shades"),
    SHADOW_WARRIORS("Shadow warriors"),
    SKELETAL_WYVERNS("Skeletal wyverns"),
    SKELETONS("Skeletons"),
    SMOKE_DEVILS("Smoke devils"),
    SPIDERS("Spiders"),
    SPIRITUAL_CREATURES("Spiritual creatures"),
    STEEL_DRAGONS("Steel dragons"),
    SULPHUR_LIZARDS("Sulphur Lizards"),
    SUQAHS("Suqahs"),
    TEMPLE_SPIDERS("Temple Spiders"),
    TERROR_DOGS("Terror dogs"),
    THERMONUCLEAR_SMOKE_DEVIL("Thermonuclear Smoke Devil"),
    TROLLS("Trolls"), // 18
    TUROTH("Turoth"),
    TZHAAR("Tzhaar"),
    UNDEAD_DRUIDS("Undead Druids"),
    VAMPYRES("Vampyres"),
    VENENATIS("Venenatis"),
    VETION("Vet'ion"),
    VORKATH("Vorkath"),
    WALL_BEASTS("Wall beasts"),
    WATERFIENDS("Waterfiends"),
    WEREWOLVES("Werewolves"),
    WOLVES("Wolves"),
    WYRMS("Wyrms"),
    ZILYANA("Commander Zilyana"),
    ZOMBIES("Zombies"),
    ZULRAH("Zulrah"),
    ZUK("TzKal-Zuk");

    companion object {
        fun fromTaskName(taskName: String): SlayerTask {
            return values().find { taskName.contentEquals(it.taskName, true) } ?: INVALID
        }
    }
}