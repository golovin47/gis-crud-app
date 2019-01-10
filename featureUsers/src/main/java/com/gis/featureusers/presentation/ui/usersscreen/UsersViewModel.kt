package com.gis.featureusers.presentation.ui.usersscreen

import com.gis.featureusers.presentation.ui.usersscreen.UsersIntent.GetUsers
import com.gis.featureusers.presentation.ui.usersscreen.UsersIntent.RefreshUsers
import com.gis.featureusers.presentation.ui.usersscreen.UsersStateChange.*
import com.gis.repoimpl.domain.entitiy.User
import com.gis.repoimpl.domain.entitiy.ValidationType.Valid
import com.gis.repoimpl.domain.interactors.*
import com.gis.utils.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UsersViewModel(
  private val getUsersUseCase: GetUsersUseCase,
  private val refreshUsersUseCase: RefreshUsersUseCase,
  private val addUserUseCase: AddUserUseCase,
  private val updateUserUseCase: UpdateUserUseCase,
  private val validateUserUseCase: ValidateUserUseCase
) : BaseViewModel<UsersState>() {

  override fun initState(): UsersState = UsersState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(listOf(

      intentStream.ofType(GetUsers::class.java)
        .switchMap { event ->
          getUsersUseCase.execute()
            .map { users -> UsersReceived((users.map { it.toPresentation() })) }
            .cast(UsersStateChange::class.java)
            .startWith(RefreshStarted)
            .onErrorResumeNext { e: Throwable -> handleError(e) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        },

      intentStream.ofType(RefreshUsers::class.java)
        .switchMap { event ->
          refreshUsersUseCase.execute()
            .startWith(Observable.just(RefreshStarted))
            .cast(UsersStateChange::class.java)
            .onErrorResumeNext { e: Throwable -> handleError(e) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        },

      intentStream.ofType(UsersIntent.OpenAddUserDialog::class.java)
        .map { OpenAddUserDialog },

      intentStream.ofType(UsersIntent.OpenUpdateUserDialog::class.java)
        .map { event -> OpenUpdateUserDialog(event.user) },

      intentStream.ofType(UsersIntent.UserFirstNameChanged::class.java)
        .map { event -> UserFirstNameChanged(event.firstName) },

      intentStream.ofType(UsersIntent.UserLastNameChanged::class.java)
        .map { event -> UserLastNameChanged(event.lastName) },

      intentStream.ofType(UsersIntent.UserEmailChanged::class.java)
        .map { event -> UserEmailChanged(event.email) },

      intentStream.ofType(UsersIntent.AddUser::class.java)
        .switchMap { event ->
          validateUserUseCase.execute(event.user.firstName, event.user.lastName, event.user.email)
            .flatMap { validationResult ->
              if (validationResult.firstNameResult != Valid ||
                validationResult.lastNameResult != Valid ||
                validationResult.emailResult != Valid)
                Observable.just(UserInvalid(validationResult))
              else
                addUserUseCase.execute(event.user.toUser())
                  .andThen(getUsersUseCase.execute())
                  .flatMap { users ->
                    Observable.just(UsersReceived(users.map { it.toPresentation() }), CloseUserDialog)
                  }
                  .cast(UsersStateChange::class.java)
                  .onErrorResumeNext { e: Throwable -> handleError(e) }
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
            }
            .startWith(LoadingInUserDialog)
        },

      intentStream.ofType(UsersIntent.UpdateUser::class.java)
        .switchMap { event ->
          validateUserUseCase.execute(event.user.firstName, event.user.lastName, event.user.email)
            .flatMap { validationResult ->
              if (validationResult.firstNameResult != Valid ||
                validationResult.lastNameResult != Valid ||
                validationResult.emailResult != Valid)
                Observable.just(UserInvalid(validationResult))
              else
                updateUserUseCase.execute(event.user.toUser())
                  .andThen(getUsersUseCase.execute())
                  .flatMap { users ->
                    Observable.just(UsersReceived(users.map { it.toPresentation() }), CloseUserDialog)
                  }
                  .cast(UsersStateChange::class.java)
                  .onErrorResumeNext { e: Throwable -> handleError(e) }
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
            }
            .startWith(LoadingInUserDialog)
        },

      intentStream.ofType(UsersIntent.CloseUserDialog::class.java)
        .map { CloseUserDialog }
    ))

  private fun handleError(e: Throwable) =
    Observable.just(
      UsersStateChange.Error(e),
      UsersStateChange.HideError
    )

  override fun reduceState(
    previousState: UsersState,
    stateChange: Any
  ): UsersState =
    when (stateChange) {

      is RefreshStarted -> previousState.copy(
        refreshing = true,
        error = null
      )

      is UsersReceived -> previousState.copy(
        refreshing = false,
        loadingInUserDialog = false,
        users = stateChange.users
      )

      is UsersStateChange.OpenAddUserDialog -> previousState.copy(
        showUserDialog = true,
        userToUpdate = UsersListItem.emptyUserItem
      )

      is UsersStateChange.OpenUpdateUserDialog -> previousState.copy(
        showUserDialog = true,
        userToUpdate = stateChange.user
      )

      is UserFirstNameChanged -> previousState.copy(
        userToUpdate = previousState.userToUpdate.copy(firstName = stateChange.firstName),
        userValidationResult = previousState.userValidationResult.copy(firstNameResult = Valid))

      is UserLastNameChanged -> previousState.copy(
        userToUpdate = previousState.userToUpdate.copy(lastName = stateChange.lastName),
        userValidationResult = previousState.userValidationResult.copy(lastNameResult = Valid))

      is UserEmailChanged -> previousState.copy(
        userToUpdate = previousState.userToUpdate.copy(email = stateChange.email),
        userValidationResult = previousState.userValidationResult.copy(emailResult = Valid))

      is UserInvalid -> previousState.copy(
        loadingInUserDialog = false,
        userValidationResult = stateChange.validation)

      is LoadingInUserDialog -> previousState.copy(loadingInUserDialog = true)

      is UsersStateChange.CloseUserDialog -> previousState.copy(
        showUserDialog = false,
        userToUpdate = UsersListItem.emptyUserItem
      )

      is Error -> previousState.copy(
        refreshing = false,
        loadingInUserDialog = false,
        error = stateChange.error
      )

      is HideError -> previousState.copy(error = null)

      else -> previousState
    }

  private fun User.toPresentation(): UsersListItem =
    UsersListItem(
      id = id,
      firstName = firstName,
      lastName = lastName,
      email = email,
      avatarUrl = avatarUrl
    )

  private fun UsersListItem.toUser(): User =
    User(
      id = id,
      firstName = firstName,
      lastName = lastName,
      email = email,
      avatarUrl = avatarUrl
    )
}