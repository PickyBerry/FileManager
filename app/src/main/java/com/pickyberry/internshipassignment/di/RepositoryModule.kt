package com.pickyberry.internshipassignment.di

import com.pickyberry.internshipassignment.data.FilesDatabase
import com.pickyberry.internshipassignment.data.RepositoryImpl
import com.pickyberry.internshipassignment.domain.Repository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideRepository(db: FilesDatabase): Repository = RepositoryImpl(db)

}