package com.gis.repoimpl.domain.entitiy

import com.gis.repoimpl.domain.entitiy.ValidationType.Valid

data class UserValidationResult(
  val firstNameResult: ValidationType = Valid,
  val lastNameResult: ValidationType = Valid,
  val emailResult: ValidationType = Valid
)