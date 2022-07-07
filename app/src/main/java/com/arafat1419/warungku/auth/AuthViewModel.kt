package com.arafat1419.warungku.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.arafat1419.warungku.core.domain.usecase.DataUseCase

class AuthViewModel(private val dataUseCase: DataUseCase) : ViewModel() {
    fun signUpUser(username: String, password: String) =
        dataUseCase.signUpUser(username, password).asLiveData()

    fun isUserAuthenticated() =
        dataUseCase.isUserAuthenticated().asLiveData()

    fun signInUser(username: String, password: String) =
        dataUseCase.signInUser(username, password).asLiveData()
}