package org.example.requester

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

internal class HttpFileRequester(
    private val client: HttpClient,
    private val url: String
) : FileRequester<LongRange> {
    override suspend fun getBodySize(): Long {
        val response = client.head(url)
        if (!response.status.isSuccess()) throw ResponseException(response, "Response is not 2xx")
        val contentLength = response.headers[HttpHeaders.ContentLength]?.toLong() ?: return 0
        if (response.headers[HttpHeaders.AcceptRanges] != "bytes") throw NotAcceptByteRangesException(response)
        return contentLength
    }

    override suspend fun getChunk(id: LongRange): ByteArray {
        if (id.isEmpty()) return byteArrayOf()
        val startOffset = id.first
        val endOffset = id.last
        val response = client.get(url) {
            header(HttpHeaders.Range, "bytes=$startOffset-$endOffset")
        }
        if (response.status != HttpStatusCode.PartialContent)
            throw ResponseException(
                response,
                "Expected ${HttpStatusCode.PartialContent} but received ${response.status}"
            )
        return response.bodyAsBytes()
    }
}