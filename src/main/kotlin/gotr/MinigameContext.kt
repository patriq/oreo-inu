package gotr

import api.containsPlayer
import org.rspeer.event.Subscribe
import org.rspeer.game.Game
import org.rspeer.game.Vars
import org.rspeer.game.adapter.component.InterfaceComponent
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.adapter.scene.SceneObject
import org.rspeer.game.combat.Combat
import org.rspeer.game.component.Dialog
import org.rspeer.game.component.Interfaces
import org.rspeer.game.event.ChatMessageEvent
import org.rspeer.game.event.TickEvent
import org.rspeer.game.position.area.Area
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects
import javax.inject.Singleton

@Singleton
class MinigameContext {
    var catalyticRewardPoints = 0
    var elementalRewardPoints = 0
    var gamesCompleted = 0
    var averagePointsPerGame = 0

    var state = MinigameState.DEPOSIT_RUNES
    var isMinigameRunning = false
    var activeAltars = emptyList<Altar>()
    var guardianPower = 0
    var portalUp = false
    var nextRunecraftingAltarSpawnSeconds = 120 // 2 minutes at the start
    var currentElementalRewardPoints = 0
    var currentCatalyticRewardPoints = 0
    private var nextPortalTick = -1

    val ticksTilPortalSpawn get() = nextPortalTick - Game.getTick()
    val potentialElementalRewadPoints get() = elementalRewardPoints + (currentElementalRewardPoints / 100)
    val potentialCatalyticRewardPoints get() = catalyticRewardPoints + (currentCatalyticRewardPoints / 100)

    fun minigameTick() {
        // We are loading, don't update anything
        if (isTransitioningRegions()) {
            return
        }

        // Keep track of minigame state changes
        val currentIsRunning = gotrRootWidget()?.isVisible == true
        if (currentIsRunning != isMinigameRunning) {
            onGameRunningChange(currentIsRunning)
            isMinigameRunning = currentIsRunning
        }

        // If the GOTR widget is not visible, we don't update any info
        if (!isMinigameRunning) {
            return
        }

        // Keep track of altars
        activeAltars = activeAltars()

        // Keep track of guardian power
        guardianPower = guardiansPower()

        // Keep track of next altar spawn
        nextRunecraftingAltarSpawnSeconds = nextRunecraftingAltarSpawnSeconds()

        // Keep track of portal state
        if (Interfaces.getDirect(746, 27)?.isVisible == true) {
            portalUp = true
        } else if (portalUp) {
            portalUp = false
            nextPortalTick = Game.getTick() + 184
        }

        // Keep track of current points
        currentElementalRewardPoints = currentElementalEnergyCount()
        currentCatalyticRewardPoints = currentCatalyticEnergyCount()
    }

    private fun onGameRunningChange(running: Boolean) {
        if (running) {
            nextPortalTick = Game.getTick() + 266
        } else {
            resetContext()
        }
    }

    @Subscribe(async = false)
    fun onTick(event: TickEvent) {
        // Try to parse point message from dialog
        Dialog.getText().let {
            val checkMatcher = CHECK_POINT_REGEX.find(it, 0) ?: return@let
            // For some reason these are reversed compared to everything else
            catalyticRewardPoints = checkMatcher.groups[1]?.value?.toInt() ?: 0
            elementalRewardPoints = checkMatcher.groups[2]?.value?.toInt() ?: 0
        }
    }

    @Subscribe(async = false)
    fun onChatMessage(event: ChatMessageEvent) {
        val message = event.contents

        if (message.contains("You found some loot:")) {
            elementalRewardPoints--
            catalyticRewardPoints--
        }

        POINTS_GAINED_REGEX.find(message, 0)?.let {
            val elementalGameRewardPoints = it.groups[1]?.value?.toInt() ?: 0
            val catalyticGameRewardPoints = it.groups[2]?.value?.toInt() ?: 0
            val totalPoints = elementalGameRewardPoints + catalyticGameRewardPoints
            averagePointsPerGame = (averagePointsPerGame * gamesCompleted + totalPoints) / (gamesCompleted + 1)
            gamesCompleted++
        }

        REWARD_POINT_REGEX.find(message, 0)?.let {
            elementalRewardPoints = it.groups[1]?.value?.replace(",", "")?.toInt() ?: 0
            catalyticRewardPoints = it.groups[2]?.value?.replace(",", "")?.toInt() ?: 0
        }
    }

    private fun resetContext() {
        state = MinigameState.DEPOSIT_RUNES
        isMinigameRunning = false
        activeAltars = emptyList()
        guardianPower = 0
        portalUp = false
        nextRunecraftingAltarSpawnSeconds = 120 // 2 minutes at the start
        currentCatalyticRewardPoints = 0
        currentElementalRewardPoints = 0
    }

