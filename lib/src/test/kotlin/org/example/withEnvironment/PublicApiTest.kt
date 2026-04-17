package org.example.withEnvironment

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.runBlocking
import org.example.loadFile
import org.junit.jupiter.api.AutoClose
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.pathString
import kotlin.io.path.readLines
import kotlin.test.assertEquals

class PublicApiTest(
    @TempDir
    private val tempDir: Path
) {
    private val outputPath = tempDir.resolve("test-file.txt")

    @AutoClose
    val client = HttpClient(CIO)

    @BeforeEach
    fun deleteFile() {
        outputPath.deleteIfExists()
    }

    @Test
    fun `load and save with specified chunk size`() = runBlocking {
        client.loadFile(
            url = "http://localhost:8080/input/test-file.txt",
            path = outputPath.pathString,
            maxChunkSize = 5,
            numOfParallelRequests = 2
        )
        val res = outputPath.readLines().joinToString("")
        val expected = "test0test1test2"
        assertEquals(expected, res)
    }

    @Test
    fun `load and save without specified chunk size`() = runBlocking {
        client.loadFile(
            url = "http://localhost:8080/input/test-file.txt",
            path = outputPath.pathString,
            numOfParallelRequests = 2
        )
        val res = outputPath.readLines().joinToString("")
        val expected = "test0test1test2"
        assertEquals(expected, res)
    }

    @Test
    fun `load and save empty file`() = runBlocking {
        client.loadFile(
            url = "http://localhost:8080/input/test-file-empty.txt",
            path = outputPath.pathString,
            maxChunkSize = 2,
            numOfParallelRequests = 2
        )
        val res = outputPath.readLines().joinToString("")
        val expected = ""
        assertEquals(expected, res)
    }
}