package api

import java.util.regex.Pattern

object Text {
    private val TAG_REGEXP = Pattern.compile("<[^>]*>")

    fun removeTags(str: String?): String {
        return TAG_REGEXP.matcher(str).replaceAll("")
    }
}