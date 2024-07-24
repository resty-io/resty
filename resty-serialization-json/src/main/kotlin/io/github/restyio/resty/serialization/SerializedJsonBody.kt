package io.github.restyio.resty.serialization

import io.github.restyio.resty.RawBody
import io.github.restyio.resty.RawBodyEnvelope
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

class SerializedJsonBody<B>(content: B, strategy: SerializationStrategy<B>) :
    RawBody by RawBodyEnvelope(
        lazy { JsonBody(Json.encodeToJsonElement(strategy, content)) }::value,
    )
