package io.github.katarem.application.utils

import io.ktor.utils.io.core.toByteArray
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun isUuid(string: String): Boolean {
    return try {
        Uuid.fromByteArray(string.toByteArray())
        true
    } catch (ex: Exception) {
        false
    }
}