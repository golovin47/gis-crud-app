package com.gis.repoimpl.domain.interactors

import android.util.Patterns
import com.gis.repoimpl.domain.entitiy.User
import com.gis.repoimpl.domain.entitiy.UserValidationResult
import com.gis.repoimpl.domain.entitiy.ValidationType
import com.gis.repoimpl.domain.repository.UsersRepository
import io.reactivex.Observable

class GetUsersUseCase(private val repository: UsersRepository) {

  fun execute(): Observable<List<User>> = repository.getUsers()
}

class RefreshUsersUseCase(private val repository: UsersRepository) {

  fun execute() = repository.refreshUsers()
}


class AddUserUseCase(private val repository: UsersRepository) {

  fun execute(request: User) = repository.addUser(request)
}


class UpdateUserUseCase(private val repository: UsersRepository) {

  fun execute(request: User) = repository.updateUser(request)
}


class ValidateUserUseCase {
  fun execute(firstName: String, lastName: String, email: String): Observable<UserValidationResult> =
    Observable.just(
      UserValidationResult(
        firstNameResult = validateFirstName(firstName),
        lastNameResult = validateLastName(lastName),
        emailResult = validateEmail(email)
      )
    )

  private fun validateFirstName(firstName: String): ValidationType =
    when {
      firstName.isEmpty() -> ValidationType.Empty
      else -> ValidationType.Valid
    }

  private fun validateLastName(lastName: String): ValidationType =
    when {
      lastName.isEmpty() -> ValidationType.Empty
      else -> ValidationType.Valid
    }

  private fun validateEmail(email: String): ValidationType =
    when {
      email.isEmpty() -> ValidationType.Empty
      Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationType.Valid
      else -> ValidationType.Invalid
    }
}