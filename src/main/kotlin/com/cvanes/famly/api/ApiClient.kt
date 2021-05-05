package com.cvanes.famly.api

import com.cvanes.famly.model.AuthenticationContext
import com.cvanes.famly.model.Feed
import com.cvanes.famly.model.FeedItem
import com.cvanes.famly.model.Image
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.system.measureTimeMillis

const val BASE_URL = "https://app.famly.co/api"

class ApiClient(
    private val email: String,
    private val password: String,
    private val downloadLocation: File,
    private val httpClient: HttpClient
) {

    private val logger = LoggerFactory.getLogger(ApiClient::class.java)

    suspend fun downloadPhotos(startDate: OffsetDateTime, endDate: OffsetDateTime) = withContext(Dispatchers.IO) {
        val authContext = authenticate()

        logger.info("Downloading images from $startDate to $endDate")
        val duration = measureTimeMillis {
            fetchFeed(authContext, startDate, endDate)
                .flatMap { it.images }
                .also { logger.info("Found ${it.size} photos") }
                .map { async { downloadPhoto(it) } }
                .awaitAll()
        }

        logger.info("Downloaded images in ${duration}ms")
    }

    private suspend fun authenticate(): AuthenticationContext {
        logger.info("Authenticating user")
        return httpClient.post("$BASE_URL/login/login/authenticate") {
            body = mapOf(
                "email" to email,
                "password" to password,
                "deviceId" to UUID.randomUUID(),
                "locale" to "en-GB",
            )
        }
    }

    private suspend fun fetchFeed(authContext: AuthenticationContext, startDate: OffsetDateTime, endDate: OffsetDateTime): List<FeedItem> {
        val feed = httpClient.get<Feed>("$BASE_URL/feed/feed/feed") {
            header("x-famly-accesstoken", authContext.accessToken)
            parameter("olderThan", endDate)
        }

        val items = feed.items.filter { it.createdDate.isAfter(startDate) }
        val isFullPageOfResults = items.size == feed.items.size
        if (items.isNotEmpty() && isFullPageOfResults) {
            return feed.items + fetchFeed(authContext, startDate, feed.items.last().createdDate)
        }

        return items
    }

    private suspend fun downloadPhoto(image: Image) {
        if (!downloadLocation.exists()) {
            logger.info("Creating $downloadLocation")
            downloadLocation.mkdirs()
        }
        val file = File(downloadLocation, "${image.id}.jpg")
        val imageData = httpClient.get<ByteArray>(image.url)
        file.writeBytes(imageData)
    }
}
