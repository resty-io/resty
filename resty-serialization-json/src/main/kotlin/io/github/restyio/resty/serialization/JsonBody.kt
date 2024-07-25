package io.github.restyio.resty.serialization

import io.github.restyio.resty.RawBody
import io.github.restyio.resty.RawBodyEnvelope
import io.github.restyio.resty.toRawBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class JsonBody(json: JsonElement) :
    RawBody by RawBodyEnvelope(
        lazy { Json.encodeToString(json).toByteArray().toRawBody() }::value,
    )
