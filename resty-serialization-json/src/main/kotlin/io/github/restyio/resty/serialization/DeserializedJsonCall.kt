package io.github.restyio.resty.serialization

import io.github.restyio.resty.Call
import io.github.restyio.resty.ResponseMessage
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonElement

class DeserializedJsonCall<out B>(
    private val origin: Call<JsonElement>,
    private val strategy: DeserializationStrategy<B>,
) : Call<B> {
    override suspend fun perform(): ResponseMessage<B> {
        return DeserializedJsonResponseMessage(
            this.origin.perform(),
            this.strategy,
        )
    }
}
