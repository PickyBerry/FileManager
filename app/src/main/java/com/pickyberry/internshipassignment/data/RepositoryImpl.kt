package com.pickyberry.internshipassignment.data

import android.util.Log
import com.pickyberry.internshipassignment.domain.FileItem
import com.pickyberry.internshipassignment.domain.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.util.*

class RepositoryImpl(
    //  private val db: FilesDatabase
) : Repository {

    private val updatedFiles = listOf<String>()
    override suspend fun getFiles(root: File, fileList: MutableList<FileItem>) {
        withContext(Dispatchers.IO) {
            val files = root.listFiles()
            files?.let {
                for (file in it) {
              //      getFileChecksum(file)
                    fileList.add(
                        FileItem(
                            file.absolutePath,
                            file.length(),
                            Files.readAttributes(
                                file.toPath(),
                                BasicFileAttributes::class.java
                            ).creationTime(),
                            file.isDirectory
                        )
                    )
                }

            }


        }
    }


    override suspend fun getAllFiles(root: File, fileList: MutableList<FileItem>) {
        withContext(Dispatchers.IO) {
            if (root.isDirectory) {
                val files = root.listFiles()
                files?.let {
                    for (file in it)
                        if (file.isDirectory)
                            getAllFiles(file, fileList)
                        else {
                            getFileChecksum(file)
                            fileList.add(
                                FileItem(
                                    file.absolutePath,
                                    file.length(),
                                    Files.readAttributes(
                                        file.toPath(),
                                        BasicFileAttributes::class.java
                                    ).creationTime(),
                                    file.isDirectory
                                )
                            )
                        }

                }

            } else {
                Log.e("test2", root.absolutePath)
                // If the root is a file, add its path to the list
                fileList.add(
                    FileItem(
                        root.absolutePath,
                        root.length(),
                        Files.readAttributes(root.toPath(), BasicFileAttributes::class.java)
                            .creationTime(),
                        false
                    )
                )
            }
        }
    }

    private fun getFileChecksum(file: File): String {
        val digest = MessageDigest.getInstance("MD5")
        val inputStream = FileInputStream(file)
        val buffer = ByteArray(8192)
        var read = inputStream.read(buffer, 0, 8192)

        while (read != -1) {
            digest.update(buffer, 0, read)
            read = inputStream.read(buffer, 0, 8192)
        }

        inputStream.close()
        val md5sum = digest.digest()
        val output = StringBuilder()

        for (i in md5sum.indices) {
            val hex = Integer.toHexString(0xff and md5sum[i].toInt())

            if (hex.length == 1) {
                output.append('0')
            }

            output.append(hex)
        }

        return output.toString()
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