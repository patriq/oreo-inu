package slayer

import api.teleport.*
import org.rspeer.game.House
import org.rspeer.game.Vars
import org.rspeer.game.combat.Combat
import org.rspeer.game.component.Inventories
import org.rspeer.game.component.tdi.Prayers
import org.rspeer.game.effect.Health
import org.rspeer.game.scene.SceneObjects
import slayer.data.Constants.CONSTRUCTION_CAPE
import slayer.data.Constants.DRAMEN_STAFF
import slayer.data.Constants.FAIRY_RING_SPIRIT_TREE
import slayer.data.Constants.MYTHICAL_CAPE
import slayer.data.Constants.NEXUS_PORTAL
import slayer.data.Constants.ORNATE_JEWELLERY_BOX
import slayer.data.Constants.REJUVENATION_POOL

fun remainingAntifireSeconds(): Int = Vars.get(Vars.Type.VARBIT, 6101) * 10

fun remainingAmount(): Int = Vars.get(394)

fun teleportHouse(): Boolean {
    if (House.isInside()) {
        return true
    }
    Inventories.backpack().getItems(CONSTRUCTION_CAPE).firstOrNull()?.interact("Tele to POH")
    return true
}

fun shouldRestoreStats(): Boolean {
    return Combat.getSpecialEnergy() != 100
            || Prayers.getPoints() != Prayers.getTotalPoints()
            || Health.getCurrent() != Health.getLevel()
}

fun restoreStats(): Boolean {
    return SceneObjects.query().names(REJUVENATION_POOL).results().nearest()?.interact("Drink") == true
}

fun teleportJewelleryBox(location: JewelleryBox.Location): Boolean {
    val box = SceneObjects.query().names(ORNATE_JEWELLERY_BOX).results().nearest() ?: return false
    return JewelleryBox.teleport(box, location)
}

fun travelFairyRing(code: String): Boolean {
    // Make sure to equip dramen staff
    if (!Inventories.equipment().contains { it.names(DRAMEN_STAFF).results() }) {
        Inventories.backpack().getItems(DRAMEN_STAFF).firstOrNull()?.interact("Wield")
        return true
    }

    val ring = SceneObjects.query().names(FAIRY_RING_SPIRIT_TREE).results().nearest() ?: return false
    return FairyRing.travel(ring, code)
}

fun travelFairyZanaris(): Boolean {
    // Make sure to equip dramen staff
    if (!Inventories.equipment().contains { it.names(DRAMEN_STAFF).results() }) {
        Inventories.backpack().getItems(DRAMEN_STAFF).firstOrNull()?.interact("Wield")
        return true
    }

    val ring = SceneObjects.query().names(FAIRY_RING_SPIRIT_TREE).results().nearest() ?: return false
    return FairyRing.travelZanaris(ring)
}

fun travelNexusPortal(location: NexusPortal.Location): Boolean {
    val portal = SceneObjects.query().names(NEXUS_PORTAL).results().nearest() ?: return false
    return NexusPortal.teleport(portal, location)
}

fun teleportMythicalCape() {
    SceneObjects.query().names(MYTHICAL_CAPE).results().nearest()?.interact("Teleport")
}

fun teleportConstructionCape(location: ConstructionCape.Location): Boolean {
    val cape = Inventories.backpack().getItems(CONSTRUCTION_CAPE).firstOrNull() ?: return false
    return ConstructionCape.teleport(cape, location, true)
}

fun teleportDigsitePendant(location: DigsitePendant.Location): Boolean {
    val pendant = SceneObjects.query().names("Digsite Pendant").results().nearest() ?: return false
    return DigsitePendant.teleport(pendant, location)
}