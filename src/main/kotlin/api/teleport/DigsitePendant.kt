package api.teleport

import org.rspeer.game.adapter.scene.SceneObject
import org.rspeer.game.component.InterfaceComposite
import org.rspeer.game.component.Interfaces

object DigsitePendant {
    enum class Location(val locationName:String, ) {
        DIGSITE("Digsite"),
        FOSSIL_ISLAND("Fossil Island"),
        LITHKREN("Lithkren"),
        NOWHERE("Nowhere")
    }

    fun isOpen(): Boolean {
        return Interfaces.isSubActive(InterfaceComposite.SPIRIT_TREE)
    }

    private fun teleport(location: Location): Boolean {
        if (!isOpen()) {
            return false
        }

        val widget = Interfaces.getDirect(InterfaceComposite.SPIRIT_TREE.group, 3, location.ordinal)
        return widget.interact { true }
    }

    fun teleport(sceneObject: SceneObject, location: Location): Boolean {
        if (isOpen()) {
            return teleport(location)
        }

        val options = sceneObject.actions
        val locationName = location.locationName
        val canRightClick = options.any { option -> option.contains(locationName) }

        // If we can right click, just do that instead
        if (canRightClick) {
            return sceneObject.interact(locationName)
        }

        // Otherwise open the menu.
        return sceneObject.interact("Teleport Menu")
    }

    fun close(): Boolean {
        if (!isOpen()) {
            return true
        }

        Interfaces.closeSubs()
        return true
    }
}