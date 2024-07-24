package io.github.restyio.resty

/**
 * HTTP request message.
 *
 * @since 0.1
 */
interface RequestMessage<out B> : Message<B> {
    /**
     * @return Method of this request message.
     *
     * @since 0.1
     */
    fun method(): CharSequence

    /**
     * @return Location of the requested file.
     *
     * @since 0.1
     */
    fun location(): CharSequence
}

class PreparedRequestMessage<out B>(
    private val header: () -> Header,
    private val method: () -> CharSequence,
    private val location: () -> CharSequence,
    private val body: suspend () -> B,
) : RequestMessage<B> {
    constructor(
        origin: RequestMessage<B>,
        header: () -> Header = origin::header,
        method: () -> CharSequence = origin::method,
        location: () -> CharSequence = origin::location,
        body: suspend () -> B = origin::body,
    ) : this(header, method, location, body)

    constructor(
        origin: Message<B>,
        method: () -> CharSequence,
        location: () -> CharSequence,
        header: () -> Header = origin::header,
        body: suspend () -> B = origin::body,
    ) : this(header, method, location, body)

    override fun header(): Header {
        return this.header.invoke()
    }

    override fun method(): CharSequence {
        return this.method.invoke()
    }

    override fun location(): CharSequence {
        return this.location.invoke()
    }

    override suspend fun body(): B {
        return this.body.invoke()
    }
}

fun <B> RequestMessage<B>.with(
    field: CharSequence,
    value: Iterable<CharSequence>,
): RequestMessage<B> {
    return PreparedRequestMessage(
        (this as Message<B>).with(field, value),
        method = this::method,
        location = this::location,
    )
}

fun <B> RequestMessage<B>.with(field: CharSequence, value: CharSequence): RequestMessage<B> {
    return this.with(field, listOf(value))
}

fun <B> RequestMessage<B>.without(field: CharSequence): RequestMessage<B> {
    return PreparedRequestMessage(
        (this as Message<B>).without(field),
        method = this::method,
        location = this::location,
    )
}

fun <B> RequestMessage<B>.replace(
    field: CharSequence,
    value: Iterable<CharSequence>,
): RequestMessage<B> {
    return this.without(field).with(field, value)
}

fun <B> RequestMessage<B>.replace(field: CharSequence, value: CharSequence): RequestMessage<B> {
    return this.replace(field, listOf(value))
}

fun <B> RequestMessage<B>.with(body: B): RequestMessage<B> {
    return PreparedRequestMessage(this, body = { body })
}

fun <B> RequestMessage<B>.at(location: CharSequence): RequestMessage<B> {
    return PreparedRequestMessage(this, location = { location })
}

fun <B> RequestMessage<B>.method(method: CharSequence): RequestMessage<B> {
    return PreparedRequestMessage(this, method = { method })
}
