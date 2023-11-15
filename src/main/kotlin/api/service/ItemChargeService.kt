package api.service

import org.rspeer.event.Service
import org.rspeer.event.Subscribe
import org.rspeer.game.action.ActionOpcode
import org.rspeer.game.adapter.component.inventory.Equipment
import org.rspeer.game.component.Interfaces
import org.rspeer.game.event.ChatMessageEvent
import org.rspeer.game.event.ClientScriptEvent
import org.rspeer.game.event.MenuActionEvent
import java.util.regex.Pattern
import javax.inject.Singleton


@Singleton
class ItemChargeService : Service {
    private companion object {
        private val BINDING_CHECK_PATTERN: Pattern = Pattern.compile(
            "You have ([0-9]+|one) charges? left before your Binding necklace disintegrates\\."
        )
        private val BINDING_USED_PATTERN: Pattern = Pattern.compile(
            "You (partially succeed to )?bind the temple's power into (mud|lava|steam|dust|smoke|mist) runes\\."
        )
        private const val BINDING_BREAK_TEXT = "Your Binding necklace has disintegrated."
        private const val MAX_BINDING_CHARGES = 16
        private const val BINDING_NECKLACE_ID = 5521
    }

    var bindingNecklaceCharges = -1

    override fun onSubscribe() {
        bindingNecklaceCharges = -1
    }

    override fun onUnsubscribe() {
    }

    @Subscribe
    fun onChatMessage(event: ChatMessageEvent) {
        val message = event.contents
        val bindingNecklaceCheckMatcher = BINDING_CHECK_PATTERN.matcher(message)
        val bindingNecklaceUsedMatcher = BINDING_USED_PATTERN.matcher(message)

        if (message.contains(BINDING_BREAK_TEXT)) {
            // This chat message triggers before the used message so add 1 to the max charges to ensure proper sync
            bindingNecklaceCharges = MAX_BINDING_CHARGES + 1
        } else if (bindingNecklaceUsedMatcher.find()) {
            if (Equipment.equipment().query().ids(BINDING_NECKLACE_ID).results().isNotEmpty()) {
                bindingNecklaceCharges--
            }
        } else if (bindingNecklaceCheckMatcher.find()) {
            val match = bindingNecklaceCheckMatcher.group(1)
            var charges = 1
            if (!match.equals("one")) {
                charges = Integer.parseInt(match)
            }
            bindingNecklaceCharges = charges
        }
    }

    @Subscribe
    fun onScriptEvent(event: ClientScriptEvent) {
        val scriptEvent = event.source

        // Destroy event
        if (scriptEvent.scriptId == 6008) {
            val optionSelected = scriptEvent.args.last() as? Int ?: return
            if (optionSelected == 1) {
                checkDestroyWidget()
            }
        }
    }

    @Subscribe
    fun onMenuActionEvent(event: MenuActionEvent) {
        val menuAction = event.source
        // Destroy yes option
        if (menuAction.opcode == ActionOpcode.COMPONENT_ACTION && menuAction.tertiary == 38273025) {
            checkDestroyWidget()
        }
    }

    private fun checkDestroyWidget() {
        if (Interfaces.getDirect(584, 6)?.text == "Binding necklace") {
            bindingNecklaceCharges = MAX_BINDING_CHARGES
        }
    }
}