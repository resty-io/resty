package io.github.restyio.resty.composite

import io.github.restyio.resty.Header
import io.github.restyio.resty.RequestMessage

class CompositeRequestMessage<out B>(
    private val method: () -> CharSequence,
    private val location: () -> CharSequence,
    private val header: () -> Header,
    private val body: suspend () -> B,
) : RequestMessage<B> {
    override fun method(): CharSequence {
        return this.method.invoke()
    }

    override fun location(): CharSequence {
        return this.location.invoke()
    }

    override fun header(): Header {
        return this.header.invoke()
    }

    override suspend fun body(): B {
        return this.body.invoke()
    }
}
