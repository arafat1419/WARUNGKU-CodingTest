package com.arafat1419.warungku.core.data

import android.net.Uri
import com.arafat1419.warungku.core.domain.model.WarungDomain
import com.arafat1419.warungku.core.domain.repository.IDataRepository
import com.arafat1419.warungku.core.vo.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
class DataRepository(
    private val dataCollection: DataCollection
) : IDataRepository {
    override fun getAllWarung(): Flow<Resource<List<WarungDomain>>> =
        callbackFlow {
            val snapshotListener = dataCollection.warungCollection()
                .addSnapshotListener { value, error ->
                    val response = if (value != null) {
                        Resource.Success(value.toObjects(WarungDomain::class.java))
                    } else {
                        Resource.Failure(error?.message)
                    }
                    trySend(response)
                }

            awaitClose { snapshotListener.remove() }
        }

    override fun getWarung(id: String): Flow<Resource<WarungDomain?>> =
        callbackFlow {
            val listener = dataCollection.warungCollection().document(id)
                .get()
                .addOnSuccessListener { document ->
                    val response = if (document.data != null) {
                        Resource.Success(document.toObject(WarungDomain::class.java))
                    } else {
                        Resource.Failure("Document not found")
                    }
                    trySend(response)
                }

            awaitClose { listener.result }
        }

    override fun saveWarung(warungDomain: WarungDomain): Flow<Resource<Boolean>> =
        callbackFlow {
            val id = if (warungDomain.id == null) {
                val newId = dataCollection.warungCollection().document().id
                warungDomain.id = newId
                newId
            } else {
                warungDomain.id!!
            }

            val objectToMap = hashMapOf<String, Any?>(
                "id" to warungDomain.id,
                "photoUrl" to warungDomain.photoUrl,
                "name" to warungDomain.name,
                "lat" to warungDomain.lat,
                "long" to warungDomain.long,
                "address" to warungDomain.address
            )

            val listener = dataCollection.warungCollection()
                .document(id)
                .set(objectToMap)
                .addOnCompleteListener { task ->

                    val response = if (task.isSuccessful) Resource.Success(task.isSuccessful)
                    else Resource.Failure(task.exception?.message)

                    trySend(response)
                }

            awaitClose { listener.result }
        }

    override fun isNameAvailable(id: String?, name: String): Flow<Resource<Boolean>> =
        callbackFlow {
            val listener = dataCollection.warungCollection()
                .whereEqualTo("name", name)
                .limit(1)
                .get()
                .addOnCompleteListener { task ->
                    val response = if (task.isSuccessful) {
                        val getData = task.result.toObjects(WarungDomain::class.java)
                        Resource.Success(task.result.isEmpty || getData[0].id == id)
                    } else Resource.Failure(task.exception?.message)

                    trySend(response)
                }

            awaitClose { listener.result }
        }

    override fun uploadWarungImage(uri: Uri, name: String): Flow<Resource<Uri?>> =
        callbackFlow {
            val listener = dataCollection.warungStorage().reference
                .child("image/${name}.jpg")
                .putFile(uri)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.storage.downloadUrl
                            .addOnCompleteListener { downloadTask ->
                                val response =
                                    if (downloadTask.isSuccessful) Resource.Success(downloadTask.result)
                                    else Resource.Failure(downloadTask.exception?.message)

                                trySend(response)
                            }
                    }
                }

            awaitClose { listener.result }
        }

    override fun isUserAuthenticated(): Flow<Boolean> =
        flow {
            val getUser = dataCollection.authUser().currentUser

            emit(getUser != null)
        }

    override fun signInUser(username: String, password: String): Flow<Resource<Boolean>> =
        callbackFlow {
            val listener = dataCollection.authUser()
                .signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    val response = if (task.isSuccessful) Resource.Success(task.isSuccessful)
                    else Resource.Failure(task.exception?.message)

                    trySend(response)
                }

            awaitClose { listener.result }
        }

    override fun signUpUser(username: String, password: String): Flow<Resource<Boolean>> =
        callbackFlow {

            val listener = dataCollection.authUser()
                .createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    val response = if (task.isSuccessful) Resource.Success(task.isSuccessful)
                    else Resource.Failure(task.exception?.message)

                    trySend(response)
                }

            awaitClose { listener.result }
        }

    override fun signOut() {
        dataCollection.authUser()
            .signOut()
    }
}
