package org.example

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LoadWithChunkSizeAndNumOfParallelRequestsTest {

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

private const val testData = "test0test1test2test3test4"
private val testDataByteArray = testData.toByteArray()

internal suspend fun loadFile(chunkSize: Int, numOfParallelRequests: Int): MutableList<ByteArray> {
    return loadFile(
        fileRequester = InMemFileRequester(testDataByteArray),
        chunkSizeProvider = { bodySize ->
            when {
                chunkSize * numOfParallelRequests > bodySize ->
                    bodySize / numOfParallelRequests + 1
                chunkSize * numOfParallelRequests == bodySize ->
                    bodySize / numOfParallelRequests
                else -> chunkSize
            }
        },
        chunksStorageProvider = { bodySize, chunkSize ->
            InMemChunksStorage(bodySize/chunkSize + if (bodySize % chunkSize > 0) 1 else 0)
        },
        numOfParallelRequests = numOfParallelRequests
    )
}
