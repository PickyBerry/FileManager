package com.pickyberry.internshipassignment.domain

import com.pickyberry.internshipassignment.data.FileEntity
import java.io.File

interface Repository {

    suspend fun getFiles(root: File, fileList: MutableList<FileItem>)

    suspend fun getUpdatedFiles(root: File, fileList: MutableList<FileItem>):List<FileItem>

}