package com.example.fintracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.fintracker.di.qualifier.AuthPreferences
import com.example.fintracker.di.qualifier.SettingsPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    @SettingsPreferences
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = createDataStore(context, "settings")

    @Provides
    @Singleton
    @AuthPreferences
    fun provideAuthDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = createDataStore(context, "auth")

    private fun createDataStore(context: Context, name: String): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            produceFile = { context.preferencesDataStoreFile(name) }
        )
}
