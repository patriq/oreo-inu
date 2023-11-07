package gotr

import org.rspeer.game.adapter.scene.SceneObject
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects

enum class Alignment {
    ELEMENTAL,
    CATALYTIC,
}

enum class CellType(val cellName: String, val cellTileName: String) {
    UNCHARGED("Uncharged cell", "Inactive cell tile"),
    WEAK("Weak cell", "Weak cell tile"),
    MEDIUM("Medium cell", "Medium cell tile"),
    STRONG("Strong cell", "Strong cell tile"),
    OVERCHARGED("Overcharged cell", "Overpowered cell tile");

    companion object {
        fun fromCellTileName(cellTileName: String): CellType? {
            return values().find { it.cellTileName == cellTileName }
        }

        fun fromCellName(cellName: String): CellType? {
            return values().find { it.cellName == cellName }
        }

        @OptIn(ExperimentalStdlibApi::class)
        fun chargedCellNames(): List<String> = entries.filter { it != UNCHARGED }.map { it.cellName }
    }
}

enum class Altar(
    val runecraftingLevel: Int,
    val regionId: Int,
    val guardianName: String,
    val cellType: CellType,
    val alignment: Alignment,
    val materialId: Int
) {
    AIR(1, 11339, "Guardian of Air", CellType.WEAK, Alignment.ELEMENTAL, 4353),
    MIND(2, 11083, "Guardian of Mind", CellType.WEAK, Alignment.ELEMENTAL, 4354),
    WATER(5, 10827, "Guardian of Water", CellType.MEDIUM, Alignment.ELEMENTAL, 4355),
    EARTH(9, 10571, "Guardian of Earth", CellType.STRONG, Alignment.ELEMENTAL, 4356),
    FIRE(14, 10315, "Guardian of Fire", CellType.OVERCHARGED, Alignment.ELEMENTAL, 4357),
    BODY(20, 10059, "Guardian of Body", CellType.WEAK, Alignment.CATALYTIC, 4358),
    COSMIC(27, 8523, "Guardian of Cosmic", CellType.MEDIUM, Alignment.CATALYTIC, 4359),
    CHAOS(35, 9035, "Guardian of Chaos", CellType.MEDIUM, Alignment.CATALYTIC, 4360),
    NATURE(44, 9547, "Guardian of Nature", CellType.STRONG, Alignment.CATALYTIC, 4361),
    LAW(54, 9803, "Guardian of Law", CellType.STRONG, Alignment.CATALYTIC, 4362),
    DEATH(65, 8779, "Guardian of Death", CellType.OVERCHARGED, Alignment.CATALYTIC, 4363),
    BLOOD(77, 12875, "Guardian of Blood", CellType.OVERCHARGED, Alignment.CATALYTIC, 4364);

    fun insideAltar(): Boolean {
        return Players.self().position.regionId == regionId
    }

    fun guardian(): SceneObject? {
        return SceneObjects.query().names(guardianName).results().nearest()
    }

    companion object {
        fun fromMaterialId(materialId: Int): Altar? {
            return values().find { it.materialId == materialId }
        }
    }
}