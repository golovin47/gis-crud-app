package com.gis.repoimpl.data.repository

import com.gis.repoimpl.domain.datasource.UsersDataSource
import com.gis.repoimpl.domain.entitiy.User
import com.gis.repoimpl.domain.repository.UsersRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class UsersRepositoryImpl(
  private val remoteSourceUsers: UsersDataSource
) :
  UsersRepository {

  private val usersPublisher = PublishSubject.create<Any>()

  override fun getUsers(): Observable<List<User>> = usersPublisher
    .startWith(this)
    .switchMap { remoteSourceUsers.getUsers() }

  override fun refreshUsers(): Completable = Completable.fromAction { usersPublisher.onNext(this) }

  override fun addUser(user: User): Completable = remoteSourceUsers.addUser(user)

  override fun updateUser(user: User): Completable = remoteSourceUsers.updateUser(user)
}