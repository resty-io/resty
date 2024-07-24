package io.github.restyio.resty

/**
 * HTTP message.
 *
 * @param B Type of the message body.
 *
 * @since 0.1
 */
interface Message<out B> {
    /**
     * @return Header of this message.
     *
     * @since 0.1
     */
    fun header(): Header

    /**
     * @return Body of this message.
     *
     * @since 0.1
     */
    suspend fun body(): B
}

class PreparedMessage<out B>(
    private val header: () -> Header,
    private val body: suspend () -> B,
) : Message<B> {
    constructor(
        origin: Message<B>,
        header: () -> Header = origin::header,
        body: suspend () -> B = origin::body,
    ) : this(header, body)

    override fun header(): Header {
        return this.header.invoke()
    }

    override suspend fun body(): B {
        return this.body.invoke()
    }
}

fun <B> Message<B>.with(field: CharSequence, value: Iterable<CharSequence>): Message<B> {
    return PreparedMessage(
        this,
        header = { this.header().with(field, value) },
    )
}

fun <B> Message<B>.with(field: CharSequence, value: CharSequence): Message<B> {
    return this.with(field, listOf(value))
}

fun <B> Message<B>.without(field: CharSequence): Message<B> {
    return PreparedMessage(
        this,
        header = { this.header().without(field) },
    )
}

fun <B> Message<B>.replace(field: CharSequence, value: Iterable<CharSequence>): Message<B> {
    return this.without(field).with(field, value)
}

fun <B> Message<B>.replace(field: CharSequence, value: CharSequence): Message<B> {
    return this.replace(field, listOf(value))
}

fun <B> Message<B>.with(body: B): Message<B> {
    return PreparedMessage(
        this,
        body = { body },
    )
}
