package org.example.internal

import kotlinx.coroutines.runBlocking
import org.example.ChunkSizeDefiner
import org.example.forLoadInOneIteration
import org.example.requester.InMemFileRequester
import org.example.storage.InMemChunksStorage
import org.example.utils.ceilDiv
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class FileChunkedByNumOfParallelRequestsTest {
    private val testData = "test0test1test2"
    private val testDataByteArray = testData.toByteArray()

    private suspend fun loadFile(numOfParallelRequests: Int): List<ByteArray> {
        return org.example.loadFile(
            fileRequester = InMemFileRequester(testDataByteArray),
            chunkSizeProvider = ChunkSizeDefiner.forLoadInOneIteration(numOfParallelRequests),
            chunksStorageProvider = { _, _ -> InMemChunksStorage() },
            numOfParallelRequests = numOfParallelRequests
        )
    }

    @Test
    fun `file size divides by the num of parallel requests without a remainder`() = runBlocking {
        val arr = loadFile(3)
        val expected = listOf("test0", "test1", "test2")
        assertContentEquals(expected, arr.map(ByteArray::decodeToString))
    }

    @Test
    fun `file size is less than num of parallel requests`() = runBlocking {
        val arr = loadFile(20)
        val expected = "test0test1test2".map { it.toString() }
        assertContentEquals(expected, arr.map(ByteArray::decodeToString))
    }

    @Test
    fun `file size divides by the num of parallel requests with a remainder`() = runBlocking {
        val arr = loadFile(4)
        val expected = listOf("test", "0tes", "t1te", "st2")
        assertContentEquals(expected, arr.map(ByteArray::decodeToString))
    }

    @Test
    fun `one request`() = runBlocking {
        val arr = loadFile(1)
        val expected = listOf("test0test1test2")
        assertContentEquals(expected, arr.map(ByteArray::decodeToString))
    }
}