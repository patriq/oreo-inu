package api

import org.rspeer.game.Vars

object MoreVars {
    fun getServerTick(): Int {
        return Vars.get(3079)
    }
}