package com.pickyberry.internshipassignment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FileEntity(
    @PrimaryKey val absolutePath: String,
    val hash: Int
)