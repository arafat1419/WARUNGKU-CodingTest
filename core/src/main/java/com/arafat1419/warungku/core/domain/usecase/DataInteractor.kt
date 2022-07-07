package com.arafat1419.warungku.core.domain.usecase

import android.net.Uri
import android.telephony.PhoneNumberUtils
import com.arafat1419.warungku.core.domain.model.WarungDomain
import com.arafat1419.warungku.core.domain.repository.IDataRepository
import com.arafat1419.warungku.core.utils.Helper.PHONE_FORMAT_HELPER
import com.arafat1419.warungku.core.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class DataInteractor(private val iDataRepository: IDataRepository) : DataUseCase {
    override fun getAllWarung(): Flow<Resource<List<WarungDomain>>> =
        flow {
            emit(Resource.Loading())

            try {
                iDataRepository.getAllWarung().collect {
                    emit(it)
                }

            } catch (e: Exception) {
                emit(Resource.Failure(e.message))
            }
        }

    override fun getWarung(id: String): Flow<Resource<WarungDomain?>> =
        flow {
            emit(Resource.Loading())

            try {
                iDataRepository.getWarung(id).collect {
                    emit(it)
                }

            } catch (e: Exception) {
                emit(Resource.Failure(e.message))
            }
        }

    override fun saveWarung(warungDomain: WarungDomain): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())

            try {
                iDataRepository.saveWarung(warungDomain).collect {
                    emit(it)
                }

            } catch (e: Exception) {
                emit(Resource.Failure(e.message))
            }
        }

    override fun uploadWarungImage(uri: Uri, name: String): Flow<Resource<Uri?>> =
        flow {
            emit(Resource.Loading())

            try {
                iDataRepository.uploadWarungImage(uri, name).collect {
                    emit(it)
                }

            } catch (e: Exception) {
                emit(Resource.Failure(e.message))
            }
        }

    override fun isUserAuthenticated(): Flow<Boolean> =
        iDataRepository.isUserAuthenticated()

    override fun signInUser(username: String, password: String): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())

            try {
                iDataRepository.signInUser(isValidNumber(username), password).collect {
                    emit(it)
                }

            } catch (e: Exception) {
                emit(Resource.Failure(e.message))
            }
        }

    override fun signUpUser(username: String, password: String): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())

            try {
                iDataRepository.signUpUser(isValidNumber(username), password).collect {
                    emit(it)
                }

            } catch (e: Exception) {
                emit(Resource.Failure(e.message))
            }
        }

    override fun signOut() {
        iDataRepository.signOut()
    }

    private fun isValidNumber(username: String): String {
        return if (PhoneNumberUtils.isGlobalPhoneNumber(username)) username + PHONE_FORMAT_HELPER
        else username
    }
}