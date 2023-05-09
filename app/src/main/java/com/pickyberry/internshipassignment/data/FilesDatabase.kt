package com.pickyberry.internshipassignment.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [FileEntity::class], version = 1, exportSchema = true
)
abstract class FilesDatabase : RoomDatabase() {
    abstract val dao: FileDao
}