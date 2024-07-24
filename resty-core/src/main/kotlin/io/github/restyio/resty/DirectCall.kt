package io.github.restyio.resty

class DirectCall<in I, out O>(
    private val portal: Portal<I, O>,
    private val request: RequestMessage<I>,
) : Call<O> {
    override suspend fun perform(): ResponseMessage<O> {
        return this.portal.translate(this.request)
    }
}
