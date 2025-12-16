package tech.rkanelabs.marblelab.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.rkanelabs.marblelab.util.CoroutineDispatcherProvider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoroutinesModule {
    @Provides
    @Singleton
    fun provideCoroutineDispatcherProvider() = CoroutineDispatcherProvider()
}