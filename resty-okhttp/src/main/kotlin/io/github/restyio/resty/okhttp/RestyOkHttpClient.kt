package io.github.restyio.resty.okhttp

import io.github.restyio.resty.Header
import io.github.restyio.resty.HttpClient
import io.github.restyio.resty.Method
import io.github.restyio.resty.Portal
import io.github.restyio.resty.RawBody
import io.github.restyio.resty.RequestMessage
import io.github.restyio.resty.ResponseMessage
import io.github.restyio.resty.extensions.buildString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okio.BufferedSink
import java.io.IOException
import java.net.URL
import kotlin.coroutines.resumeWithException

class RestyOkHttpClient(
    private val client: okhttp3.OkHttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : HttpClient {
    private val portals = HashSet<Portal<RawBody, RawBody>>()

    override fun open(host: CharSequence, port: Int, secure: Boolean): Portal<RawBody, RawBody> {
        val portal = this.OkHttpClientPortal(host, port, secure)
        if (!this.portals.add(portal)) {
            throw Error("hash collision occurred")
        }
        return portal
    }

    override fun close() {
        for (portal in this.portals) {
            portal.close()
        }
    }

    inner class OkHttpClientPortal(
        private val host: CharSequence,
        private val port: Int,
        private val secure: Boolean,
    ) : Portal<RawBody, RawBody> {
        private val calls = HashSet<okhttp3.Call>()

        override suspend fun translate(
            request: RequestMessage<RawBody>,
        ): ResponseMessage<RawBody> {
            val call =
                this@RestyOkHttpClient.client.newCall(
                    request.buildRequest(
                        this.host,
                        this.port,
                        this.secure,
                    ),
                )
            this.calls.add(call)
            val response = call.await()
            this.calls.remove(call)
            return OkHttpResponseMessage(
                response,
                this@RestyOkHttpClient.dispatcher,
            )
        }

        override fun close() {
            for (call in this.calls) {
                call.cancel()
            }
            this.calls.clear()
            this@RestyOkHttpClient.portals.remove(this)
        }
    }
}

private class OkHttpResponseMessage(
    private val response: okhttp3.Response,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ResponseMessage<RawBody> {
    override fun status(): Number {
        return this.response.code
    }

    override fun message(): CharSequence {
        return this.response.message
    }

    override fun header(): Header {
        return OkHttpResponseHeader(this.response)
    }

    override suspend fun body(): RawBody {
        return OkHttpResponseBody(
            with(this.response.body) {
                if (this == null) {
                    throw IllegalStateException(
                        "ResponseBody is not present in the response",
                    )
                }
                this
            },
            this.dispatcher,
        )
    }
}

private class OkHttpResponseBody(
    private val body: okhttp3.ResponseBody,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RawBody {
    override suspend fun read(destination: ByteArray): Int {
        val source = this.body.source()
        val read: Int
        withContext(this.dispatcher) {
            read = source.read(destination)
        }
        return read
    }

    override fun close() {
        this.body.close()
    }
}

private class OkHttpResponseHeader(private val response: okhttp3.Response) : Header {
    override fun field(name: CharSequence): Iterator<CharSequence> {
        return this.response.headers(name.buildString()).iterator()
    }

    override fun iterator(): Iterator<CharSequence> {
        return this.response.headers
            .names()
            .iterator()
    }
}

private class OkHttpRequestBody(private val body: RawBody) : okhttp3.RequestBody() {
    private val buffer = ByteArray(1024)

    override fun contentType(): okhttp3.MediaType? {
        return null
    }

    override fun writeTo(sink: BufferedSink) {
        val n: Int
        runBlocking {
            n = this@OkHttpRequestBody.body.read(this@OkHttpRequestBody.buffer)
        }
        if (n != -1) {
            sink.write(this.buffer.sliceArray(0 until n))
            sink.flush()
        }
        if (n < this.buffer.size) {
            this.body.close()
        }
    }
}

private suspend inline fun okhttp3.Call.await(): okhttp3.Response {
    return suspendCancellableCoroutine { continuation ->
        this@await.enqueue(
            object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    continuation.resume(response) { _, resource, _ ->
                        resource.close()
                    }
                }
            },
        )
        continuation.invokeOnCancellation {
            this@await.cancel()
        }
    }
}

private suspend fun RequestMessage<RawBody>.buildRequest(
    host: CharSequence,
    port: Int,
    secure: Boolean,
): okhttp3.Request {
    return okhttp3.Request
        .Builder()
        .url(
            URL(
                if (secure) "https" else "http",
                host.buildString(),
                port,
                this.location().buildString(),
            ),
        ).apply {
            this@buildRequest
                .method()
                .let { method ->
                    this.method(
                        method.buildString(),
                        when {
                            Method.Get == method || Method.Head == method -> {
                                this@buildRequest.body().close()
                                null
                            }
                            else -> OkHttpRequestBody(this@buildRequest.body())
                        },
                    )
                }
        }.apply {
            for (field in this@buildRequest.header()) {
                for (value in this@buildRequest.header().field(field)) {
                    addHeader(field.buildString(), value.buildString())
                }
            }
        }.build()
}
