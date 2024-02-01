package slayer

import slayer.data.Settings
import slayer.data.SlayerTask
import slayer.slayertaskinfo.*
import javax.inject.Singleton

@Singleton
class ScriptContext {
    private var slayerTask: SlayerTask = SlayerTask.UNKNOWN
    private var noneTaskMillis: Long = -1
    private var superiorSpawned = false

    private val supportedTasks: MutableMap<SlayerTask, SlayerTaskInfo> = mutableMapOf(
        SlayerTask.KALPHITE to Kalphite(this),
        SlayerTask.TROLLS to Trolls(this),
        SlayerTask.ELVES to Elves(this),
        SlayerTask.BLACK_DEMONS to BlackDemons(this),
        SlayerTask.FIRE_GIANTS to FireGiants(this),
        SlayerTask.GREATER_DEMONS to GreaterDemons(this),
        SlayerTask.DAGANNOTH to Dagannoth(this),
        SlayerTask.ANKOU to Ankou(this),
        SlayerTask.DUST_DEVILS to DustDevils(this),
        SlayerTask.MUTATED_ZYGOMITES to MutadedZygomites(this),
        SlayerTask.BLOODVELD to MutatedBloodveld(this),
        SlayerTask.HELLHOUNDS to Hellhounds(this),
        SlayerTask.BLACK_DRAGONS to BlackDragons(this),
        SlayerTask.KURASK to Nechryael(this),
        SlayerTask.ABERRANT_SPECTRES to AberrantSpectres(this),
        SlayerTask.SUQAHS to Suqahs(this),
        SlayerTask.GARGOYLES to Gargoyles(this),
        SlayerTask.STEEL_DRAGONS to SteelDragons(this),
        SlayerTask.NECHRYAEL to Nechryael(this),
        SlayerTask.ADAMANT_DRAGONS to AdamantDragons(this),
        SlayerTask.ABYSSAL_DEMONS to AbyssalDemons(this),
        SlayerTask.RUNE_DRAGONS to RuneDragons(this),
    )
    private var lastSupportedTask: SlayerTaskInfo? = null

    fun setTask(slayerTask: SlayerTask) {
        this.slayerTask = slayerTask
        this.noneTaskMillis = -1
    }

    fun removeTask() {
        setTask(SlayerTask.NONE)
    }

    fun removeTaskAfterMillis(millis: Long) {
        noneTaskMillis = System.currentTimeMillis() + millis
    }

    fun getTask(): SlayerTask {
        if (noneTaskMillis != -1L && System.currentTimeMillis() > noneTaskMillis) {
            return SlayerTask.NONE
        }
        return slayerTask
    }

    fun currentTaskInfo(): SlayerTaskInfo? {
        // Will return nullptr if not found (this will initialize the entry, but still returning nullptr).
        val taskInfo = supportedTasks[getTask()]
        if (taskInfo != null) {
            lastSupportedTask = taskInfo
        }
        // We return the last supported task info to prevent race conditions.
        return lastSupportedTask
    }

    fun hasTask(): Boolean {
        val task = getTask()
        return task != SlayerTask.NONE && task != SlayerTask.UNKNOWN
    }

    fun hasInvalidTask(): Boolean {
        return getTask() == SlayerTask.INVALID
    }

    fun isTaskSupported(): Boolean {
        return supportedTasks.containsKey(getTask())
    }

    fun shouldSkipTask(): Boolean {
        return Settings.SKIPPING_TASKS.contains(getTask())
    }

    fun shouldBlockTask(): Boolean {
        return Settings.BLOCKING_TASKS.contains(getTask())
    }

    fun superiorSpawned() {
        superiorSpawned = true
    }

    fun isSuperiorAlive(): Boolean {
        return superiorSpawned
    }

    fun killedSuperior() {
        superiorSpawned = false
    }
}