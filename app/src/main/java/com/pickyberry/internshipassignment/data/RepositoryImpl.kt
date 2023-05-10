package com.pickyberry.internshipassignment.data


import com.pickyberry.internshipassignment.domain.FileItem
import com.pickyberry.internshipassignment.domain.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest

class RepositoryImpl(
    private val db: FilesDatabase,
) : Repository {

    //Updated files to be sent to viewmodel
    private val updatedFiles = mutableListOf<FileItem>()

    //all files to update database
    private val allFiles = mutableListOf<FileEntity>()


    //List the files within folder
    override suspend fun getFiles(root: File, fileList: MutableList<FileItem>) {
        withContext(Dispatchers.IO) {
            val files = root.listFiles()
            files?.let {
                for (file in it) {
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

    //Calculate new hashes and update the database
    override suspend fun getUpdatedFiles(
        root: File,
        fileList: MutableList<FileItem>,
    ): List<FileItem> {
        withContext(Dispatchers.IO) {
            getHashes(root, fileList)
            clearFiles()
            insertFiles(allFiles)
        }
        return updatedFiles
    }

    //Calculate hashes for all files from root folder
    private suspend fun getHashes(root: File, fileList: MutableList<FileItem>) {
        if (root.isDirectory) {
            val files = root.listFiles()
            files?.let {
                for (file in it) {
                    if (file.isDirectory)
                        getHashes(file, fileList)
                    else {
                        val hash = getFileChecksum(file)
                        val oldHash = getByPath(file.absolutePath)?.hash
                        if (oldHash == null || oldHash != hash) {
                            updatedFiles.add(
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
                        allFiles.add(FileEntity(file.absolutePath, hash))
                    }
                }

            }
        }
    }

    //MD5 hashing for files
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

    private suspend fun insertFiles(fileEntities: List<FileEntity>) =
        withContext(Dispatchers.IO) { db.dao.insertFiles(fileEntities) }

    private suspend fun getByPath(path: String): FileEntity? =
        withContext(Dispatchers.IO) { db.dao.getFileByPath(path) }

    private suspend fun clearFiles() {
        withContext(Dispatchers.IO) {
            db.dao.clearFiles()
        }
    }
}