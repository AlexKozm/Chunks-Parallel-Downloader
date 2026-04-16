package org.example.unit

import kotlinx.coroutines.runBlocking
import org.example.ChunkSizeDefiner
import org.example.byMaxChunkSizeAndMaxParallel
import org.example.requester.InMemFileRequester
import org.example.storage.InMemChunksStorage
import org.junit.jupiter.api.Test
import kotlin.collections.map
import kotlin.test.assertEquals

class LoadByMaxChunkSizeAndMaxParallelTest {
    private val testData = "test0test1test2test3test4" // length = 25
    private val testDataByteArray = testData.toByteArray()

    internal suspend fun loadFile(chunkSize: Int, numOfParallelRequests: Int): List<ByteArray> {
        return org.example.loadFile(
            fileRequester = InMemFileRequester(testDataByteArray),
            chunkSizeProvider = ChunkSizeDefiner.byMaxChunkSizeAndMaxParallel(chunkSize, numOfParallelRequests),
            chunksStorageProvider = { _, _ -> InMemChunksStorage() },
            numOfParallelRequests = numOfParallelRequests
        )
    }

    @Test
    fun `bodySize rem chunkSize == 0, chunkSize times numOfParallelRequests lt bodySize`() = runBlocking {
        val arr = loadFile(chunkSize = 3, numOfParallelRequests = 2)
        val expected = testData.chunked(3)
        assertEquals(expected, arr.map(ByteArray::decodeToString))
    }

    @Test
    fun `bodySize rem chunkSize != 0, chunkSize times numOfParallelRequests lt bodySize`() = runBlocking {
        val arr = loadFile(chunkSize = 4, numOfParallelRequests = 2)
        val res = arr.joinToString("", transform = ByteArray::decodeToString)
        assertEquals(testData, res)
    }

    @Test
    fun `bodySize rem chunkSize != 0, chunkSize times numOfParallelRequests gt bodySize`() = runBlocking {
        val arr = loadFile(chunkSize = 8, numOfParallelRequests = 4)
        val expected = testData.chunked(7)  // length ceilDiv numOfParallelRequests = 25 ceilDiv 4 = 4
        assertEquals(expected, arr.map(ByteArray::decodeToString))
    }


    @Test
    fun `chunkSize gt bodySize`() = runBlocking {
        val arr = loadFile(chunkSize = 20, numOfParallelRequests = 4)
        val expected = testData.chunked(7) // length ceilDiv numOfParallelRequests = 25 ceilDiv 4 = 4
        assertEquals(expected, arr.map(ByteArray::decodeToString))
    }

    // TODO: with custom FileRequester check the max num of parallel requests
}
