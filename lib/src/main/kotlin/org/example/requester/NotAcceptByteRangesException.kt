package org.example.requester

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse

class NotAcceptByteRangesException(response: HttpResponse) : ResponseException(
    response,
    "Resource does not accept byte ranges"
)