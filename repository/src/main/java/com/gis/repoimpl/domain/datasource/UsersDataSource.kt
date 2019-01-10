package com.gis.repoimpl.domain.datasource

import com.gis.repoimpl.domain.entitiy.User
import io.reactivex.Completable
import io.reactivex.Observable

interface UsersDataSource {

  fun getUsers(): Observable<List<User>>

  fun addUser(user: User): Completable

  fun updateUser(user: User): Completable
}