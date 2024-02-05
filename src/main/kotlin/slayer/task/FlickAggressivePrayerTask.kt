package slayer.task

import org.rspeer.commons.logging.Log
import org.rspeer.game.Game
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.combat.Combat
import org.rspeer.game.component.Inventories
import org.rspeer.game.component.tdi.Prayers
import org.rspeer.game.scene.Players
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.data.Settings.ATTACKING_ANIMATIONS
import slayer.data.Settings.OFFENSIVE_PRAYERS
import slayer.data.Settings.WEAPON_SPEEDS

@TaskDescriptor(
    name = "Flick aggressive prayer",
)
class FlickAggressivePrayerTask : Task() {
    private var lastAttackTick: Int = 0
    private var lastAnimationId: Int = 0

    override fun execute(): Boolean {
        // Keep track of animation changes
        val currentAnimationId = Players.self().animationId
        if (currentAnimationId != lastAnimationId) {
            if (currentAnimationId in ATTACKING_ANIMATIONS) {
                lastAttackTick = Game.getTick()
            }
            lastAnimationId = currentAnimationId
        }

        // Turn on prayer if we are attacking
        if (Players.self().target == null) {
            return false
        }

        if (!OFFENSIVE_PRAYERS.containsKey(Combat.getWeaponType())) {
            Log.severe("No offensive prayer for weapon type: ${Combat.getWeaponType()}")
        }
        val weapon = Inventories.equipment().getItemAt(Equipment.Slot.MAINHAND)?.name ?: return false
        val weaponSpeed = WEAPON_SPEEDS[weapon] ?: Combat.getWeaponSpeed()
        val offensivePrayer = OFFENSIVE_PRAYERS[Combat.getWeaponType()] ?: return false
        val shouldTurnOn = (Game.getTick() - lastAttackTick + 1) >= weaponSpeed
        Prayers.toggle(shouldTurnOn, offensivePrayer)
        return true
    }
}