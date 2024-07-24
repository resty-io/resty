package io.github.restyio.resty.mapped

import io.github.restyio.resty.Header
import io.github.restyio.resty.RequestMessage

class MappedRequestMessage<in I, out O>(
    private val origin: RequestMessage<I>,
    private val mapper: (I) -> O,
) : RequestMessage<O> {
    override fun method(): CharSequence {
        return this.origin.method()
    }

    override fun location(): CharSequence {
        return this.origin.location()
    }

    override fun header(): Header {
        return this.origin.header()
    }

    override suspend fun body(): O {
        return this.mapper.invoke(this.origin.body())
    }
}
