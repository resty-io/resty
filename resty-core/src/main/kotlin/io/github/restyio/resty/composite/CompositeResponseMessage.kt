package io.github.restyio.resty.composite

import io.github.restyio.resty.Header
import io.github.restyio.resty.ResponseMessage

class CompositeResponseMessage<out B>(
    private val status: () -> Int,
    private val message: () -> CharSequence,
    private val header: () -> Header,
    private val body: suspend () -> B,
) : ResponseMessage<B> {
    override fun status(): Int {
        return this.status.invoke()
    }

    override fun message(): CharSequence {
        return this.message.invoke()
    }

    override fun header(): Header {
        return this.header.invoke()
    }

    override suspend fun body(): B {
        return this.body.invoke()
    }
}
