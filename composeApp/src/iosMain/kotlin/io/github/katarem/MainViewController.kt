package io.github.katarem

import androidx.compose.ui.window.ComposeUIViewController
import io.github.katarem.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) { App() }