package com.pickyberry.internshipassignment.domain

import java.io.File

interface Repository {
    fun getAllFiles(root: File, fileList: MutableList<FileItem>)
}