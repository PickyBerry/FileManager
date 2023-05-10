package com.pickyberry.internshipassignment.presentation

import android.os.Environment
import android.util.Log
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

//Viewmodel for main screen
class FileListViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    //Livedata of files to be observed by fragment
    private val _currentFiles = MutableLiveData<List<FileItem>>()
    val currentFiles = _currentFiles

    //Our states
    private var currentRootPath = Environment.getExternalStorageDirectory().absolutePath
    private var sortedBy = SortTypes.NAMES_ASC
    val loading = MutableLiveData<Boolean>()
    var showingUpdatedFiles = false

    //Coroutine jobs to do file hashing in the background
    private var updatedFilesJob: Job? = null
    private val updatedFilesDeferred: Deferred<List<FileItem>> =
        viewModelScope.async(Dispatchers.IO) {
            val updatedFiles = repository.getUpdatedFiles(File(Environment.getExternalStorageDirectory().absolutePath), mutableListOf())
            updatedFiles
        }


    init {
        getFiles(currentRootPath, SortTypes.NAMES_ASC)
    }

    //Get files from specific folder
    fun getFiles(rootPath: String, newType: SortTypes?) =
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)

            val sortType = newType ?: sortedBy
            val list = mutableListOf<FileItem>()

            repository.getFiles(File(rootPath), list)
            currentRootPath = rootPath
            sort(list, sortType)
        }


    //Sort incoming list
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
                FileItem.sortIsDirectory().thenComparing(FileItem.sortExtensionsDescending())
            )
        }
        _currentFiles.postValue(newList)
        sortedBy = type
        loading.postValue(false)
    }

    //Navigate back if possible
    fun goBack(): Boolean {
        if (showingUpdatedFiles) {
            switchBetweenAllAndUpdated()
            return true
        }
        else if (currentRootPath!=Environment.getExternalStorageDirectory().absolutePath) {
            Log.e("um",currentRootPath)
            val newRootPath = currentRootPath.replaceAfterLast('/', "").dropLast(1)
            Log.e("um",newRootPath)
            getFiles(newRootPath, sortedBy)
            return true
        }
        return false
    }

    //Update data to display all files or updated files
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