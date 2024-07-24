package io.github.restyio.resty

class ClientCall<in I, out O>(
    private val client: Client<I, O>,
    private val host: () -> CharSequence,
    private val port: () -> Int,
    private val secure: () -> Boolean,
    private val request: RequestMessage<I>,
) : Call<O> {
    override suspend fun perform(): ResponseMessage<O> {
        this.client.open(
            this.host(),
            this.port(),
            this.secure(),
        ).use { portal ->
            return DirectCall(portal, this.request).perform()
        }
    }
}
