package slayer.task

import org.rspeer.game.component.Dialog
import org.rspeer.game.component.tdi.Magic
import org.rspeer.game.component.tdi.Spell
import org.rspeer.game.scene.SceneObjects
import org.rspeer.game.script.Task
import org.rspeer.game.script.TaskDescriptor
import slayer.*
import slayer.data.Constants.NPC_ASSIGN_MESSAGE
import slayer.data.Constants.OCCULT_ALTAR
import javax.inject.Inject


@TaskDescriptor(
    name = "Get slayer assignment",
    blocking = true,
    blockIfSleeping = true,
)
class GetSlayerAssignmentTask @Inject constructor(private val ctx: ScriptContext) : Task() {
    override fun execute(): Boolean {
        // Check if we have a task
        if (ctx.getTask() != slayer.data.SlayerTask.NONE) {
            return false
        }

        // Turn off prayer
        turnOffPrayers()

        // Check if need restore stats
        if (shouldRestoreStats()) {
            if (!teleportHouse()) {
                return true
            }

            restoreStats()
            return true
        }

        // Check if need to change spell book
        if (Magic.getBook() != Magic.Book.LUNAR) {
            if (!teleportHouse()) {
                return true
            }

            val altar = SceneObjects.query().names(OCCULT_ALTAR).results().nearest() ?: return true
            altar.interact("Lunar")
            return true
        }

        // Perform NPC contact with Duradel
        if (Dialog.getOpenType(true) == null) {
            Magic.interact(Spell.Lunar.NPC_CONTACT, "Duradel")
            sleep(8)
            return true
        }

        // Check if you already have an assignment
        if (dialogueContains("You're still hunting")) {
            ctx.setTask(slayer.data.SlayerTask.UNKNOWN)
            return true
        }

        // Scroll text until you find the assignment
        var match: MatchResult? = null
        if (dialogueContains("Your new task is")) {
            match = dialogueContains(NPC_ASSIGN_MESSAGE) ?: return false
        }
        if (match == null) {
            Dialog.process("I need another assignment.")
            return true
        }

        // Parse the text and set the task
        val name = match.groups[2]?.value ?: return false
        ctx.setTask(slayer.data.SlayerTask.fromTaskName(name))
        return true
    }

    private fun dialogueContains(regex: Regex): MatchResult? {
        val text = Dialog.getText() ?: return null
        return regex.find(text)
    }

    private fun dialogueContains(substring: String): Boolean {
        val text = Dialog.getText() ?: return false
        return text.contains(substring)
    }
}