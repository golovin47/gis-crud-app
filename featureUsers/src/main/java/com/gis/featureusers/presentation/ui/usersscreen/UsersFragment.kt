package com.gis.featureusers.presentation.ui.usersscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gis.featureusers.R
import com.gis.featureusers.databinding.DialogAddUpdateUserBinding
import com.gis.featureusers.databinding.FragmentUsersBinding
import com.gis.featureusers.presentation.ui.usersscreen.UsersIntent.*
import com.gis.repoimpl.domain.entitiy.UserValidationResult
import com.gis.repoimpl.domain.entitiy.ValidationType.*
import com.gis.utils.BaseView
import com.gis.utils.domain.ImageLoader
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit
import android.content.DialogInterface.*

class UsersFragment : Fragment(), BaseView<UsersState> {

  val imageLoader: ImageLoader by inject()

  private lateinit var currentState: UsersState

  private var userAddUpdateDialog: AlertDialog? = null
  private var dialogBinding: DialogAddUpdateUserBinding? = null

  private val usersIntentsPublisher = PublishSubject.create<UsersIntent>()
  private var binding: FragmentUsersBinding? = null

  private lateinit var viewSubscriptions: Disposable
  private val vmUsers: UsersViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    handleStates()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    initBinding(inflater, container)
    initRecyclerView(container!!.context)
    initSwipeRefresh()
    initAddUpdateUserDialog(inflater, container)
    initIntents()

    return binding!!.root
  }

  override fun onDestroyView() {
    viewSubscriptions.dispose()
    binding = null
    dialogBinding = null
    userAddUpdateDialog?.cancel()
    userAddUpdateDialog = null
    super.onDestroyView()
  }

  private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false)
  }

  private fun initRecyclerView(context: Context) {
    val adapter = UsersAdapter(
      imageLoader,
      usersIntentsPublisher
    )
    binding!!.rvPeople
      .apply {
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        setAdapter(adapter)
      }
  }

  private fun initSwipeRefresh() {
    binding!!.srlRefreshPeople.setOnRefreshListener {
      usersIntentsPublisher.onNext(RefreshUsers)
    }
  }

  private fun initAddUpdateUserDialog(inflater: LayoutInflater, container: ViewGroup) {
    dialogBinding = DataBindingUtil.inflate(
      inflater,
      R.layout.dialog_add_update_user,
      container,
      false)

    userAddUpdateDialog = AlertDialog.Builder(context!!)
      .setView(dialogBinding!!.root)
      .setPositiveButton(R.string.ok, null)
      .setNegativeButton(R.string.cancel, null)
      .setCancelable(false)
      .create()

    userAddUpdateDialog!!.setOnShowListener { dialog ->
      (dialog as AlertDialog).getButton(BUTTON_POSITIVE).setOnClickListener {
        usersIntentsPublisher.onNext(
          if (currentState.userToUpdate.id != Int.MIN_VALUE)
            UpdateUser(currentState.userToUpdate)
          else
            AddUser(currentState.userToUpdate))
      }

      dialog.getButton(BUTTON_NEGATIVE).setOnClickListener {
        usersIntentsPublisher.onNext(CloseUserDialog)
      }
    }
  }

  @SuppressLint("CheckResult")
  override fun initIntents() {
    viewSubscriptions = Observable.merge(
      listOf(

        Observable.just(GetUsers),

        usersIntentsPublisher,

        RxView.clicks(binding!!.fabAddUser)
          .throttleFirst(500, TimeUnit.MILLISECONDS)
          .map { OpenAddUserDialog },

        RxTextView.textChanges(dialogBinding!!.etUserFirstName)
          .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
          .map { firstName -> UserFirstNameChanged(firstName.toString()) },

        RxTextView.textChanges(dialogBinding!!.etUserLastName)
          .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
          .map { lastName -> UserLastNameChanged(lastName.toString()) },

        RxTextView.textChanges(dialogBinding!!.etUserEmail)
          .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
          .map { email -> UserEmailChanged(email.toString()) },

        usersIntentsPublisher.ofType(AddUser::class.java)
          .throttleFirst(500, TimeUnit.MILLISECONDS),

        usersIntentsPublisher.ofType(UpdateUser::class.java)
          .throttleFirst(500, TimeUnit.MILLISECONDS),

        usersIntentsPublisher.ofType(CloseUserDialog::class.java)
          .throttleFirst(500, TimeUnit.MILLISECONDS)
      )
    )
      .subscribe(vmUsers.viewIntentsConsumer())
  }

  private fun showAddUpdateUserDialog() {
    if (!userAddUpdateDialog!!.isShowing) {
      userAddUpdateDialog!!.show()
      if (dialogBinding!!.imgLoader == null)
        dialogBinding!!.imgLoader = imageLoader
      if (dialogBinding!!.user == null)
        dialogBinding!!.user = currentState.userToUpdate
    }
  }

  private fun closeAddUpdateUserDialog() {
    userAddUpdateDialog!!.cancel()
    dialogBinding!!.imgLoader = null
    dialogBinding!!.user = null
  }

  private fun processDialogLoading(loadingInUserDialog: Boolean) {
    dialogBinding!!.loading = loadingInUserDialog

    if (loadingInUserDialog) {
      if (userAddUpdateDialog!!.isShowing) {
        userAddUpdateDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = false
        userAddUpdateDialog!!.getButton(DialogInterface.BUTTON_NEGATIVE).isEnabled = false
      }
    } else {
      if (userAddUpdateDialog!!.isShowing) {
        userAddUpdateDialog!!.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = true
        userAddUpdateDialog!!.getButton(DialogInterface.BUTTON_NEGATIVE).isEnabled = true
      }
    }
  }

  private fun processValidationResult(validationResult: UserValidationResult) {
    when (validationResult.firstNameResult) {
      is Valid -> dialogBinding!!.etUserFirstName.error = null
      else -> dialogBinding!!.etUserFirstName.error = getString(R.string.error_field_empty)
    }

    when (validationResult.lastNameResult) {
      is Valid -> dialogBinding!!.etUserLastName.error = null
      else -> dialogBinding!!.etUserLastName.error = getString(R.string.error_field_empty)
    }

    when (validationResult.emailResult) {
      is Valid -> dialogBinding!!.etUserEmail.error = null
      is Empty -> dialogBinding!!.etUserEmail.error = getString(R.string.error_field_empty)
      is Invalid -> dialogBinding!!.etUserEmail.error = getString(R.string.error_email_invalid)
    }
  }

  override fun handleStates() {
    vmUsers.stateReceived().observe(this, Observer { state -> render(state) })
  }

  override fun render(state: UsersState) {
    currentState = state

    if (state.showUserDialog) showAddUpdateUserDialog()
    else closeAddUpdateUserDialog()

    processValidationResult(state.userValidationResult)

    processDialogLoading(state.loadingInUserDialog)

    binding!!.srlRefreshPeople.isRefreshing = state.refreshing

    if (state.error != null)
      Snackbar.make(view!!, state.error.message!!, Snackbar.LENGTH_SHORT).show()

    (binding!!.rvPeople.adapter as UsersAdapter).submitList(state.users)
  }
}