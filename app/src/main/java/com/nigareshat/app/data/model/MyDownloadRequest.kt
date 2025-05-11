package com.nigareshat.app.data.model

import com.google.gson.annotations.SerializedName

data class MyDownloadRequest(
    @SerializedName("url")
    val url: String?
)

data class MyDownloadResponse(
    @SerializedName("video_url")
    val videoUrl: String?
)