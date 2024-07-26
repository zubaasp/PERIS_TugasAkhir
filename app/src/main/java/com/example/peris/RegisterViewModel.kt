package com.example.peris

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peris.network.NetworkState
import kotlinx.coroutines.launch

class RegisterViewModel
    (private val repository: Repository): ViewModel() {
        private val _errorMessage = MutableLiveData<String>()
        val errorMessage: LiveData<String>
        get() = _errorMessage

        val register = MutableLiveData<RegisterResponse>()
        val loading = MutableLiveData<Boolean>()

        fun register(request: RegisterRequest) {
            viewModelScope.launch {
                when (val response = repository.register(request)) {
                    is NetworkState.Success -> {
                        register.postValue(response.data)
                        loading.value = false
                    }
                    is NetworkState.Error -> {
                        onError("Error : ${response.response.message()} ")
                        _errorMessage.postValue(response.response.message())
                        loading.value = false
                    }
                }
            }
        }

        private fun onError(message: String) {
            _errorMessage.value = message
            loading.value = false
        }
}