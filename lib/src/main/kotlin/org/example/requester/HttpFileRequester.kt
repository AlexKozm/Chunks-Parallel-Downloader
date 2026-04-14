package org.example.requester

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsBytes
import kotlin.text.toInt

internal class HttpFileRequester(
    private val client: HttpClient,
    private val url: String
) : FileRequester<IntRange> {
    override suspend fun getBodySize() =
        client.head(url).headers["Content-Length"]?.toInt() ?: TODO("Throw something meaningful")

    // TODO: consider using Long for range for big files
    override suspend fun getChunk(id: IntRange): ByteArray {
        val startOffset = id.first
        val endOffset = id.last
        val response = client.get(url) {
            header("Range", "bytes=$startOffset-$endOffset")
        }
        return response.bodyAsBytes()
    }
}