package io.stene.http2errorexample.client

import feign.Headers
import feign.Param
import feign.RequestLine
import io.stene.http2errorexample.client.type.PlayerProfileRequest

interface PlayerProfileApi {
    @RequestLine("PUT /users/{buypassId}")
    @Headers("Content-Type: application/scim+json", "Accept: application/scim+json")
    fun upload(@Param("buypassId") buypassId: String, playerProfileRequest: PlayerProfileRequest)
}
