package com.gis.repoimpl.domain.entitiy

sealed class ValidationType {
  object Empty : ValidationType()
  object Valid : ValidationType()
  object Invalid : ValidationType()
}