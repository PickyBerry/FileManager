package com.pickyberry.internshipassignment

enum class SortTypes(val value: Int) {
    NAMES_ASC(0),NAMES_DESC(1),SIZE_ASC(2),SIZE_DESC(3),DATE_ASC(4),DATE_DESC(5),EXT_ASC(6),EXT_DESC(7);

    companion object {
        infix fun from(value: Int): SortTypes? = SortTypes.values().firstOrNull { it.value == value }
    }
}