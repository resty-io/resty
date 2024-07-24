package io.github.restyio.resty.serialization

import io.github.restyio.resty.RawBody
import io.github.restyio.resty.RequestMessage
import io.github.restyio.resty.mapped.MappedRequestMessage
import kotlinx.serialization.json.JsonElement

class JsonRequestMessage(origin: RequestMessage<JsonElement>) :
    RequestMessage<RawBody> by MappedRequestMessage(origin, ::JsonBody)
