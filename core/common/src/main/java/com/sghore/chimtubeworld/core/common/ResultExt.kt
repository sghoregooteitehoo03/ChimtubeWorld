package com.sghore.chimtubeworld.core.common

import kotlin.coroutines.cancellation.CancellationException

suspend inline fun <T> suspendRunCatching(
    crossinline block: suspend () -> T
): Result<T> =
    try {
        Result.success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }
