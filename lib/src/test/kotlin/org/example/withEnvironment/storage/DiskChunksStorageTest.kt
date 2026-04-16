package org.example.withEnvironment.storage

import kotlinx.coroutines.runBlocking
import org.example.storage.ChunksStorageScope
import org.example.storage.DiskChunksStorage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.readBytes
import kotlin.test.assertEquals

class DiskChunksStorageTest(
    @TempDir
    private val tempDir: Path
) {
    private val outputPath = tempDir.resolve("test-file.txt")


    @Test
    fun `load and save with specified chunk size`() = runBlocking {
        ChunksStorageScope(DiskChunksStorage(outputPath)).use {
            saveChunk(5L..9, "test1".toByteArray())
            saveChunk(15L..19, "test3".toByteArray())
        }
        val bytes = outputPath.readBytes()
        assert(bytes.slice(0..4).all { it == 0.toByte() })
        assertEquals("test1", bytes.slice(5..9).toByteArray().decodeToString())
        assert(bytes.slice(10..14).all { it == 0.toByte() })
        assertEquals("test3", bytes.slice(15..19).toByteArray().decodeToString())
    }
}