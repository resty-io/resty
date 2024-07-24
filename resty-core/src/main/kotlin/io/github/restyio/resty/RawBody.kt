package io.github.restyio.resty

import java.io.Closeable
import java.io.EOFException
import kotlin.math.max
import kotlin.math.min

/**
 * @since 0.1
 */
interface RawBody : Closeable {
    /**
     * @since 0.1
     */
    suspend fun read(destination: ByteArray): Int
}

/**
 * @since 0.1
 */
object EmptyRawBody : RawBody {
    override suspend fun read(destination: ByteArray): Int {
        return -1
    }

    override fun close() {
    }
}

class RawBodyEnvelope(private val content: () -> RawBody) : RawBody {
    override suspend fun read(destination: ByteArray): Int {
        return this.content().read(destination)
    }

    override fun close() {
        this.content().close()
    }
}

class RawByteArrayBody(
    private val content: ByteArray,
    offset: Int = 0,
    length: Int = content.size,
) : RawBody {
    private var offset: Int = max(offset, 0)
    private val length: Int = min(max(length, 0), this.content.size)

    override suspend fun read(destination: ByteArray): Int {
        if (this.offset == -1) {
            throw EOFException("Body is closed")
        }
        val len = min(destination.size, this.length - this.offset)
        if (len == 0) {
            return -1
        }
        this.content.copyInto(
            destination,
            startIndex = this.offset,
            endIndex = this.offset + len,
        )
        this.offset += len
        return len
    }

    override fun close() {
        this.offset = -1
    }
}

fun ByteArray.toRawBody(offset: Int = 0, length: Int = this.size): RawBody {
    return RawByteArrayBody(this, offset, length)
}

suspend fun RawBody.readAll(bufferSize: Int = 1024): ByteArray {
    val buf = ByteArray(bufferSize)
    var acc = ByteArray(0)
    var n: Int
    do {
        n = this.read(buf)
        acc += buf.sliceArray(0 until n)
        if (n < buf.size) {
            break
        }
    } while (true)
    this.close()
    return acc
}
