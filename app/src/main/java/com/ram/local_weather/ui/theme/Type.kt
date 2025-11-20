package com.ram.local_weather.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ram.local_weather.R

// Set of Material typography styles to start with

val juraFont  = FontFamily(
    Font(R.font.jura_regular),
    Font(R.font.jura_semibold),
    Font(R.font.jura_bold),
)

val aldrichFont = FontFamily(
    Font(R.font.aldrich_regular)
)

val outlineFont = FontFamily(
    Font(R.font.londrinaoutline_regular)
)

val orbitronFont = FontFamily(
    Font(R.font.orbitron_regular),
    Font(R.font.orbitron_semibold),
    Font(R.font.orbitron_bold),
)

val sarpanchFont = FontFamily(
    Font(R.font.sarpanch_regular),
    Font(R.font.sarpanch_semibold),
    Font(R.font.sarpanch_bold),
)

val shareTechFont = FontFamily(
    Font(R.font.sharetechmono_regular)
)

val vtFont = FontFamily(
    Font(R.font.vt323_regular)
)

val poppinsFont = FontFamily(
    Font(R.font.poppins_regular),
    Font(R.font.poppins_semibold),
    Font(R.font.poppins_bold),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)