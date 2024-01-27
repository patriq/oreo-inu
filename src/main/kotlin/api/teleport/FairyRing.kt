package api.teleport

import org.rspeer.game.adapter.scene.SceneObject
import org.rspeer.game.component.InterfaceComposite
import org.rspeer.game.component.Interfaces
import org.rspeer.game.web.path.FairyRingEdge

object FairyRing {
    fun isOpen() = Interfaces.isSubActive(InterfaceComposite.FAIRY_RING)

    private fun travel(code: String): Boolean {
        val edge = FairyRingEdge()
        // Use reflection to write to accessCode String private field
        val accessCodeField = edge.javaClass.getDeclaredField("accessCode")
        accessCodeField.isAccessible = true
        accessCodeField.set(edge, code)
        edge.traverse()
        return true
    }

    fun travel(ring: SceneObject, code: String): Boolean {
        if (isOpen()) {
            return travel(code)
        }

        val options = ring.actions
        val canRightClick = options.any { option -> option.contains(code) }
        if (canRightClick) {
            ring.interact { it.contains("last-destination", true) }
            return true
        }

        return ring.interact { it.contains("configure", true) }
    }

    fun travelZanaris(ring: SceneObject): Boolean {
        return ring.interact { it.contains("zanaris", true) }
    }
}