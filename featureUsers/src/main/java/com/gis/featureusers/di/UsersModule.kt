package com.gis.featureusers.di

import com.gis.featureusers.presentation.ui.usersscreen.UsersViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val usersModule = module {

  viewModel {
    UsersViewModel(get(), get(), get(), get(), get())
  }
}