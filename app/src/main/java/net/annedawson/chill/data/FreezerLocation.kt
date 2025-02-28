package net.annedawson.chill.data

enum class FreezerLocation(val id: Int, val locationName: String) {
    TOP_LEFT(0, "Top-Left"),
    TOP_MIDDLE(1, "Top-Middle"),
    TOP_RIGHT(2, "Top-Right"),
    MIDDLE_LEFT(3, "Middle-Left"),
    MIDDLE_MIDDLE(4, "Middle-Middle"),
    MIDDLE_RIGHT(5, "Middle-Right"),
    BOTTOM_LEFT(6, "Bottom-Left"),
    BOTTOM_MIDDLE(7, "Bottom-Middle"),
    BOTTOM_RIGHT(8, "Bottom-Right"),
    DOOR_LEFT(9, "Door-Left"),
    DOOR_RIGHT(10, "Door-Right");

    companion object {
        fun fromId(id: Int): FreezerLocation? {
            return values().find { it.id == id }
        }
    }
}