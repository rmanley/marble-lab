package tech.rkanelabs.marblelab.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import tech.rkanelabs.marblelab.R

val MedievalFontFamily = FontFamily(
    Font(R.font.metamorphous_regular, FontWeight.Normal)
)

private val defaultTypography = Typography()

val Typography = Typography(
    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = MedievalFontFamily
    ),
    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = MedievalFontFamily
    ),
    labelLarge = defaultTypography.labelLarge.copy(
        fontFamily = MedievalFontFamily
    ),
)