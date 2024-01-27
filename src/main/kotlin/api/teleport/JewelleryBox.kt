package api.teleport

import org.rspeer.game.Keyboard
import org.rspeer.game.adapter.scene.SceneObject
import org.rspeer.game.component.InterfaceComposite
import org.rspeer.game.component.Interfaces


object JewelleryBox {
    enum class Location(
        val locationName: String,
        val key: Char,
        val widgetIds: List<Int>
    ) {
        DUEL_ARENA("Duel Arena", '1', listOf(590, 2, 5)),
        CASTLE_WARS("Castle Wars", '2', listOf(590, 2, 6)),
        CLAN_WARS("Clan Wars", '3', listOf(590, 2, 7)),

        BUTHORPE("Buthorpe", '4', listOf(590, 3, 5)),
        BARBARIAN_OUTPOST("Barbarian Outpost", '5', listOf(590, 3, 6)),
        CORPOREAL_BEAST("Corporeal Beast", '6', listOf(590, 3, 7)),
        TEARS_OF_GUTHIX("Tears of Guthix", '7', listOf(590, 3, 8)),
        WINTERTODT_CAMP("Wintertodt Camp", '8', listOf(590, 3, 9)),

        WARRIORS_GUILD("Warriors Guild", '9', listOf(590, 4, 5)),
        CHAMPIONS_GUILD("Champions Guild", 'a', listOf(590, 4, 6)),
        MONASTERY("Monastery", 'b', listOf(590, 4, 7)),
        RANGING_GUILD("Ranging Guild", 'c', listOf(590, 4, 8)),

        FISHING_GUILD("Fishing Guild", 'd', listOf(590, 5, 5)),
        MINING_GUILD("Mining Guild", 'e', listOf(590, 5, 6)),
        CRAFTING_GUILD("Crafting Guild", 'f', listOf(590, 5, 7)),
        COOKING_GUILD("Cooking Guild", 'g', listOf(590, 5, 8)),
        WOODCUTTING_GUILD("Woodcutting Guild", 'h', listOf(590, 5, 9)),
        FARMING_GUILD("Farming Guild", 'i', listOf(590, 5, 10)),

        MISCELLANIA("Miscellania", 'j', listOf(590, 6, 5)),
        GRAND_EXCHANGE("Grand Exchange", 'k', listOf(590, 6, 6)),
        FALADOR_PARK("Falador Park", 'l', listOf(590, 6, 7)),
        DONDAKANS_ROCK("Dondakans Rock", 'm', listOf(590, 6, 8)),

        EDGEVILLE("Edgeville", 'n', listOf(590, 7, 5)),
        KARAMJA("Karamja", 'o', listOf(590, 7, 6)),
        DRAYNOR_VILLAGE("Draynor Village", 'p', listOf(590, 7, 7)),
        AL_KHARID("Al Kharid", 'q', listOf(590, 7, 8));
    }

    fun isOpen(): Boolean = Interfaces.isSubActive(InterfaceComposite.JEWELLERY_BOX)

    private fun teleport(location: Location, useKeys: Boolean = false): Boolean {
        if (!isOpen()) {
            return false
        }

        val interacted: Boolean = if (useKeys) {
            Keyboard.sendKey(location.key)
            true
        } else {
            val widgetIds = location.widgetIds
            val widget = Interfaces.getDirect(widgetIds[0], widgetIds[1], widgetIds[2])
            widget.interact { true }
        }

        return interacted
    }

    fun teleport(box: SceneObject, location: Location, useKeys: Boolean = false): Boolean {
        if (isOpen()) {
            return teleport(location, useKeys)
        }

        val options = box.actions
        val locationName = location.locationName
        val canRightClick = options.any { option -> option.contains(locationName) }

        // If we can right click, just do that instead
        if (canRightClick) {
            return box.interact(locationName)
        }

        // Otherwise open the menu.
        return box.interact("Teleport Menu")
    }

    fun close(): Boolean {
        if (!isOpen()) {
            return true
        }
        Interfaces.closeSubs()
        return true
    }
}