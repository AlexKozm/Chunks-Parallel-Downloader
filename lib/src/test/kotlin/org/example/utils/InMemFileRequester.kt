package org.example.utils

import org.example.requester.FileRequester

internal class InMemFileRequester(
    private val testData: ByteArray
) : FileRequester<LongRange> {
    override suspend fun getBodySize(): Long {
        return testData.size.toLong()
    }

    override suspend fun getChunk(id: LongRange): ByteArray {
        return testData.slice(id.first.toInt()..id.last.toInt()).toByteArray()
    }
}