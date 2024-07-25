package io.github.restyio.resty.serialization

import io.github.restyio.resty.Call
import io.github.restyio.resty.RawBody
import io.github.restyio.resty.ResponseMessage
import kotlinx.serialization.json.JsonElement

class JsonCall(private val origin: Call<RawBody>) : Call<JsonElement> {
    override suspend fun perform(): ResponseMessage<JsonElement> {
        return JsonResponseMessage(this.origin.perform())
    }
}
