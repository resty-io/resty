package io.github.restyio.resty

import java.io.Closeable

/**
 * Portal.
 *
 * @since 0.1
 */
interface Portal<in I, out O> : Closeable {
    suspend fun translate(request: RequestMessage<I>): ResponseMessage<O>
}

fun interface Middleware<in A, out B> where A : Message<*>, B : Message<*> {
    suspend fun apply(input: A): B
}

fun interface RequestMiddleware<in A, out B> : Middleware<RequestMessage<A>, RequestMessage<B>>

fun interface ResponseMiddleware<in A, out B> : Middleware<ResponseMessage<A>, ResponseMessage<B>>

class MiddlewarePipeline<in A, B, out C>(
    private val base: Middleware<A, B>,
    private val layer: Middleware<B, C>,
) : Middleware<A, C> where A : Message<*>, B : Message<*>, C : Message<*> {
    override suspend fun apply(input: A): C {
        return this.layer.apply(this.base.apply(input))
    }
}

class PortalWithRequestMiddleware<in A, B, out C>(
    private val origin: Portal<B, C>,
    private val pipeline: RequestMiddleware<A, B>,
) : Portal<A, C> {
    override suspend fun translate(request: RequestMessage<A>): ResponseMessage<C> {
        return this.origin.translate(this.pipeline.apply(request))
    }

    override fun close() {
        this.origin.close()
    }
}

class PortalWithResponseMiddleware<in A, B, out C>(
    private val origin: Portal<A, B>,
    private val pipeline: ResponseMiddleware<B, C>,
) : Portal<A, C> {
    override suspend fun translate(request: RequestMessage<A>): ResponseMessage<C> {
        return this.pipeline.apply(this.origin.translate(request))
    }

    override fun close() {
        this.origin.close()
    }
}

class MiddlewareSetUserAgent<A>(private val value: CharSequence) : RequestMiddleware<A, A> {
    override suspend fun apply(input: RequestMessage<A>): RequestMessage<A> {
        return input.with(Field.UserAgent, this.value)
    }
}

fun <A, B, C> Portal<B, C>.withRequestMiddleware(pipeline: RequestMiddleware<A, B>): Portal<A, C> {
    return PortalWithRequestMiddleware(this, pipeline)
}

fun <A, B, C> Portal<A, B>.withResponseMiddleware(
    pipeline: ResponseMiddleware<B, C>,
): Portal<A, C> {
    return PortalWithResponseMiddleware(this, pipeline)
}

fun <A, B, C> RequestMiddleware<A, B>.then(
    next: RequestMiddleware<B, C>,
): RequestMiddleware<A, C> {
    return MiddlewarePipeline(this, next).toRequestMiddleware()
}

fun <A, B, C> ResponseMiddleware<A, B>.then(
    next: ResponseMiddleware<B, C>,
): ResponseMiddleware<A, C> {
    return MiddlewarePipeline(this, next).toResponseMiddleware()
}

fun <A, B> Middleware<
    RequestMessage<A>,
    RequestMessage<B>,
    >.toRequestMiddleware(): RequestMiddleware<A, B> {
    return RequestMiddleware { this.apply(it) }
}

fun <A, B> Middleware<
    ResponseMessage<A>,
    ResponseMessage<B>,
    >.toResponseMiddleware(): ResponseMiddleware<A, B> {
    return ResponseMiddleware { this.apply(it) }
}
