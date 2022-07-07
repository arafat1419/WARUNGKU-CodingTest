package com.arafat1419.warungku.di

import com.arafat1419.warungku.auth.AuthViewModel
import com.arafat1419.warungku.core.domain.usecase.DataInteractor
import com.arafat1419.warungku.core.domain.usecase.DataUseCase
import com.arafat1419.warungku.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<DataUseCase> { DataInteractor(get()) }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { AuthViewModel(get()) }
}
