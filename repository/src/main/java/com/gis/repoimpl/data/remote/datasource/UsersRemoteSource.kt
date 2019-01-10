package com.gis.repoimpl.data.remote.datasource

import com.gis.repoimpl.data.remote.api.GISCrudApi
import com.gis.repoimpl.data.remote.entity.AddUpdateUser
import com.gis.repoimpl.data.remote.entity.UserR
import com.gis.repoimpl.data.remote.entity.request.AddUpdateUserRequest
import com.gis.repoimpl.domain.datasource.UsersDataSource
import com.gis.repoimpl.domain.entitiy.User
import io.reactivex.Completable
import io.reactivex.Observable

class UsersRemoteSource(private val api: GISCrudApi) : UsersDataSource {

  override fun getUsers(): Observable<List<User>> = api.getUsers().map { users -> users.map { it.toDomain() } }

  override fun addUser(user: User): Completable =
    api.addUser(AddUpdateUserRequest(user = user.toAddUpdateUser()))

  override fun updateUser(user: User): Completable =
    api.updateUser(user.id, AddUpdateUserRequest(user = user.toAddUpdateUser()))

  private fun UserR.toDomain() =
    User(
      id = id ?: Int.MIN_VALUE,
      firstName = firstName ?: "",
      lastName = lastName ?: "",
      email = email ?: "",
      avatarUrl = avatarUrl ?: ""
    )

  private fun User.toAddUpdateUser() =
    AddUpdateUser(
      firstName = firstName,
      lastName = lastName,
      email = email,
      avatarUrl = avatarUrl
    )
}