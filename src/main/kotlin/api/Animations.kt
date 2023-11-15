package api

import org.rspeer.game.scene.Players

object Animations {
    fun isNpcContacting(): Boolean = Players.self().animationId == 4413
}