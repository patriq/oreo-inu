package tithe

import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.adapter.scene.SceneObject
import org.rspeer.game.component.tdi.Tab
import org.rspeer.game.component.tdi.Tabs
import org.rspeer.game.movement.Movement
import org.rspeer.game.position.Position
import org.rspeer.game.scene.Players
import org.rspeer.game.scene.SceneObjects

class Patch(
    private val instancedPatchPosition: Position,
    private val instanceActionPosition: Position,
    val forceWalk: Boolean
) {
    val patchPosition: Position
        get() {
            return instancedPatchPosition.instancePositions.first()
        }

    val actionPosition: Position
        get() {
            return instanceActionPosition.instancePositions.first()
        }

    fun getState(seed: Seed): PathState {
        val patch = patch() ?: return PathState.NO_STATE
        if (patch.name.contains("Blighted")) {
            return PathState.DEAD
        }
        return seed.idToStateMapping[patch.id] ?: PathState.NO_STATE
    }

    fun handle(seed: Seed): Boolean {
        val state = getState(seed)
        val patch = patch() ?: return false
        val player = Players.self()
        val actionPosition = actionPosition

        // Do nothing if moving to the patch
        if (player.isMoving && player.position != actionPosition) {
            return true
        }

        // Walk to the patch if not there
        if (player.position != actionPosition && forceWalk) {
            Movement.walkTowards(actionPosition)
            return true
        }

        when (state) {
            PathState.EMPTY -> {
                Tabs.open(Tab.INVENTORY)
                val seed = Backpack.backpack().query().names(seed.seedName).results().firstOrNull() ?: return false
                return Backpack.backpack().use(seed, patch)
            }
            PathState.DEAD -> return patch.interact("Clear")
            PathState.UNWATERED_SEEDLING, PathState.UNWATERED_PLANT_FLOWER, PathState.UNWATERED_PLANT_VEG -> {
                Tabs.open(Tab.INVENTORY)
                val can = Backpack.backpack().query().names("Gricoller's can", "Watering can(").results().firstOrNull()
                    ?: return false
                return Backpack.backpack().use(can, patch)
            }
            PathState.PLANT_HARVEST -> return patch.interact("Harvest")
            else -> return true // No action needed
        }
    }

    private fun patch(): SceneObject? = SceneObjects.query().on(patchPosition).results().firstOrNull()
}

enum class Seed(
    val seedName: String,
    val batchExperience: Int,
    val idToStateMapping: Map<Int, PathState>
) {
    NONE(
        "None", 0, mapOf()
    ),
    LOGAVANO_SEED(
        "Logavano seed", 37030, mapOf(
            27383 to PathState.EMPTY,
            27406 to PathState.UNWATERED_SEEDLING,
            27407 to PathState.SEEDLING,
            27409 to PathState.UNWATERED_PLANT_VEG,
            27410 to PathState.PLANT_VEG,
            27412 to PathState.UNWATERED_PLANT_FLOWER,
            27413 to PathState.PLANT_FLOWER,
            27415 to PathState.PLANT_HARVEST
        )
    );
}

enum class PathState {
    NO_STATE,
    DEAD,
    EMPTY,
    UNWATERED_SEEDLING,
    SEEDLING,
    UNWATERED_PLANT_VEG,
    PLANT_VEG,
    UNWATERED_PLANT_FLOWER,
    PLANT_FLOWER,
    PLANT_HARVEST;
}