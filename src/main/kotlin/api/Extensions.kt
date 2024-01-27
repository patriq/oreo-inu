package api

import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.Players

fun Area.containsPlayer(): Boolean {
    return this.contains(Players.self())
}

fun Backpack.countDoses(name: String): Int {
    return this.getItems(name).results.sumOf { it.name.filter { char -> char.isDigit() }.toInt() }
}