package org.example.unit.storage

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.example.storage.UnsaveChunksStorage
import org.example.storage.toSaveStorage
import org.junit.jupiter.api.Test

class DiskChunksStorageTest {
    @Test
    fun `closes even on cancellation`() = runBlocking {
        val unsaveChunksStorage = object : UnsaveChunksStorage<Unit, LongRange> {
            var open = false
            override suspend fun open() { withContext(Dispatchers.IO) { open = true } }
            override suspend fun mergeChunks() { currentCoroutineContext().cancel() }
            override suspend fun close() { withContext(Dispatchers.IO) { open = false } }
            override suspend fun saveChunk(id: LongRange, chunk: ByteArray) {}
        }
        try {
            async {
                unsaveChunksStorage.toSaveStorage().use {
                    saveChunk(5L..9, "test1".toByteArray())
                    saveChunk(15L..19, "test3".toByteArray())
                }
            }.await()
        } catch (e: Exception) {
            assert(e is CancellationException)
            assert(!unsaveChunksStorage.open)
        }
    }
}