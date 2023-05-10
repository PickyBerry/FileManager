package com.pickyberry.internshipassignment.domain


import java.io.File

//Interface for repository to have dependency inversion
interface Repository {

    suspend fun getFiles(root: File, fileList: MutableList<FileItem>)

    suspend fun getUpdatedFiles(root: File, fileList: MutableList<FileItem>):List<FileItem>

}