package com.cvanes.famly.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class Feed(@JsonProperty("feedItems") val items: List<FeedItem>)

data class FeedItem(
    @JsonProperty("createdDate") val createdDate: OffsetDateTime,
    @JsonProperty("images") val images: List<Image>
)

data class Image(
    @JsonProperty("imageId") val id: String,
    @JsonProperty("url_big") val url: String
)