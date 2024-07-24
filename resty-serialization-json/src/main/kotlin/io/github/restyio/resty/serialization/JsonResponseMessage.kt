package io.github.restyio.resty.serialization

import io.github.restyio.resty.RawBody
import io.github.restyio.resty.ResponseMessage
import io.github.restyio.resty.mapped.MappedResponseMessage
import io.github.restyio.resty.readAll
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class JsonResponseMessage(origin: ResponseMessage<RawBody>, bufferSize: Int = 1024) :
    ResponseMessage<JsonElement> by MappedResponseMessage(
        origin,
        { body ->
            Json.parseToJsonElement(
                body.readAll(bufferSize).toString(Charsets.UTF_8),
            )
        },
    )
