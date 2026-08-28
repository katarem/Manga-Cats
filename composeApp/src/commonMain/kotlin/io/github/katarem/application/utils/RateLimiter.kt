package io.github.katarem.application.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RateLimiter(
    private val maxRequests: Int,
    coroutineScope: CoroutineScope
) {
    private val tokens = Channel<Unit>(maxRequests)
    init {
        coroutineScope.launch {
            while (isActive) {
                repeat(maxRequests) {
                    tokens.trySend(Unit)
                }
            }
        }
    }

    suspend fun acquire(){
        println("me ejecuto xd")
        tokens.receive()
    }

}