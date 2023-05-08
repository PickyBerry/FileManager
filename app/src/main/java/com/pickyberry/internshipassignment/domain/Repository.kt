package com.pickyberry.internshipassignment.domain

import com.pickyberry.internshipassignment.data.FileEntity
import java.io.File

interface Repository {
    suspend fun getAllFiles(root: File, fileList: MutableList<FileItem>)

    suspend fun getUpdatedFiles()

    suspend fun insertFile(fileEntity: FileEntity)

   // suspend fun getByPath(path: String):FileEntity

    suspend fun clearFiles()
}