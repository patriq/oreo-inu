package api

import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.Players

fun Area.containsPlayer(): Boolean {
    return this.contains(Players.self())
}