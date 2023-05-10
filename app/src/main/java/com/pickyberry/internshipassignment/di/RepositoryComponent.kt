package com.pickyberry.internshipassignment.di

import android.app.Application
import com.pickyberry.internshipassignment.presentation.FileListFragment
import com.pickyberry.internshipassignment.presentation.FileListViewModel
import com.pickyberry.internshipassignment.presentation.FileListViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class RepositoryScope


//Dagger2 component for repository
@RepositoryScope
@Component(
    modules = [RepositoryModule::class,ViewModelModule::class],
    dependencies = [DbComponent::class]
)
interface RepositoryComponent {

    fun inject(fragment: FileListFragment)
    fun inject(viewmodel: FileListViewModel)
    fun inject(factory: FileListViewModelFactory)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder


        fun dbComponent(dbComponent: DbComponent): Builder

        fun build(): RepositoryComponent

    }

}