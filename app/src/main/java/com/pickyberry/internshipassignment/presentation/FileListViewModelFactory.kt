package com.pickyberry.internshipassignment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pickyberry.internshipassignment.di.RepositoryScope
import javax.inject.Inject
import javax.inject.Provider

//Viewmodel factory
@RepositoryScope
class FileListViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull()
            ?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        return creator.get() as T
    }
}