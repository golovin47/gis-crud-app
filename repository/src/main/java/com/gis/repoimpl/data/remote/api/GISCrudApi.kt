package com.gis.repoimpl.data.remote.api

import com.gis.repoimpl.data.remote.entity.UserR
import com.gis.repoimpl.data.remote.entity.request.AddUpdateUserRequest
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

interface GISCrudApi {

  @GET("users.json")
  fun getUsers(): Observable<List<UserR>>

  @POST("users.json")
  fun addUser(@Body addUpdateUserRequest: AddUpdateUserRequest): Completable

  @PATCH("users/{id}.json")
  fun updateUser(
    @Path("id") id: Int,
    @Body addUpdateUserRequest: AddUpdateUserRequest): Completable
}