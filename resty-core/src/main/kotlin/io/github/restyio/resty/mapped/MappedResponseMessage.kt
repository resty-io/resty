package io.github.restyio.resty.mapped

import io.github.restyio.resty.Header
import io.github.restyio.resty.ResponseMessage

class MappedResponseMessage<in I, out O>(
    private val origin: ResponseMessage<I>,
    private val mapper: suspend (I) -> O,
) : ResponseMessage<O> {
    override fun status(): Number {
        return this.origin.status()
    }

    override fun message(): CharSequence {
        return this.origin.message()
    }

    override fun header(): Header {
        return this.origin.header()
    }

    override suspend fun body(): O {
        return this.mapper.invoke(this.origin.body())
    }
}
