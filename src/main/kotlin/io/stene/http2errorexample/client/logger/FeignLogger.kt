package io.stene.http2errorexample.client.logger

import feign.Request
import feign.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*

class FeignLogger(val logger: Logger, val clientName: String) : feign.Logger() {
    constructor(clazz: Class<*>, clientName: String) : this(LoggerFactory.getLogger(clazz), clientName)
    override fun logRequest(configKey: String?, logLevel: Level?, request: Request?) {
        if (logger.isDebugEnabled) {
            super.logRequest(configKey, Level.FULL, request)
        }
    }

    @Throws(IOException::class)
    override fun logAndRebufferResponse(configKey: String?, logLevel: Level?, response: Response, elapsedTime: Long): Response {
        if (logger.isDebugEnabled) {
            return super.logAndRebufferResponse(configKey, Level.FULL, response, elapsedTime)
        }

        if (logger.isInfoEnabled) {
            logger.info(
                "Response: Client='{}' Method='{}' URI='{}' Status='{}' Reason='{}' ResponseTime='{}' RequestProtocol='{}' ResponseProtocol='{}'",
                clientName,
                response.request().httpMethod().name,
                response.request().url(),
                response.status(),
                if (response.reason() != null) "" + response.reason() else "",
                elapsedTime,
                response.request().protocolVersion().name,
                response.protocolVersion()?.name ?: "Unknown"
            )
        }

        return response
    }
    override fun log(configKey: String?, format: String?, vararg args: Any?) {
        if (logger.isDebugEnabled) {
            logger.debug(String.format(String.format("%s%s", methodTag(configKey), format), *args))
        }
    }
}
