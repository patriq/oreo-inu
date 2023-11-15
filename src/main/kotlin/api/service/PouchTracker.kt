package api.service

import api.pouch.ClickOperation
import api.pouch.Pouch
import com.google.common.collect.ImmutableMap
import com.google.inject.Singleton
import org.rspeer.event.Service
import org.rspeer.event.Subscribe
import org.rspeer.game.Game
import org.rspeer.game.Vars
import org.rspeer.game.action.ActionOpcode
import org.rspeer.game.adapter.component.inventory.Backpack
import org.rspeer.game.component.Item
import org.rspeer.game.event.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.min


@OptIn(ExperimentalStdlibApi::class)
@Singleton
class PouchTracker: Service {
    companion object {
        private const val POUCH_ADD_FULL_MESSAGE = "You cannot add any more essence to the pouch."
        private val POUCH_CHECK_MESSAGE: Pattern =
            Pattern.compile("^There (?:is|are) ([a-z-]+)(?: pure| daeyalt| guardian)? essences? in this pouch\\.$")
        private val TEXT_TO_NUMBER = ImmutableMap.builder<String, Int>()
            .put("no", 0)
            .put("one", 1)
            .put("two", 2)
            .put("three", 3)
            .put("four", 4)
            .put("five", 5)
            .put("six", 6)
            .put("seven", 7)
            .put("eight", 8)
            .put("nine", 9)
            .put("ten", 10)
            .put("eleven", 11)
            .put("twelve", 12)
            .put("thirteen", 13)
            .put("fourteen", 14)
            .put("fifteen", 15)
            .put("sixteen", 16)
            .put("seventeen", 17)
            .put("eighteen", 18)
            .put("nineteen", 19)
            .put("twenty", 20)
            .put("twenty-one", 21)
            .put("twenty-two", 22)
            .put("twenty-three", 23)
            .put("twenty-four", 24)
            .put("twenty-five", 25)
            .put("twenty-six", 26)
            .put("twenty-seven", 27)
            .put("twenty-eight", 28)
            .put("twenty-nine", 29)
            .put("thirty", 30)
            .put("thirty-one", 31)
            .put("thirty-two", 32)
            .put("thirty-three", 33)
            .put("thirty-four", 34)
            .put("thirty-five", 35)
            .put("thirty-six", 36)
            .put("thirty-seven", 37)
            .put("thirty-eight", 38)
            .put("thirty-nine", 39)
            .put("forty", 40)
            .build()
    }

    private val clickedItems: Deque<ClickOperation> = ArrayDeque()
    private val checkedPouches: Deque<ClickOperation> = ArrayDeque()
    private var lastEssence = 0
    private var lastSpace = 0
    private var gotrStarted = false
    private var lastItems: Set<Item> = emptySet()

    override fun onSubscribe() {
        // Reset pouch state
        for (pouch in Pouch.entries) {
            pouch.holding = 0
            pouch.unknown = true
            pouch.degrade(false)
        }

        lastSpace = -1
        lastEssence = -1
    }

    private fun popFirstValidCheckedPouch(): ClickOperation? {
        var op = checkedPouches.pollFirst()
        while (op != null && op.tick < Game.getTick()) {
            op = checkedPouches.pollFirst()
        }
        return op
    }

    @Subscribe
    fun onTick(event: TickEvent) {
        val currentItems = Backpack.backpack().items.toSet()
        if (currentItems != lastItems) {
            onBackpackContainerChanged()
            lastItems = currentItems
        }
    }

    @Subscribe
    fun onChatMessage(event: ChatMessageEvent) {
        if (event.type != ChatMessageEvent.Type.GAME) {
            return
        }

        val message = event.contents

        // Clear pouches when GotR starts.
        if (message.contains("The rift becomes active!")) {
            gotrStarted = true
            for (pouch in Pouch.entries) {
                pouch.holding = 0
                pouch.unknown = false
            }
        }

        if (!checkedPouches.isEmpty()) {
            if (message.equals(POUCH_ADD_FULL_MESSAGE)) {
                val op = popFirstValidCheckedPouch()
                // Make sure it was a filling operation that produced this message
                if (op != null && op.delta == 1) {
                    val pouch = op.pouch
                    // It's gotta be all the way full now.
                    pouch.holding = pouch.holdAmount
                    pouch.unknown = false
                }
            } else {
                val matcher: Matcher = POUCH_CHECK_MESSAGE.matcher(message)
                if (matcher.matches()) {
                    val num: Int = TEXT_TO_NUMBER[matcher.group(1)]!!
                    val op = popFirstValidCheckedPouch()
                    // Update if it was a check operation (delta == 0) or an empty operation and it is now empty. The
                    // empty operation only produces the message if it was already completely empty
                    if (op != null && (op.delta == 0 || op.delta == -1 && num == 0)) {
                        val pouch = op.pouch
                        pouch.holding = num
                        pouch.unknown = false
                    }
                }
            }
        }
    }

    private fun isInGotr(): Boolean {
        return Vars.get(Vars.Type.VARBIT, 5667) > 0
    }

