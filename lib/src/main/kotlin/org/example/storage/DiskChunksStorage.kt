package org.example.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*


internal class DiskChunksStorage(
    val path: Path,
) : ChunksStorage<Path, LongRange> {
    override suspend fun saveChunk(id: LongRange, chunk: ByteArray) {
        withContext(Dispatchers.IO) {
            FileChannel.open(path, CREATE, WRITE).use { channel ->
                channel.write(ByteBuffer.wrap(chunk), id.first)
            }
        }
    }

    override suspend fun mergeChunks(): Path {
        return path
    }
}