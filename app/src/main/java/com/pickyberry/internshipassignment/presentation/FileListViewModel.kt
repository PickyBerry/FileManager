package com.pickyberry.internshipassignment.presentation

import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pickyberry.internshipassignment.domain.SortTypes
import com.pickyberry.internshipassignment.data.RepositoryImpl
import com.pickyberry.internshipassignment.domain.FileItem
import com.pickyberry.internshipassignment.domain.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Collections

class FileListViewModel : ViewModel() {

    private val repository: Repository = RepositoryImpl()
    private val _currentFiles = MutableLiveData<List<FileItem>>()
    val currentFiles = _currentFiles
    var sortedBy = SortTypes.NAMES_ASC
    val loading = MutableLiveData<Boolean>()
    var showingUpdatedFiles = false
    private var allFiles = listOf<FileItem>()
    private var updatedFiles = listOf<FileItem>()
    private var currentRootPath = Environment.getExternalStorageDirectory().absolutePath


    init {
        getFiles(currentRootPath, SortTypes.NAMES_ASC)
    }

    fun getFiles(rootPath: String, newType: SortTypes?) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUpdatedFiles(File(rootPath), mutableListOf<FileItem>())
            loading.postValue(true)

            val sortType = newType ?: sortedBy
            val list = mutableListOf<FileItem>()

            repository.getFiles(File(rootPath), list)
            currentRootPath = rootPath
            sort(list, sortType)
        }


    fun sort(list: MutableList<FileItem>?, type: SortTypes) {
        val newList = list ?: currentFiles.value!!.toMutableList()
        if (shouldReverse(type)) newList.reverse()
        else when (type) {
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
        val newRootPath = currentRootPath.replaceAfterLast('/',"").dropLast(1)
        Log.e("path",newRootPath)
        getFiles(newRootPath,sortedBy)
    }

    fun switchBetweenAllAndUpdated() {
        showingUpdatedFiles = !showingUpdatedFiles
        if (showingUpdatedFiles) allFiles = currentFiles.value!!
        else updatedFiles = currentFiles.value!!
        //  getFiles(sortedBy)
    }

    private fun shouldReverse(type: SortTypes): Boolean {
        return ((type == SortTypes.NAMES_ASC && sortedBy == SortTypes.NAMES_DESC) ||
                (type == SortTypes.NAMES_DESC && sortedBy == SortTypes.NAMES_ASC) ||
                (type == SortTypes.SIZE_ASC && sortedBy == SortTypes.SIZE_DESC) ||
                (type == SortTypes.SIZE_DESC && sortedBy == SortTypes.SIZE_ASC) ||
                (type == SortTypes.DATE_ASC && sortedBy == SortTypes.DATE_DESC) ||
                (type == SortTypes.DATE_DESC && sortedBy == SortTypes.DATE_ASC) ||
                (type == SortTypes.EXT_ASC && sortedBy == SortTypes.EXT_DESC) ||
                (type == SortTypes.EXT_DESC && sortedBy == SortTypes.EXT_ASC))
    }

}