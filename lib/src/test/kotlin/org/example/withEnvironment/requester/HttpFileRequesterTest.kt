package org.example.withEnvironment.requester

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.example.requester.HttpFileRequester
import org.junit.jupiter.api.AutoClose
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class HttpFileRequesterTest {
    @AutoClose
    val client = HttpClient(CIO)

    @Nested
    inner class TestFile {
        private val requester = HttpFileRequester(client, "http://localhost:8080/input/test-file.txt")
        @Test
        fun `body size returns expected value`() = runBlocking {
            val size = requester.getBodySize()
            assertEquals(15L, size)
        }

        @Test
        fun `range of size 0 returns all byte`() = runBlocking {
            val arr = requester.getChunk(6L..<6L)
            assertEquals(0, arr.size)
        }

        @Test
        fun `range of size 1 returns 1 byte`() = runBlocking {
            val arr = requester.getChunk(6L..6L)
            assertEquals(1, arr.size)
        }

        @Test
        fun `5 rangeTo 9 returns test1`() = runBlocking {
            val arr = requester.getChunk(5L..9L)
            assertEquals("test1", arr.decodeToString())
        }
    }

    @Nested
    inner class TestFileEmpty {
        private val requester = HttpFileRequester(client, "http://localhost:8080/input/test-file-empty.txt")

        @Test
        fun `empty file size is 0`() = runBlocking {
            val size = requester.getBodySize()
            assertEquals(0, size)
        }
    }

    @Nested
    inner class NotExistedFile {
        private val requester = HttpFileRequester(client, "http://localhost:8080/input/test-file-unexisted.txt")

        @Test
        fun `throw on not found`() = runBlocking {
            val err = assertThrows<ResponseException> { requester.getBodySize() }
            assertEquals(HttpStatusCode.NotFound, err.response.status)
        }
    }
}