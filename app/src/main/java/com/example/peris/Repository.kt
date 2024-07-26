package com.example.peris

import com.example.peris.network.ApiService
import com.example.peris.network.NetworkState

class Repository(
    private val apiService: ApiService
) {
    suspend fun login(request: LoginRequest): NetworkState<LoginResponse> {
        val response = apiService.login(request)
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }
    suspend fun register(request: RegisterRequest): NetworkState<RegisterResponse> {
        val response = apiService.register(request)
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }
    }
}

