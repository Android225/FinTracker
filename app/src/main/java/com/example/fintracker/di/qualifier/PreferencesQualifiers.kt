package com.example.fintracker.di.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsPreferences

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthPreferences
