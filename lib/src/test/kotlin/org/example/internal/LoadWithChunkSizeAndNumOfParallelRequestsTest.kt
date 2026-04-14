package org.example.internal

import kotlinx.coroutines.runBlocking
import org.example.requester.InMemFileRequester
import org.example.storage.InMemChunksStorage
import org.example.utils.ceilDiv
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LoadWithChunkSizeAndNumOfParallelRequestsTest {
    private val testData = "test0test1test2test3test4"
    private val testDataByteArray = testData.toByteArray()

    internal suspend fun loadFile(chunkSize: Int, numOfParallelRequests: Int): MutableList<ByteArray> {
        return org.example.loadFile(
            fileRequester = InMemFileRequester(testDataByteArray),
            chunkSizeProvider = { bodySize ->
                when {
                    chunkSize * numOfParallelRequests > bodySize -> bodySize / numOfParallelRequests + 1
                    chunkSize * numOfParallelRequests == bodySize -> bodySize / numOfParallelRequests
                    else -> chunkSize
                }
            },
            chunksStorageProvider = { bodySize, chunkSize ->
                InMemChunksStorage(bodySize ceilDiv chunkSize)
            },
            numOfParallelRequests = numOfParallelRequests
        )
    }

    @Test
    fun `bodySize rem chunkSize == 0, chunkSize times numOfParallelRequests lt bodySize`() = runBlocking {
        val arr = loadFile(chunkSize = 3, numOfParallelRequests = 2)
        val res = arr.joinToString("", transform = ByteArray::decodeToString)
        assertEquals(testData, res)
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
        val res = arr.joinToString("", transform = ByteArray::decodeToString)
        assertEquals(testData, res)
    }


    @Test
    fun `chunkSize gt bodySize`() = runBlocking {
        val arr = loadFile(chunkSize = 20, numOfParallelRequests = 4)
        val res = arr.joinToString("", transform = ByteArray::decodeToString)
        assertEquals(testData, res)
    }

    // TODO: with custom FileRequester check the max num of parallel requests
}
