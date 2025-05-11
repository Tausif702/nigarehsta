package com.nigareshat.app.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nigareshat.app.data.model.MyDownloadRequest
import com.nigareshat.app.data.repo.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    var progress = repository.progress
    var error = repository.error

    var downloadLiveData = repository.downloadLiveData
    fun download(request: MyDownloadRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.download(request)
        }
    }


}