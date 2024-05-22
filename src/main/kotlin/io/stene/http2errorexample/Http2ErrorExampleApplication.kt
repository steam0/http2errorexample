package io.stene.http2errorexample

import feign.Feign
import feign.Logger.Level
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import io.stene.http2errorexample.client.PlayerProfileApi
import io.stene.http2errorexample.client.logger.FeignLogger
import io.stene.http2errorexample.client.type.PhoneNumber
import io.stene.http2errorexample.client.type.PlayerProfileRequest
import okhttp3.Protocol
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Http2errorexampleApplication {

	private val log: Logger = LoggerFactory.getLogger(this::class.java)

	final val personId = "702046491"
	final val baseUrl = "https://api.nt.qa-02.buypass.no/player-profile-api/"
	final val httpUrlConnectionPlayerProfileApi = Feign.builder()
		.decoder(JacksonDecoder())
		.encoder(JacksonEncoder())
		.logLevel(Level.FULL)
		.logger(FeignLogger(PlayerProfileApi::class.java, "HttpUrlConnection"))
		.target(PlayerProfileApi::class.java, baseUrl)

	final val okHttp3PlayerProfileApi = Feign.builder()
		.client(OkHttpClient(
			okhttp3.OkHttpClient().newBuilder().protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1)).build()
		))
		.decoder(JacksonDecoder())
		.encoder(JacksonEncoder())
		.logLevel(Level.FULL)
		.logger(FeignLogger(PlayerProfileApi::class.java, "OkHttp3"))
		.target(PlayerProfileApi::class.java, baseUrl)

	init {
		val numberOfRequestsPerClient = 2

		// Run httpclient
		for (number in 1..numberOfRequestsPerClient) {
			try {
				httpUrlConnectionPlayerProfileApi.upload(personId, createRequest())
			} catch (exception: Exception) {
				log.error("Something went wrong executing request", exception)
			}
		}

		// Run okhttp
		for (number in 1..numberOfRequestsPerClient) {
			try {
				okHttp3PlayerProfileApi.upload(personId, createRequest())
			} catch (exception: Exception) {
				log.error("Something went wrong executing request", exception)
			}
		}

	}

	private fun createRequest(phoneNumber: String = "12345670"): PlayerProfileRequest {
		return PlayerProfileRequest(
			listOf(PhoneNumber(phoneNumber))
		)
	}
}

fun main(args: Array<String>) {
	runApplication<Http2errorexampleApplication>(*args)
}
