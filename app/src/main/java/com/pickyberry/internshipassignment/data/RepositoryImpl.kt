package com.pickyberry.internshipassignment.data

import android.util.Log
import com.pickyberry.internshipassignment.domain.FileItem
import com.pickyberry.internshipassignment.domain.Repository
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.*

class RepositoryImpl: Repository {

    override fun getAllFiles(root: File, fileList: MutableList<FileItem>) {
        if (root.isDirectory) {
            // Get all files and directories in the current directory
            val files = root.listFiles()
            files?.let {
                for (file in it) {
                    // Recursively call this method for each directory
                    if (file.isDirectory) {
                        getAllFiles(file, fileList)
                    } else {
                        // Add the file path to the list
                        if (!file.name.contains("IMG"))
                        fileList.add(FileItem(
                            file.name,
                            file.length(),
                            Files.readAttributes(file.toPath(), BasicFileAttributes::class.java).creationTime()
                        ))
                    }
                }
            }
        } else {
            // If the root is a file, add its path to the list
            fileList.add(FileItem(
                root.name,
                root.length(),
                Files.readAttributes(root.toPath(), BasicFileAttributes::class.java).creationTime()
            ))
        }
    }
}