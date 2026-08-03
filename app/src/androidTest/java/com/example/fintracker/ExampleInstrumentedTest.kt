package com.example.fintracker

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

// Базовый пример теста на Android-устройстве.
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    // Проверяет package name запущенного приложения.
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.fintracker", appContext.packageName)
    }
}
