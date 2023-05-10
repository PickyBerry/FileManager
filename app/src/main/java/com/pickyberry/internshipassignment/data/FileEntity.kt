package com.pickyberry.internshipassignment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entity class for database keeps MD5 hash for every file
@Entity
data class FileEntity(
    @PrimaryKey val absolutePath: String,
    val hash: String
)