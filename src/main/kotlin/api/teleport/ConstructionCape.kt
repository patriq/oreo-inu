package api.teleport

import org.rspeer.game.Keyboard
import org.rspeer.game.component.Interfaces
import org.rspeer.game.component.Item

object ConstructionCape {
    enum class Location(val key: Char) {
        HOME('1'),
        RIMMINGTON('2'),
        TAVERLEY('3'),
        POLLNIVNEACH('4'),
        HOSIDIUS('5'),
        RELLEKKA('6'),
        BRIMHAVEN('7'),
        YANNILE('8'),
        PRIFFDINAS('9')
    }

    fun isOpen(): Boolean {
        return Interfaces.isSubActive(187)
    }

    fun teleport(location: Location, useKeys: Boolean): Boolean {
        if (!isOpen()) {
            return false
        }

        return if (useKeys) {
            Keyboard.sendKey(location.key)
            true
        } else {
            val widget = Interfaces.getDirect(187, 3, location.ordinal)
            widget.interact { true }
        }
    }

    fun teleport(cape: Item, location: Location, useKeys: Boolean = false): Boolean {
        if (isOpen()) {
            return teleport(location, useKeys)
        }
        return cape.interact("Teleport")
    }

    fun close(): Boolean {
        if (!isOpen()) {
            return true
        }
        Interfaces.closeSubs()
        return true
    }
}