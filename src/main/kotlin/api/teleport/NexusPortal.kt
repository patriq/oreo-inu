package api.teleport

import org.rspeer.game.Keyboard
import org.rspeer.game.adapter.scene.SceneObject
import org.rspeer.game.component.Interfaces
import java.util.*

object NexusPortal {
    private val locationNameRegex = Regex("<col=.*>(.*)</col> :  (.*)")

    enum class Location(val locationName: String) {
        LUMBRIDGE_GRAVEYARD("Lumbridge Graveyard"),
        DRAYNOR_MANOR("Draynor Manor"),
        BATTLEFRONT("Battlefront"),
        VARROCK("Varrock"),
        MIND_ALTAR("Mind Altar"),
        LUMBRIDGE("Lumbridge"),
        FALADOR("Falador"),
        SALVE_GRAVEYARD("Salve Graveyard"),
        CAMELOT("Camelot"),
        FENKENSTRAINS_CASTLE("Fenkenstrain's Castle"),
        EAST_ARDOUGNE("East Ardougne"),
        WATCHTOWER("Watchtower"),
        SENNTISTEN("Senntisten"),
        WEST_ARDOUGNE("West Ardougne"),
        MARIM("Marim"),
        HARMONY_ISLAND("Harmony Island"),
        KHARYLL("Kharyll"),
        LUNAR_ISLE("Lunar Isle"),
        KOUREND_CASTLE("Kourend Castle"),
        THE_FORGOTTEN_CEMETERY("The Forgotten Cemetery"),
        WATERBIRTH_ISLAND("Waterbirth Island"),
        BARROWS("Barrows"),
        CARRALLANGAR("Carrallangar"),
        FISHING_GUILD("Fishing Guild"),
        CATHERBY("Catherby"),
        APE_ATOLL_DUNGEON("Ape Atoll Dungeon"),
        GHORROCK("Ghorrock"),
        TROLL_STRONGHOLD("Troll Stronghold"),
        WEISS("Weiss");

        companion object {
            fun fromLocationName(locationName: String): Location? {
                return values().find { it.locationName == locationName }
            }
        }
    }

    private val rightClickOptions = mapOf(
        Location.KOUREND_CASTLE to "Great Kourend"
    )

    fun isOpen(): Boolean {
        return Interfaces.isSubActive(17)
    }

    fun getTeleportLocations(): Pair<List<Location>, List<Char>> {
        if (!isOpen()) {
            return Pair(emptyList(), emptyList())
        }
        val locations = mutableListOf<Location>()
        val keys = mutableListOf<Char>()
        val locationsWidget = Interfaces.getDirect(17, 12)
        for (child in locationsWidget?.querySubComponents()!!.results()) {
            val matchResult = locationNameRegex.find(child.text)
            if (matchResult != null) {
                val key = matchResult.groupValues[1].lowercase(Locale.getDefault()).toCharArray().first()
                val locationName = matchResult.groupValues[2]

                val location = Location.fromLocationName(locationName)
                if (location != null) {
                    locations.add(location)
                    keys.add(key)
                }
            }
        }
        return Pair(locations, keys)
    }

    fun hasLocation(location: Location): Boolean {
        return getTeleportLocations().first.contains(location)
    }

    private fun teleport(location: Location, useKeys: Boolean = false): Boolean {
        if (!isOpen()) {
            return false
        }

        val (locations, keys) = getTeleportLocations()
        val locationIndex = locations.indexOf(location)
        if (locationIndex == -1) {
            return false
        }

        val interacted: Boolean = if (useKeys) {
            val key = keys[locationIndex]
            Keyboard.sendKey(key)
            true
        } else {
            val widget = Interfaces.getDirect(17, 13, locationIndex)
            widget.interact("Continue")
        }
        return interacted
    }

    fun teleport(portal: SceneObject, location: Location, useKeys: Boolean = false): Boolean {
        if (isOpen()) return teleport(location, useKeys)

        val options = portal.actions
        val rightClickOption = rightClickOptions[location] ?: location.locationName
        val canRightClick = options.any { option -> option.contains(rightClickOption) }

        // If we can right click, just do that instead
        if (canRightClick) {
            return portal.interact(rightClickOption)
        }

        // Otherwise open the menu.
        portal.interact("Teleport Menu")
        return true
    }

    fun close(): Boolean {
        if (!isOpen()) {
            return true
        }

        Interfaces.closeSubs()
        return true
    }
}