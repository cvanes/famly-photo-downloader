package com.cvanes.famly.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthenticationContext(
    @JsonProperty("deviceId") val deviceId: String,
    @JsonProperty("accessToken") val accessToken: String
)