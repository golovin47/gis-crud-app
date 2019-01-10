package com.gis.repoimpl.domain.entitiy

data class User(
  val id: Int = Int.MIN_VALUE,
  val firstName: String = "",
  val lastName: String = "",
  val email: String = "",
  val avatarUrl: String = "")