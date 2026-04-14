package org.example.requester

internal class InMemFileRequester(
    private val testData: ByteArray
) : FileRequester<IntRange> {
    override suspend fun getBodySize(): Int {
        return testData.size
    }

    override suspend fun getChunk(id: IntRange): ByteArray {
        return testData.slice(id).toByteArray()
    }
}