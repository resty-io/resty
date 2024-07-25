package io.github.restyio.resty

import io.github.restyio.resty.composite.CompositeRequestMessage
import java.net.URL

class UrlCall(client: HttpClient, url: URL, request: RequestMessage<RawBody>) :
    Call<RawBody> by ClientCall(
        client = client,
        host = url::getHost,
        port = { url.port },
        secure = { url.protocol == "https" },
        request =
            CompositeRequestMessage(
                method = request::method,
                header = request::header,
                body = request::body,
                location = {
                    (url.path.plus(url.query ?: "")).ifEmpty {
                        request.location()
                    }
                },
            ),
    ) {
    constructor(
        client: HttpClient,
        url: URL,
        method: () -> CharSequence,
        header: () -> Header,
        body: suspend () -> RawBody = { EmptyRawBody },
    ) : this(
        client = client,
        url = url,
        request =
            CompositeRequestMessage(
                method = method,
                header = header,
                body = body,
                location = { "" },
            ),
    )

    constructor(
        client: HttpClient,
        url: URL,
        method: CharSequence,
        header: Header,
        body: RawBody = EmptyRawBody,
    ) : this(
        client = client,
        url = url,
        method = { method },
        header = { header },
        body = { body },
    )
}
