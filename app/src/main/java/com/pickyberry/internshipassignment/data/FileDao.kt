package com.pickyberry.internshipassignment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface FileDao {

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity)

    @Query("DELETE FROM fileentity")
    suspend fun clearFiles()

    @Query("SELECT * FROM fileentity where :path == absolutePath ")
    suspend fun getFileByPath(path: String): FileEntity?
}