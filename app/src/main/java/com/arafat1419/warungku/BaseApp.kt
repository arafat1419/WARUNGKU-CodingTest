package com.arafat1419.warungku

import android.app.Application
import com.arafat1419.warungku.core.di.firebaseModule
import com.arafat1419.warungku.core.di.repositoryModule
import com.arafat1419.warungku.di.useCaseModule
import com.arafat1419.warungku.di.viewModelModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BaseApp : Application() {

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        // startKoin use to make koin create injection to all descendant application like activity and fragment
        startKoin {
            androidLogger(Level.NONE)
            // Provide function that need context
            androidContext(this@BaseApp)
            // Add modules to koin
            modules(
                listOf(
                    repositoryModule,
                    useCaseModule,
                    viewModelModule,
                    firebaseModule
                )
            )
        }
    }
}