package com.example.fintracker.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Набор текстовых стилей приложения.
val Typography = Typography(

    // ================================================================
    // Герой-сумма: "323 524 ₽", "1 322 444 ₽" (Hero-блок)
    // Figma: fontSize=45, fontWeight=700, letterSpacing=-1, lineHeightPx=54
    // ================================================================
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(700),
        fontSize = 45.sp,
        lineHeight = 45.sp,
        letterSpacing = (-1.0).sp
    ),

    // ================================================================
    // Названия операций/счетов: "Покупка канцтоваров", "Яндекс Pay"
    // Figma: fontSize=16, fontWeight=500, letterSpacing=0, lineHeightPx=19.2
    // ================================================================
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight(500),
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    ),

    // ================================================================
    // Суммы в списках ("1 200 ₽"), заголовки секций ("расходы, всего"),
    // кнопка фильтра ("12 июня"), время в статус-баре ("9:41")
    // Figma: fontSize=14, fontWeight=500, letterSpacing=0, lineHeightPx=16.8
    // ================================================================
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp
    ),

    // ================================================================
    // Табы нижней навигации: "Расходы", "Доходы", "Счета"
    // Figma: fontSize=12, fontWeight=500, letterSpacing=0, lineHeightPx=14.4
    // ================================================================
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp
    ),
    //Analytic
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 33.sp,
        letterSpacing = 0.sp
    ),
)
