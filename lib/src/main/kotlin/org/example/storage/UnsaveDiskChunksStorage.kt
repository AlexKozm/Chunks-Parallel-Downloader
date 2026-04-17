package org.example.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*


internal class UnsaveDiskChunksStorage(
    val path: Path,
) : UnsaveChunksStorage<Path, LongRange> {

    lateinit var file: FileChannel

    override suspend fun open() {
        withContext(Dispatchers.IO) {
            file = FileChannel.open(path, CREATE, WRITE)
        }
    }

    override suspend fun saveChunk(id: LongRange, chunk: ByteArray) {
        withContext(Dispatchers.IO) {
            file.write(ByteBuffer.wrap(chunk), id.first)
        }
    }

    override suspend fun mergeChunks(): Path {
        return path
    }

    override suspend fun close() {
        withContext(Dispatchers.IO) {
            file.close()
        }
    }
}