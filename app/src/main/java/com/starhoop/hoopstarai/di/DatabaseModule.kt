package com.starhoop.hoopstarai.di

import android.content.Context
import androidx.room.Room
import com.starhoop.hoopstar.data.local.db.HoopStarDao
import com.starhoop.hoopstar.data.local.db.HoopStarDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HoopStarDatabase =
        Room.databaseBuilder(context, HoopStarDatabase::class.java, "hoopstar.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideDao(db: HoopStarDatabase): HoopStarDao = db.dao()
}