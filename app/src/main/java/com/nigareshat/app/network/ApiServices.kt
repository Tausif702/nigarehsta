package com.nigareshat.app.network

import com.nigareshat.app.data.model.MyDownloadRequest
import com.nigareshat.app.data.model.MyDownloadResponse
import com.nigareshat.app.utils.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiServices {

    @POST(Constants.download)
    suspend fun download(
        @Body request: MyDownloadRequest
    ): Response<MyDownloadResponse>




}
