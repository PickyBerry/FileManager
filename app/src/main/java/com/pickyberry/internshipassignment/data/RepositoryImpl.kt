package com.pickyberry.internshipassignment.data

import android.util.Log
import com.pickyberry.internshipassignment.domain.FileItem
import com.pickyberry.internshipassignment.domain.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.*

class RepositoryImpl(
    //  private val db: FilesDatabase
) : Repository {

    private val updatedFiles = listOf<String>()
    override suspend fun getAllFiles(root: File, fileList: MutableList<FileItem>) {
        withContext(Dispatchers.IO) {
            if (root.isDirectory) {
                // Get all files and directories in the current directory
                val files = root.listFiles()
                //Log.e("lol",root.absolutePath)
                // Log.e("lol", root.listFiles()?.toString() ?: "")
                files?.let {
                    for (file in it)
                    // Recursively call this method for each directory
                        if (file.isDirectory)
                            getAllFiles(file, fileList)
                        else
                            fileList.add(
                                FileItem(
                                    file.absolutePath,
                                    file.length(),
                                    Files.readAttributes(
                                        file.toPath(),
                                        BasicFileAttributes::class.java
                                    ).creationTime()
                                )
                            )


                }

            } else {
                Log.e("test2", root.absolutePath)
                // If the root is a file, add its path to the list
                fileList.add(
                    FileItem(
                        root.absolutePath,
                        root.length(),
                        Files.readAttributes(root.toPath(), BasicFileAttributes::class.java)
                            .creationTime()
                    )
                )
            }
        }
    }

    override suspend fun getUpdatedFiles() = withContext(Dispatchers.IO) {
        //TODO
    }

    override suspend fun insertFile(fileEntity: FileEntity) =
        withContext(Dispatchers.IO) { /*db.dao.insertFile(fileEntity)*/ }

    /*override suspend fun getByPath(path: String): FileEntity =
        withContext(Dispatchers.IO) { db.dao.getFileByPath(path) }*/

    override suspend fun clearFiles() {
        withContext(Dispatchers.IO) {
            /* db.dao.clearFiles()*/
        }
    }
}