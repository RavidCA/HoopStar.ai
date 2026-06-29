package com.starhoop.hoopstar.di

import com.starhoop.hoopstarai.data.remote.AuthApi
import com.starhoop.hoopstar.data.remote.HighlightsApi
import com.starhoop.hoopstar.data.remote.MappingApi
import com.starhoop.hoopstar.data.remote.TeamsApi
import com.starhoop.hoopstar.data.remote.VideosApi
import com.starhoop.hoopstar.data.repository.AuthRepositoryImpl
import com.starhoop.hoopstar.data.repository.HighlightsRepositoryImpl
import com.starhoop.hoopstar.data.repository.MappingRepositoryImpl
import com.starhoop.hoopstar.data.repository.TeamsRepositoryImpl
import com.starhoop.hoopstar.data.repository.VideosRepositoryImpl
import com.starhoop.hoopstar.domain.repository.AuthRepository
import com.starhoop.hoopstar.domain.repository.HighlightsRepository
import com.starhoop.hoopstar.domain.repository.MappingRepository
import com.starhoop.hoopstar.domain.repository.TeamsRepository
import com.starhoop.hoopstarai.domain.repository.VideosRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides @Singleton
    fun provideTeamsApi(retrofit: Retrofit): TeamsApi = retrofit.create(TeamsApi::class.java)

    @Provides @Singleton
    fun provideVideosApi(retrofit: Retrofit): VideosApi = retrofit.create(VideosApi::class.java)

    @Provides @Singleton
    fun provideMappingApi(retrofit: Retrofit): MappingApi = retrofit.create(MappingApi::class.java)

    @Provides @Singleton
    fun provideHighlightsApi(retrofit: Retrofit): HighlightsApi = retrofit.create(HighlightsApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindTeamsRepository(impl: TeamsRepositoryImpl): TeamsRepository

    @Binds @Singleton
    abstract fun bindVideosRepository(impl: VideosRepositoryImpl): VideosRepository

    @Binds @Singleton
    abstract fun bindMappingRepository(impl: MappingRepositoryImpl): MappingRepository

    @Binds @Singleton
    abstract fun bindHighlightsRepository(impl: HighlightsRepositoryImpl): HighlightsRepository
}