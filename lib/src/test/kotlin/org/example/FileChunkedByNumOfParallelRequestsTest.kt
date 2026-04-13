package org.example

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class FileChunkedByNumOfParallelRequestsTest {
    @Test
    fun `file size divides by the num of parallel requests without a remainder`() = runBlocking {
        val client = HttpClient(CIO)
        val arr = client.loadFile("http://localhost:8080/test-file-1.txt", 3)
        val expected = listOf("test0", "test1", "test2")
        assertContentEquals(expected, arr.map(ByteArray::decodeToString))
    }

    @Test
    fun `file size is less than num of parallel requests`() = runBlocking {
        val client = HttpClient(CIO)
        val arr = client.loadFile("http://localhost:8080/test-file-1.txt", 20)
        val expected = "test0test1test2".map { it.toString() }
        assertContentEquals(expected, arr.map(ByteArray::decodeToString))
    }

    @Test
    fun `file size divides by the num of parallel requests with a remainder`() = runBlocking {
        val client = HttpClient(CIO)
        val arr = client.loadFile("http://localhost:8080/test-file-1.txt", 4)
        val expected = listOf("test", "0tes", "t1te", "st2")
        assertContentEquals(expected, arr.map(ByteArray::decodeToString))
    }

    @Test
    fun `one request`() = runBlocking {
        val client = HttpClient(CIO)
        val arr = client.loadFile("http://localhost:8080/test-file-1.txt", 1)
        val expected = listOf("test0test1test2")
        assertContentEquals(expected, arr.map(ByteArray::decodeToString))
    }
}