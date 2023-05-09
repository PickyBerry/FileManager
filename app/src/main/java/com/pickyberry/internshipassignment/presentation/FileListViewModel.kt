package com.pickyberry.internshipassignment.presentation

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pickyberry.internshipassignment.domain.FileItem
import com.pickyberry.internshipassignment.domain.Repository
import com.pickyberry.internshipassignment.domain.SortTypes
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import javax.inject.Inject

class FileListViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _currentFiles = MutableLiveData<List<FileItem>>()
    val currentFiles = _currentFiles

    private var currentRootPath = Environment.getExternalStorageDirectory().absolutePath
    var sortedBy = SortTypes.NAMES_ASC
    val loading = MutableLiveData<Boolean>()
    var showingUpdatedFiles = false

    private var updatedFilesJob: Job? = null
    private val updatedFilesDeferred: Deferred<List<FileItem>> =
        viewModelScope.async(Dispatchers.IO) {
            val updatedFiles = repository.getUpdatedFiles(File("/storage/emulated/0/Download/VK"), mutableListOf())
            updatedFiles
        }


    init {
        getFiles(currentRootPath, SortTypes.NAMES_ASC)
    }

    fun getFiles(rootPath: String, newType: SortTypes?) =
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)

            val sortType = newType ?: sortedBy
            val list = mutableListOf<FileItem>()

            repository.getFiles(File(rootPath), list)
            currentRootPath = rootPath
            sort(list, sortType)
        }


    fun sort(list: MutableList<FileItem>?, type: SortTypes) {
        val newList = list ?: currentFiles.value!!.toMutableList()
        when (type) {
            //ПОПРОБОВАТЬ ЗАМЕНИТЬ НА list.sortBy !!!!!!!!!!
            SortTypes.NAMES_ASC -> Collections.sort(
                newList,
                FileItem.sortIsDirectory().thenComparing(FileItem.sortNamesAscending())
            )

            SortTypes.NAMES_DESC -> Collections.sort(
                newList,
                FileItem.sortIsDirectory().thenComparing(FileItem.sortNamesDescending())
            )

            SortTypes.SIZE_ASC -> Collections.sort(
                newList,
                FileItem.sortIsDirectory().thenComparing(FileItem.sortSizesAscending())
            )

            SortTypes.SIZE_DESC -> Collections.sort(
                newList,
                FileItem.sortIsDirectory().thenComparing(FileItem.sortSizesDescending())
            )

            SortTypes.DATE_ASC -> Collections.sort(
                newList,
                FileItem.sortIsDirectory().thenComparing(FileItem.sortDatesAscending())
            )

            SortTypes.DATE_DESC -> Collections.sort(
                newList,
                FileItem.sortIsDirectory().thenComparing(FileItem.sortDatesDescending())
            )

            SortTypes.EXT_ASC -> Collections.sort(
                newList,
                FileItem.sortIsDirectory().thenComparing(FileItem.sortExtensionsAscending())
            )

            SortTypes.EXT_DESC -> Collections.sort(
                newList,
                FileItem.sortExtensionsDescending()
            )
        }
        _currentFiles.postValue(newList)
        sortedBy = type
        loading.postValue(false)
    }

    fun goBack() {
        if (currentRootPath!=Environment.getExternalStorageDirectory().absolutePath) {
            val newRootPath = currentRootPath.replaceAfterLast('/', "").dropLast(1)
            getFiles(newRootPath, sortedBy)
        }
    }

    fun switchBetweenAllAndUpdated() {
        showingUpdatedFiles = !showingUpdatedFiles
        if (showingUpdatedFiles) {
            updatedFilesJob = viewModelScope.launch {
                loading.postValue(true)
                val result = updatedFilesDeferred.await()
                if (showingUpdatedFiles) sort(result.toMutableList(), sortedBy)
            }
        } else {
            getFiles(currentRootPath, sortedBy)
        }
    }

}