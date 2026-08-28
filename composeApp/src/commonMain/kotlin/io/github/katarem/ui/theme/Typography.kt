package io.github.katarem.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import mangacatskmp.composeapp.generated.resources.Lato_Black
import mangacatskmp.composeapp.generated.resources.Lato_BlackItalic
import mangacatskmp.composeapp.generated.resources.Lato_BoldItalic
import mangacatskmp.composeapp.generated.resources.Lato_Light
import mangacatskmp.composeapp.generated.resources.Lato_LightItalic
import mangacatskmp.composeapp.generated.resources.Lato_Regular
import mangacatskmp.composeapp.generated.resources.Res
import mangacatskmp.composeapp.generated.resources.Roboto_Italic_VariableFont_wdth
import mangacatskmp.composeapp.generated.resources.Roboto_VariableFont_wdth
import org.jetbrains.compose.resources.Font

@Composable
fun AppTypography(): Typography {

    val lato = FontFamily(
            Font(Res.font.Lato_Black, weight = FontWeight.Black),
            Font(Res.font.Lato_Black, FontWeight.Bold),
            Font(Res.font.Lato_Regular, FontWeight.Normal),
            Font(Res.font.Lato_Light, FontWeight.Light),
            Font(Res.font.Lato_BlackItalic, FontWeight.Black, FontStyle.Italic),
            Font(Res.font.Lato_BoldItalic, FontWeight.Bold, FontStyle.Italic),
            Font(Res.font.Lato_LightItalic, FontWeight.Light, FontStyle.Italic),
        )
    val roboto = FontFamily(
        Font(Res.font.Roboto_VariableFont_wdth, FontWeight.Normal),
        Font(Res.font.Roboto_VariableFont_wdth, FontWeight.Bold),
        Font(Res.font.Roboto_VariableFont_wdth, FontWeight.Black),
        Font(Res.font.Roboto_Italic_VariableFont_wdth, FontWeight.Normal, FontStyle.Italic),
    )

    return Typography(
        bodyMedium = TextStyle(
            fontFamily = lato,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        ),
        displayMedium = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        ),
        displayLarge = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
        )
    )
}