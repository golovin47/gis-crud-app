package com.gis.repoimpl.di

import android.content.Context.MODE_PRIVATE
import com.gis.repoimpl.data.remote.api.GISCrudApiProvider
import com.gis.repoimpl.data.remote.datasource.UsersRemoteSource
import com.gis.repoimpl.data.repository.UsersRepositoryImpl
import com.gis.repoimpl.domain.datasource.UsersDataSource
import com.gis.repoimpl.domain.interactors.*
import com.gis.repoimpl.domain.repository.UsersRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val repoModule = module {

  single { GISCrudApiProvider.createApi() }

  single { androidContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE) }

  //Users

  single<UsersDataSource>("remote") { UsersRemoteSource(get()) }

  single<UsersRepository> { UsersRepositoryImpl( get("remote")) }

  factory { GetUsersUseCase(get()) }

  factory { RefreshUsersUseCase(get()) }

  factory { AddUserUseCase(get()) }

  factory { UpdateUserUseCase(get()) }

  factory { ValidateUserUseCase() }
}