    companion object {
        private val CHECK_POINT_REGEX = Regex("You have (\\d+) catalytic energy and (\\d+) elemental energy")
        private val POINTS_GAINED_REGEX = Regex("Elemental energy attuned: +(\\d+).*Catalytic energy attuned: +(\\d+)")
        private val REWARD_POINT_REGEX = Regex("Total elemental energy: +(\\d+).*Total catalytic energy: +(\\d+)")
        const val GUARDIAN_ESSENCE_NAME = "Guardian essence"
        private val GUARDIAN_STONE_NAMES = arrayOf(
            "Elemental guardian stone",
            "Catalytic guardian stone",
            "Polyelemental guardian stone",
        )
        val CHARGED_CELL_NAMES = CellType.chargedCellNames().toTypedArray()
        val MINIGAME_AREA = Area.rectangular(3574, 9541, 3661, 9483, 0)
        val MINIGAME_MAIN_AREA = Area.rectangular(3597, 9518, 3633, 9483, 0)
        val LARGE_REMAINS_MINING_AREA = Area.rectangular(3636, 9511, 3644, 9495, 0)
        val HUGE_REMAINS_MINING_AREA = Area.rectangular(3584, 9519, 3594, 9491, 0)

        private fun gotrRootWidget(): InterfaceComponent? = Interfaces.getDirect(746, 2)

        private fun isTransitioningRegions(): Boolean = Game.isLoadingRegion() || Interfaces.getDirect(745, 0) != null

        private fun activeAltars(): List<Altar> {
            val elementalRune = Interfaces.getDirect(746, 20) ?: return emptyList()
            val catalyticRune = Interfaces.getDirect(746, 23) ?: return emptyList()

            return listOf(
                Altar.fromMaterialId(elementalRune.materialId),
                Altar.fromMaterialId(catalyticRune.materialId)
            ).filterNotNull()
        }

        private fun nextRunecraftingAltarSpawnSeconds(): Int {
            val timer = Interfaces.getDirect(746, 5) ?: return 120
            val text = timer.text
            // Covert M:SS to seconds
            val index = text.indexOf(":")
            if (index == -1) {
                return 120
            }
            val minutes = text.substring(0, index).toInt()
            val seconds = text.substring(index + 1).toInt()
            return minutes * 60 + seconds
        }

        private fun guardiansPower(): Int {
            val widget = Interfaces.getDirect(746, 18) ?: return 0
            return widget.text.filter { it.isDigit() }.toInt()
        }

        fun isInsideMinigame(): Boolean = MINIGAME_AREA.containsPlayer() || insideAltar()

        fun isInsideLargeRemainsMiningArea() = LARGE_REMAINS_MINING_AREA.containsPlayer()

        fun isInsideHugeRemainsMiningArea() = HUGE_REMAINS_MINING_AREA.containsPlayer()

        fun isClimbingLadder(): Boolean = Players.self().animation?.id == 1148

        fun isCraftingGuardianEssence(): Boolean = Players.self().animation?.id == 9365

        fun isMining(): Boolean = Players.self().animation?.id == 7139

        fun unchargedCellCount(): Int =
            Backpack.backpack().query().names(CellType.UNCHARGED.cellName).results().firstOrNull()?.stackSize ?: 0

        fun containsGuardianEssence() =
            Backpack.backpack().query().names(GUARDIAN_ESSENCE_NAME).results().isNotEmpty()

        fun containsGuardianFragments() = Backpack.backpack().query().names("Guardian fragments").results().isNotEmpty()

        fun containsGuardianStones() =
            Backpack.backpack().query().names(*GUARDIAN_STONE_NAMES).results().isNotEmpty()

        fun containsChargedCells() =
            Backpack.backpack().query().names(*CHARGED_CELL_NAMES).results().isNotEmpty()

        @OptIn(ExperimentalStdlibApi::class)
        fun insideAltar() = Altar.entries.any { it.insideAltar() }

        fun currentCatalyticEnergyCount() = Vars.get(Vars.Type.VARBIT, 13685)

        fun currentElementalEnergyCount() = Vars.get(Vars.Type.VARBIT, 13686)

        fun pickaxeSpec() = Combat.getSpecialEnergy() == 100 && Combat.toggleSpecial(true)

        fun hugeFragmentsPortal(): SceneObject? =
            SceneObjects.query().names("Portal").within(MINIGAME_MAIN_AREA).results().nearest()
    }
}

enum class MinigameState {
    COLLECT_UNCHARGED_CELLS,
    MINE_INITIAL_FRAGMENTS,
    CRAFT_WITHOUT_FILLING_POUCHES,
    CRAFT_GUARDIAN_STONES,
    FEED_GUARDIAN_STONES_AND_PLACE_BARRIER_CELL,
    CRAFT_CHARGED_CELL,
    PLACE_BARRIER_CELL,
    MINE_HUGE_FRAGMENTS,
    DEPOSIT_RUNES,
    CRAFT_WITH_FILLING_POUCHES,
    WAIT,
}