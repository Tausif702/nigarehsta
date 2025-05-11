package com.nigareshat.app.data.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import retrofit2.Response

abstract class BaseRepository {

    var progress = MutableLiveData<Boolean>()
    var error = MutableLiveData<String>()

    protected suspend fun <T> handleApiCall(
        api: suspend () -> Response<T>,
        responseLiveData: MutableLiveData<T>
    ) {
        progress.postValue(true)
        try {
            val response = api()
            if (response.isSuccessful && response.body() != null) {
                responseLiveData.postValue(response.body())
            } else if (response.errorBody() != null) {
                val errorJsonObject = JSONObject(response.errorBody()!!.charStream().readText())
                val errorMessage = errorJsonObject.getString("message")
                error.postValue(errorMessage)
            } else {
                error.postValue("Something went wrong")
            }
        } catch (e: Exception) {
            error.postValue(e.message)
            Log.d("Exception", e.message.toString())
        } finally {
            progress.postValue(false)
        }
    }
}
