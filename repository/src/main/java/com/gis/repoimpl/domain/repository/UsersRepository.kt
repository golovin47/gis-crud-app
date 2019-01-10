package com.gis.repoimpl.domain.repository

import com.gis.repoimpl.domain.entitiy.User
import io.reactivex.Completable
import io.reactivex.Observable

interface UsersRepository {

  fun getUsers(): Observable<List<User>>

  fun refreshUsers(): Completable

  fun addUser(user: User): Completable

  fun updateUser(user: User): Completable
}