    private fun onBackpackContainerChanged() {
        // empty pouches if you left GotR
        if (gotrStarted && !isInGotr()) {
            gotrStarted = false

            for (pouch in Pouch.entries) {
                pouch.holding = 0
                pouch.unknown = false
            }
        }

        val items = Backpack.backpack().items
        var newEss = 0
        var newSpace = 0

        // Count ess/space, and change pouch states
        for (item in items) {
            when (item.id) {
                7936, 24704, 26879 -> newEss += 1
                -1 -> newSpace += 1
                5510, 5512, 5514, 26784 -> {
                    val pouch = Pouch.forItem(item.id)
                    pouch!!.degrade(false)
                }
                5511, 5513, 5515, 26786 -> {
                    val pouch = Pouch.forItem(item.id)
                    pouch!!.degrade(true)
                }
            }
        }

        if (items.size < 28) {
            // Pad newSpace for unallocated inventory slots
            newSpace += 28 - items.size;
        }

        if (clickedItems.isEmpty()) {
            lastSpace = newSpace
            lastEssence = newEss
            return
        }

        if (lastEssence == -1 || lastSpace == -1) {
            lastSpace = newSpace
            lastEssence = newEss
            clickedItems.clear()
            return
        }

        val tick = Game.getTick()

        var essence = lastEssence
        var space = lastSpace
        // Log.fine("Begin processing ${clickedItems.size} events, last ess: ${lastEssence} space: ${lastSpace}, cur ess ${newEss}: space ${newSpace}")

        while (essence != newEss) {
            val op = clickedItems.poll()
            if (op == null) {
                // Log.fine("Ran out of updates while trying to balance essence!")
                break
            }
            if (tick > op.tick) {
                // Log.fine("Click op timed out")
                continue
            }
            val pouch = op.pouch
            val fill = op.delta > 0
            // How much ess can either be deposited or withdrawn
            val required = if (fill) pouch.remaining else pouch.holding
            // Bound to how much ess or free space we actually have, and optionally negate
            val essenceGot: Int = op.delta * min(required, if (fill) essence else space)

            // if we have enough essence or space to fill or empty the entire pouch, it no
            // longer becomes unknown
            if (pouch.unknown && (if (fill) essence else space) >= pouch.holdAmount) {
                pouch.unknown = false
            }
            // Log.fine("${pouch.name}: ${essenceGot}")
            essence -= essenceGot
            space += essenceGot
            pouch.addHolding(essenceGot)
        }

        if (!clickedItems.isEmpty()) {
            // Log.fine("End processing with ${clickedItems.size} events left")
        }

        lastSpace = newSpace
        lastEssence = newEss
    }

    @Subscribe
    fun onMenuActionEvent(event: MenuActionEvent) {
        val menuAction = event.source
        val itemId = when (menuAction.opcode) {
            ActionOpcode.ITEM_ACTION_0, ActionOpcode.ITEM_ACTION_1, ActionOpcode.ITEM_ACTION_2, ActionOpcode.ITEM_ACTION_3,
            ActionOpcode.ITEM_ACTION_4, ActionOpcode.PICKABLE_ACTION_2 -> {
                menuAction.primary
            }
            ActionOpcode.COMPONENT_ACTION, ActionOpcode.COMPONENT_ACTION_2 -> {
                menuAction.quaternary
            }
            else -> return
        }

        val pouch = Pouch.forItem(itemId) ?: return

        val tick: Int = Game.getTick() + 3
        when (menuAction.opcode) {
            ActionOpcode.COMPONENT_ACTION -> {
                when (menuAction.primary) {
                    1, 3 -> { // Fill pouch
                        clickedItems.add(ClickOperation(pouch, tick, 1))
                        checkedPouches.add(ClickOperation(pouch, tick, 1))
                    }
                    2, 4 -> { // Empty pouch
                        clickedItems.add(ClickOperation(pouch, tick, -1))
                        checkedPouches.add(ClickOperation(pouch, tick, -1))
                    }
                    5 -> { // Check pouch
                        checkedPouches.add(ClickOperation(pouch, tick))
                    }
                }
            }
            ActionOpcode.COMPONENT_ACTION_2 -> {
                when (menuAction.primary) {
                    10 -> {
                        // Fill pouch
                        if (!pouch.isFullInBank()) {
                            clickedItems.add(ClickOperation(pouch, tick, 1))
                            checkedPouches.add(ClickOperation(pouch, tick, 1))
                        } else {
                            // Empty pouch
                            clickedItems.add(ClickOperation(pouch, tick, -1))
                            checkedPouches.add(ClickOperation(pouch, tick, -1))
                        }
                    }
                }
            }
            ActionOpcode.PICKABLE_ACTION_2 -> {
                // Dropping pouches clears them, so clear when picked up
                pouch.holding = 0
            }
            else -> return
        }
    }

    @Subscribe
    fun onRenderEvent(event: RenderEvent) {
        if (event.layer != RenderLayer.ALWAYS_ON_TOP) {
            return
        }

        val items = Backpack.backpack().items
        for (item in items) {
            val pouch = Pouch.forItem(item.id) ?: continue
            event.source.drawString(
                "${if (pouch.unknown) "?" else pouch.holding}",
                (item.bounds.centerX + 5).toInt(),
                (item.bounds.centerY - 5).toInt()
            )
        }
    }

    override fun onUnsubscribe() {
    }
}