package com.gis.repoimpl.data.remote.entity.request

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import com.gis.repoimpl.data.remote.entity.AddUpdateUser

@JsonObject
data class AddUpdateUserRequest(@JsonField(name = ["user"]) var user: AddUpdateUser = AddUpdateUser())