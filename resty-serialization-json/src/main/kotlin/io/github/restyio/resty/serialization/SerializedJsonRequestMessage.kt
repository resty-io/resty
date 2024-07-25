package io.github.restyio.resty.serialization

import io.github.restyio.resty.RequestMessage
import io.github.restyio.resty.mapped.MappedRequestMessage
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class SerializedJsonRequestMessage<out B>(
    origin: RequestMessage<B>,
    strategy: SerializationStrategy<B>,
) : RequestMessage<JsonElement> by MappedRequestMessage(
        origin,
        { body -> Json.encodeToJsonElement(strategy, body) },
    )
