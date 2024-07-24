package io.github.restyio.resty

import java.io.Closeable

interface Client<in I, out O> : Closeable {
    fun open(host: CharSequence, port: Int, secure: Boolean): Portal<I, O>
}

typealias HttpClient = Client<RawBody, RawBody>

class ClientWithRequestMiddleware<in A, B, out C>(
    private val origin: Client<B, C>,
    private val middleware: RequestMiddleware<A, B>,
) : Client<A, C> {
    override fun open(host: CharSequence, port: Int, secure: Boolean): Portal<A, C> {
        return PortalWithRequestMiddleware(
            this.origin.open(host, port, secure),
            this.middleware,
        )
    }

    override fun close() {
        return this.origin.close()
    }
}

class ClientWithResponseMiddleware<in A, B, out C>(
    private val origin: Client<A, B>,
    private val middleware: ResponseMiddleware<B, C>,
) : Client<A, C> {
    override fun open(host: CharSequence, port: Int, secure: Boolean): Portal<A, C> {
        return PortalWithResponseMiddleware(
            this.origin.open(host, port, secure),
            this.middleware,
        )
    }

    override fun close() {
        this.origin.close()
    }
}

fun <A, B, C> Client<B, C>.withRequestMiddleware(
    middleware: RequestMiddleware<A, B>,
): Client<A, C> {
    return ClientWithRequestMiddleware(this, middleware)
}

fun <A, B, C> Client<A, B>.withResponseMiddleware(
    middleware: ResponseMiddleware<B, C>,
): Client<A, C> {
    return ClientWithResponseMiddleware(this, middleware)
}
