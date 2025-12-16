package tech.rkanelabs.marblelab.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AndroidModule {
    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext appContext: Context
    ) = appContext.contentResolver
}