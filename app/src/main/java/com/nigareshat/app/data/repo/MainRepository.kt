package com.nigareshat.app.data.repo

import androidx.lifecycle.MutableLiveData
import com.nigareshat.app.data.model.MyDownloadRequest
import com.nigareshat.app.data.model.MyDownloadResponse
import com.nigareshat.app.network.ApiServices
import javax.inject.Inject


class MainRepository @Inject constructor(
    private val api: ApiServices
) : BaseRepository() {

    val downloadLiveData = MutableLiveData<MyDownloadResponse>()
    suspend fun download(request: MyDownloadRequest) {
        handleApiCall(
            api = { api.download(request) },
            responseLiveData = downloadLiveData
        )
    }



}