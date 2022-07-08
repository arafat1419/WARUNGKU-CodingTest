package com.arafat1419.warungku.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.arafat1419.warungku.core.domain.model.WarungDomain
import com.arafat1419.warungku.core.domain.usecase.DataUseCase

class MainViewModel(private val dataUseCase: DataUseCase) : ViewModel() {
    fun getAllWarung() = dataUseCase.getAllWarung().asLiveData()

    fun getWarung(id: String) = dataUseCase.getWarung(id).asLiveData()

    fun isNameAvailable(id: String?, name: String) =
        dataUseCase.isNameAvailable(id, name).asLiveData()

    fun saveWarung(warungDomain: WarungDomain) =
        dataUseCase.saveWarung(warungDomain).asLiveData()

    fun uploadWarungImage(uri: Uri, name: String) =
        dataUseCase.uploadWarungImage(uri, name).asLiveData()

    fun signOut() = dataUseCase.signOut()

    fun isUserAuthenticated() =
        dataUseCase.isUserAuthenticated().asLiveData()

}