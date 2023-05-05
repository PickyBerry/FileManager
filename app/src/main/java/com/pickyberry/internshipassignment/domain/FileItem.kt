package com.pickyberry.internshipassignment.domain

import java.nio.file.attribute.FileTime
import java.util.*

data class FileItem(val name: String, val size: Long, val creationDate: FileTime) {
    companion object {
        fun sortNamesAscending(): Comparator<FileItem> = Comparator<FileItem> { o1, o2 ->
            o1!!.name.lowercase(Locale.getDefault())
                .compareTo(o2!!.name.lowercase(Locale.getDefault()))
        }

        fun sortNamesDescending(): Comparator<FileItem> = Comparator<FileItem> { o1, o2 ->
            o2!!.name.lowercase(Locale.getDefault())
                .compareTo(o1!!.name.lowercase(Locale.getDefault()))
        }

        fun sortSizesAscending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o1!!.size.compareTo(o2!!.size) }

        fun sortSizesDescending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o2!!.size.compareTo(o1!!.size) }

        fun sortDatesAscending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o1!!.creationDate.compareTo(o2!!.creationDate) }

        fun sortDatesDescending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o1!!.creationDate.compareTo(o2!!.creationDate) }

        fun sortExtensionssAscending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o1!!.name.split('.').last().compareTo(o2!!.name.split('.').last()) }

        fun sortExtensionsDescending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o2!!.name.split('.').last().compareTo(o1!!.name.split('.').last()) }
    }
}