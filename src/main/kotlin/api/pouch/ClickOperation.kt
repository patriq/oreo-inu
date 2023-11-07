package api.pouch

internal data class ClickOperation(val pouch: Pouch, val tick: Int, val delta: Int = 0)