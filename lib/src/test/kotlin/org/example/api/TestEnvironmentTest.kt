package org.example.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class TestEnvironmentTest {
    @Test
    fun `server is available and has a test file`() = runBlocking {
        val client = HttpClient(CIO)
        val response = client.get("http://localhost:8080/input/test-file.txt")
        assert(response.status == HttpStatusCode.OK)
    }

    @Test
    fun `head returns Content-Length header`() = runBlocking {
        val client = HttpClient(CIO)
        val response = client.head("http://localhost:8080/input/test-file.txt")
        assert(response.headers.contains("Content-Length"))
        println(response.headers["Content-Length"])
    }

    @Test
    fun `range header in request works`() = runBlocking {
        val client = HttpClient(CIO)
        val response = client.get("http://localhost:8080/input/test-file.txt") {
            header("Range", "bytes=1-2")
        }
        assert(response.status == HttpStatusCode.PartialContent)
        assert(response.bodyAsText() == "es")
    }
}
