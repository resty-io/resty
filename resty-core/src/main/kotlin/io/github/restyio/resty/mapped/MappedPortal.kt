package io.github.restyio.resty.mapped

import io.github.restyio.resty.Portal
import io.github.restyio.resty.RequestMessage
import io.github.restyio.resty.ResponseMessage

class MappedPortal<II, IO, in OI, out OO>(
    private val origin: Portal<II, IO>,
    private val requestMapper: (OI) -> II,
    private val responseMapper: (IO) -> OO,
) : Portal<OI, OO> {
    override suspend fun translate(request: RequestMessage<OI>): ResponseMessage<OO> {
        return MappedResponseMessage(
            this.origin.translate(
                MappedRequestMessage(
                    request,
                    this.requestMapper,
                ),
            ),
            this.responseMapper,
        )
    }

    override fun close() {
        this.origin.close()
    }
}
