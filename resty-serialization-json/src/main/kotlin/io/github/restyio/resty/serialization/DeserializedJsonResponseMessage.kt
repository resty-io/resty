package io.github.restyio.resty.serialization

import io.github.restyio.resty.ResponseMessage
import io.github.restyio.resty.mapped.MappedResponseMessage
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class DeserializedJsonResponseMessage<out B>(
    origin: ResponseMessage<JsonElement>,
    strategy: DeserializationStrategy<B>,
) : ResponseMessage<B> by MappedResponseMessage(
    origin,
    { body -> Json.decodeFromJsonElement(strategy, body) },
)
