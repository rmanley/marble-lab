package tech.rkanelabs.marblelab.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class CoroutineDispatcherProvider(
    val io: CoroutineDispatcher = Dispatchers.IO
)