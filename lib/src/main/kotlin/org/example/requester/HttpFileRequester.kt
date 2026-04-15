package org.example.requester

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsBytes

internal class HttpFileRequester(
    private val client: HttpClient,
    private val url: String
) : FileRequester<LongRange> {
    override suspend fun getBodySize() =
        client.head(url).headers["Content-Length"]?.toLong() ?: TODO("Throw something meaningful")

    override suspend fun getChunk(id: LongRange): ByteArray {
        val startOffset = id.first
        val endOffset = id.last
        val response = client.get(url) {
            header("Range", "bytes=$startOffset-$endOffset")
        }
        return response.bodyAsBytes()
    }
}