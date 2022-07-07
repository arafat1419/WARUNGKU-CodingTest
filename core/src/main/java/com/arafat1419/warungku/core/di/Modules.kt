package com.arafat1419.warungku.core.di

import com.arafat1419.warungku.core.data.DataCollection
import com.arafat1419.warungku.core.data.DataRepository
import com.arafat1419.warungku.core.domain.repository.IDataRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { DataCollection() }
    single<IDataRepository> {
        DataRepository(get())
    }
}

val firebaseModule = module {
    single {
        FirebaseFirestore.setLoggingEnabled(true)
    }
}