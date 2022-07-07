package com.arafat1419.warungku.core.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class DataCollection {
    fun warungCollection(): CollectionReference =
        FirebaseFirestore
            .getInstance()
            .collection("warung")

    fun authUser(): FirebaseAuth =
        FirebaseAuth.getInstance()

    fun warungStorage(): FirebaseStorage = Firebase.storage("gs://warungku-58e8a.appspot.com")
}