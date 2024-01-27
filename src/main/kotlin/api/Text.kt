package api

import java.util.regex.Pattern

object Text {
    private val TAG_REGEXP = Pattern.compile("<[^>]*>")

    fun removeTags(str: String?): String {
        return TAG_REGEXP.matcher(str).replaceAll("")
    }

    fun generateDoseNames(name: String, doses: Int, includeSpace: Boolean = false): Array<String> {
        val result = mutableListOf<String>()
        for (i in 0 until doses) {
            result.add("$name${if (includeSpace) " " else ""}(${i + 1})")
        }
        return result.toTypedArray()
    }
}