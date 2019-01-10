package com.gis.repoimpl.data.remote.entity

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
data class UserR(
  @JsonField(name = ["id"]) var id: Int? = Int.MIN_VALUE,
  @JsonField(name = ["first_name"]) var firstName: String? = "",
  @JsonField(name = ["last_name"]) var lastName: String? = "",
  @JsonField(name = ["email"]) var email: String? = "",
  @JsonField(name = ["avatar_url"]) var avatarUrl: String? = ""
)