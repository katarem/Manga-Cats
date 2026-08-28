package io.github.katarem

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.katarem.di.initKoin
import mangacatskmp.composeapp.generated.resources.Res
import mangacatskmp.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MangaCatsKMP",
            icon = painterResource(Res.drawable.logo)
        ) {
            App()
        }
    }
}