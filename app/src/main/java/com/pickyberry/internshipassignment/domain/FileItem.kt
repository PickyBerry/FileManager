package com.pickyberry.internshipassignment.domain

import java.nio.file.attribute.FileTime
import java.util.*
import kotlin.Comparator

data class FileItem(
    val path: String,
    val size: Long,
    val creationDate: FileTime,
    val isDirectory: Boolean
) {
    companion object {

        fun sortIsDirectory(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o2!!.isDirectory.compareTo(o1!!.isDirectory) }

        fun sortNamesAscending(): Comparator<FileItem> = Comparator<FileItem> { o1, o2 ->
            o1!!.path.split('/').last().lowercase(Locale.getDefault())
                .compareTo(o2!!.path.split('/').last().lowercase(Locale.getDefault()))
        }

        fun sortNamesDescending(): Comparator<FileItem> = Comparator<FileItem> { o1, o2 ->
            o2!!.path.split('/').last().lowercase(Locale.getDefault())
                .compareTo(o1!!.path.split('/').last().lowercase(Locale.getDefault()))
        }

        fun sortSizesAscending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o1!!.size.compareTo(o2!!.size) }

        fun sortSizesDescending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o2!!.size.compareTo(o1!!.size) }

        fun sortDatesAscending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o1!!.creationDate.compareTo(o2!!.creationDate) }

        fun sortDatesDescending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 -> o2!!.creationDate.compareTo(o1!!.creationDate) }

        fun sortExtensionsAscending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 ->
                o1!!.path.split('.').last().compareTo(o2!!.path.split('.').last())
            }

        fun sortExtensionsDescending(): Comparator<FileItem> =
            Comparator<FileItem> { o1, o2 ->
                o2!!.path.split('.').last().compareTo(o1!!.path.split('.').last())
            }
    }
}