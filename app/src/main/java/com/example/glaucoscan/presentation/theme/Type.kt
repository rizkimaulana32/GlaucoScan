package com.example.glaucoscan.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.glaucoscan.R

// Set of Material typography styles to start with
//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//)

private val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)

val AppTypography = Typography(
    displayLarge = TextStyle(Inter, FontWeight.Bold, 36.sp, lineHeight = 56.sp, letterSpacing = (-1.6).sp),
    headlineSmall = TextStyle(Inter, FontWeight.Bold, 20.sp, lineHeight = 30.sp, letterSpacing = (-0.4).sp),
    titleMedium = TextStyle(Inter, FontWeight.SemiBold, 16.sp, lineHeight = 22.sp, letterSpacing = (-0.1).sp),
    bodyMedium = TextStyle(Inter, FontWeight.Normal, 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(Inter, FontWeight.SemiBold, 15.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(Inter, FontWeight.Medium, 13.sp, lineHeight = 18.sp),
)

private fun TextStyle(
    family: FontFamily, weight: FontWeight, size: TextUnit,
    lineHeight: TextUnit, letterSpacing: TextUnit = 0.sp,
) = androidx.compose.ui.text.TextStyle(
    fontFamily = family, fontWeight = weight, fontSize = size,
    lineHeight = lineHeight, letterSpacing = letterSpacing,
)