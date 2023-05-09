package com.pickyberry.internshipassignment.di

import android.app.Application
import com.pickyberry.internshipassignment.data.FilesDatabase
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

interface DbComponent {
    @Singleton
    @Component(modules = [DbModule::class])
    interface DbComponent{

        fun userDb(): FilesDatabase

        @Component.Builder
        interface Builder {

            @BindsInstance
            fun application(application: Application): Builder

            fun build(): DbComponent

        }

    }
}