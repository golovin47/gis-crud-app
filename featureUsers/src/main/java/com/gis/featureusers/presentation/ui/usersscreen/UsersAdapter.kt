package com.gis.featureusers.presentation.ui.usersscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gis.featureusers.R
import com.gis.featureusers.databinding.ItemUsersListBinding
import com.gis.featureusers.presentation.ui.usersscreen.UsersIntent.OpenUpdateUserDialog
import com.gis.utils.domain.ImageLoader
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.subjects.Subject

class UsersAdapter(
  private val imageLoader: ImageLoader,
  private val clicksPublisher: Subject<UsersIntent>
) : ListAdapter<UsersListItem, UsersViewHolder>(object :
  DiffUtil.ItemCallback<UsersListItem>() {

  override fun areItemsTheSame(oldItem: UsersListItem, newItem: UsersListItem): Boolean =
    oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: UsersListItem, newItem: UsersListItem): Boolean =
    oldItem == newItem

}) {

  override fun onFailedToRecycleView(holder: UsersViewHolder): Boolean = true

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
    val binding = DataBindingUtil.inflate<ItemUsersListBinding>(
      LayoutInflater.from(parent.context),
      R.layout.item_users_list,
      parent,
      false
    )
    return UsersViewHolder(binding, imageLoader, clicksPublisher)
  }

  override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
}


class UsersViewHolder(
  private val binding: ItemUsersListBinding,
  private val imageLoader: ImageLoader,
  private val clicksPublisher: Subject<UsersIntent>) :
  RecyclerView.ViewHolder(binding.root) {

  fun bind(user: UsersListItem) {
    RxView.clicks(binding.itemUserRoot)
      .map { OpenUpdateUserDialog(user) }
      .subscribe(clicksPublisher)

    binding.user = user
    binding.imgLoader = imageLoader
  }
}