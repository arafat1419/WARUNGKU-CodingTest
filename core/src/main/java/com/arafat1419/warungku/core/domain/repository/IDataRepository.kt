package com.arafat1419.warungku.core.domain.repository

import android.net.Uri
import com.arafat1419.warungku.core.domain.model.WarungDomain
import com.arafat1419.warungku.core.vo.Resource
import kotlinx.coroutines.flow.Flow

interface IDataRepository {
    fun getAllWarung(): Flow<Resource<List<WarungDomain>>>
    fun getWarung(id: String): Flow<Resource<WarungDomain?>>
    fun saveWarung(warungDomain: WarungDomain): Flow<Resource<Boolean>>

    fun uploadWarungImage(uri: Uri, name: String): Flow<Resource<Uri?>>

    fun isUserAuthenticated(): Flow<Boolean>
    fun signInUser(username: String, password: String): Flow<Resource<Boolean>>
    fun signUpUser(username: String, password: String): Flow<Resource<Boolean>>
    fun signOut()
}