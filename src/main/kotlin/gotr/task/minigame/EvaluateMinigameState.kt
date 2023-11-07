package gotr.task.minigame

import api.pouch.Pouch
import gotr.MinigameContext.Companion.containsChargedCells
import gotr.MinigameContext.Companion.containsGuardianEssence
import gotr.MinigameContext.Companion.containsGuardianFragments
import gotr.MinigameContext.Companion.containsGuardianStones
import gotr.MinigameContext.Companion.unchargedCellCount
import gotr.MinigameState
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.script.TaskDescriptor

@TaskDescriptor(name = "Evaluate", priority = 100)
class EvaluateMinigameState : MinigameTask() {
    override fun minigameExecute(): Boolean {
        // First update context, then calculate next state
        context.minigameTick()

        // Run state machine until we are at the same state two calls in a row
        var previousState: MinigameState? = null
        while (context.state != previousState) {
            previousState = context.state
            evaluateNextState()
        }
        return false
    }

    private fun evaluateNextState() {
        when (context.state) {
            MinigameState.COLLECT_UNCHARGED_CELLS -> {
                if (unchargedCellCount() == 10) {
                    context.state = MinigameState.MINE_INITIAL_FRAGMENTS
                }
            }
            MinigameState.MINE_INITIAL_FRAGMENTS -> {
                // Large remains at 25 seconds (requires 56 agility)
                if (context.nextRunecraftingAltarSpawnSeconds <= 25) {
                    context.state = MinigameState.CRAFT_WITHOUT_FILLING_POUCHES
                }
            }
            MinigameState.CRAFT_WITHOUT_FILLING_POUCHES -> {
                if (Backpack.backpack().isFull && containsGuardianEssence()) {
                    context.state = MinigameState.CRAFT_GUARDIAN_STONES
                }
            }
            MinigameState.CRAFT_GUARDIAN_STONES -> {
                if (!containsGuardianEssence() && Pouch.allPouchesEmpty()) {
                    context.state = MinigameState.FEED_GUARDIAN_STONES_AND_PLACE_BARRIER_CELL
                }
            }
            MinigameState.FEED_GUARDIAN_STONES_AND_PLACE_BARRIER_CELL -> {
                if (!containsChargedCells() && !containsGuardianStones()) {
                    // If portal is not spawning soon we can craft essence if we have fragments
                    if (context.ticksTilPortalSpawn > 80 && containsGuardianFragments()) {
                        context.state = MinigameState.DEPOSIT_RUNES
                    } else {
                        context.state = if (context.portalUp || unchargedCellCount() == 0) {
                            if (context.guardianPower >= 90) {
                                if (containsGuardianFragments()) {
                                    MinigameState.DEPOSIT_RUNES
                                } else {
                                    MinigameState.WAIT
                                }
                            } else {
                                MinigameState.MINE_HUGE_FRAGMENTS
                            }
                        } else {
                            MinigameState.CRAFT_CHARGED_CELL
                        }
                    }
                }
            }

            MinigameState.CRAFT_CHARGED_CELL -> {
                if (containsChargedCells()) {
                    context.state = MinigameState.PLACE_BARRIER_CELL
                }
            }

            MinigameState.PLACE_BARRIER_CELL -> {
                if (!containsChargedCells()) {
                    if (context.guardianPower >= 87) {
                        if (containsGuardianFragments()) {
                            context.state = MinigameState.DEPOSIT_RUNES
                        } else {
                            context.state = MinigameState.WAIT
                        }
                    } else {
                        context.state = MinigameState.MINE_HUGE_FRAGMENTS
                    }
                }
            }

            MinigameState.MINE_HUGE_FRAGMENTS -> {
                if (Backpack.backpack().isFull && Pouch.allPouchesFilled()) {
                    context.state = MinigameState.CRAFT_GUARDIAN_STONES
                }
            }
            MinigameState.DEPOSIT_RUNES -> {
                if (Backpack.backpack().query().nameContains(" rune").results().isEmpty()) {
                    context.state = if (context.nextRunecraftingAltarSpawnSeconds < 25) {
                        MinigameState.CRAFT_WITH_FILLING_POUCHES
                    } else {
                        MinigameState.COLLECT_UNCHARGED_CELLS
                    }
                }
            }
            MinigameState.CRAFT_WITH_FILLING_POUCHES -> {
                if (!containsGuardianFragments() || (Backpack.backpack().isFull && Pouch.allPouchesFilled())) {
                    context.state = MinigameState.CRAFT_GUARDIAN_STONES
                }
            }
            MinigameState.WAIT -> {
            }
        }
    }
}