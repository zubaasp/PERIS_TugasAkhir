package com.example.peris

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peris.network.NetworkState
import kotlinx.coroutines.launch

class LoginViewModel (private val repository: Repository):ViewModel() {
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val login = MutableLiveData<LoginResponse>()
    val loading = MutableLiveData<Boolean>()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            when (val response = repository.login(request)) {
                is NetworkState.Success -> {
                    login.postValue(response.data)
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