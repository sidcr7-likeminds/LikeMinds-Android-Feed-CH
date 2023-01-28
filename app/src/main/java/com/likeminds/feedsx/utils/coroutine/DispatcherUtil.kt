package com.likeminds.feedsx.utils.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This dispatcher is optimized to perform disk or network I/O outside of the main thread.
 * Examples include using the database component, reading from or writing to files, and running any network operations.
 */
internal fun CoroutineScope.launchIO(block: suspend (CoroutineScope) -> Unit) =
    this.launch(Dispatchers.IO) {
        block(this)
    }

/**
 * This dispatcher is optimized to perform CPU-intensive work outside of the main thread.
 * Example use cases include sorting a list and parsing JSON.
 */
internal fun CoroutineScope.launchDefault(block: suspend (CoroutineScope) -> Unit) =
    this.launch(Dispatchers.Default) {
        block(this)
    }