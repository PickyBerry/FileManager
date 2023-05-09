package com.pickyberry.internshipassignment.di

import android.app.Application
import androidx.room.Room
import com.pickyberry.internshipassignment.data.FilesDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {

    @Singleton
    @Provides
    fun provideUserDatabase(app: Application): FilesDatabase {
        return Room.databaseBuilder(
            app,
            FilesDatabase::class.java,
            "userdb.db"
        ).build()
    }

}