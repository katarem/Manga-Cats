package io.github.katarem

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform