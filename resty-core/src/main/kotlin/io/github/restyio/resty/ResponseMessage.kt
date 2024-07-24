package io.github.restyio.resty

/**
 * HTTP message sent (received) as a response
 * to a request message.
 *
 * @since 0.1
 */
interface ResponseMessage<out B> : Message<B> {
    /**
     * @return Status code of this response.
     *
     * @since 0.1
     */
    fun status(): Number

    /**
     * @return Human-readable status message
     * of this response.
     *
     * @since 0.1
     */
    fun message(): CharSequence
}

class PreparedResponseMessage<out B>(
    private val status: () -> Number,
    private val message: () -> CharSequence,
    private val header: () -> Header,
    private val body: suspend () -> B,
) : ResponseMessage<B> {
    constructor(
        origin: Message<B>,
        header: () -> Header = origin::header,
        body: suspend () -> B = origin::body,
        status: () -> Number,
        message: () -> CharSequence,
    ) : this(status, message, header, body)

    constructor(
        origin: ResponseMessage<B>,
        header: () -> Header = origin::header,
        body: suspend () -> B = origin::body,
        status: () -> Number = origin::status,
        message: () -> CharSequence = origin::message,
    ) : this(status, message, header, body)

    override fun status(): Number {
        return this.status.invoke()
    }

    override fun message(): CharSequence {
        return this.message.invoke()
    }

    override fun header(): Header {
        return this.header.invoke()
    }

    override suspend fun body(): B {
        return this.body.invoke()
    }
}
