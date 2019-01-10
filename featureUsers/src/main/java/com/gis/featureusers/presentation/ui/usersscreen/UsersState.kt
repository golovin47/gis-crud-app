package com.gis.featureusers.presentation.ui.usersscreen

import com.gis.repoimpl.domain.entitiy.UserValidationResult


data class UsersState(
  val refreshing: Boolean = false,
  val users: List<UsersListItem> = emptyList(),
  val showUserDialog: Boolean = false,
  val loadingInUserDialog: Boolean = false,
  val userToUpdate: UsersListItem = UsersListItem.emptyUserItem,
  val userValidationResult: UserValidationResult = UserValidationResult(),
  val error: Throwable? = null
)


sealed class UsersIntent {
  object GetUsers : UsersIntent()
  object RefreshUsers : UsersIntent()
  object OpenAddUserDialog : UsersIntent()
  class OpenUpdateUserDialog(val user: UsersListItem) : UsersIntent()
  class UserFirstNameChanged(val firstName: String) : UsersIntent()
  class UserLastNameChanged(val lastName: String) : UsersIntent()
  class UserEmailChanged(val email: String) : UsersIntent()
  object CloseUserDialog : UsersIntent()
  class AddUser(val user: UsersListItem) : UsersIntent()
  class UpdateUser(val user: UsersListItem) : UsersIntent()
}


sealed class UsersStateChange {
  object LoadingInUserDialog : UsersStateChange()
  object RefreshStarted : UsersStateChange()
  class UsersReceived(val users: List<UsersListItem>) : UsersStateChange()
  object OpenAddUserDialog : UsersStateChange()
  class OpenUpdateUserDialog(val user: UsersListItem) : UsersStateChange()
  class UserFirstNameChanged(val firstName: String) : UsersStateChange()
  class UserLastNameChanged(val lastName: String) : UsersStateChange()
  class UserEmailChanged(val email: String) : UsersStateChange()
  class UserInvalid(val validation: UserValidationResult) : UsersStateChange()
  object CloseUserDialog : UsersStateChange()
  class Error(val error: Throwable) : UsersStateChange()
  object HideError : UsersStateChange()
}


data class UsersListItem(
  val id: Int = Int.MIN_VALUE,
  val firstName: String = "",
  val lastName: String = "",
  val email: String = "",
  val avatarUrl: String = "") {

  companion object {
    val emptyUserItem = UsersListItem()
  }
}