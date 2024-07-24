package io.github.restyio.resty

/**
 * @param B Type of the response body.
 *
 * @since 0.1
 */
interface Call<out O> {
    /**
     * Perform this call and wait for the response.
     *
     * @return Response message.
     *
     * @since 0.1
     */
    suspend fun perform(): io.github.restyio.resty.ResponseMessage<O>
}
