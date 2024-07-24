package io.github.restyio.resty.mapped

import io.github.restyio.resty.Call
import io.github.restyio.resty.ResponseMessage

/**
 * @since 0.1
 */
class MappedCall<I, O>(
    private val origin: Call<I>,
    private val mapper: suspend (I) -> O,
) : Call<O> {
    override suspend fun perform(): ResponseMessage<O> {
        return MappedResponseMessage(
            this.origin.perform(),
            this.mapper,
        )
    }
}
