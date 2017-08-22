package com.example.tamaskozmer.kotlinrxexample.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tamaskozmer.kotlinrxexample.R
import com.example.tamaskozmer.kotlinrxexample.di.modules.UserListFragmentModule
import com.example.tamaskozmer.kotlinrxexample.presentation.presenters.UserListPresenter
import com.example.tamaskozmer.kotlinrxexample.presentation.view.UserListView
import com.example.tamaskozmer.kotlinrxexample.presentation.view.viewmodels.UserViewModel
import com.example.tamaskozmer.kotlinrxexample.util.customApplication
import com.example.tamaskozmer.kotlinrxexample.view.adapters.UserListAdapter
import kotlinx.android.synthetic.main.fragment_user_list.*

/**
 * Created by Tamas_Kozmer on 7/6/2017.
 */
class UserListFragment : Fragment(), UserListView {

    private val presenter: UserListPresenter by lazy { component.presenter() }
    private val component by lazy { customApplication.component.plus(UserListFragmentModule()) }
    private val adapter by lazy {
        val userList = mutableListOf<UserViewModel>()
        UserListAdapter(userList) {
            user -> showUserClikedSnackbar(user)
        }
    }

    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initAdapter()

        presenter.attachView(this)

        // Prevent reloading when going back
        if (adapter.itemCount == 0) {
            showLoading()
            presenter.getUsers()
        }
    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }


    private fun initViews() {
        swipeRefreshLayout.setOnRefreshListener {
            presenter.getUsers(forced = true)
        }
    }

    // region View interface methods
    override fun showLoading() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun addUsersToList(users: List<UserViewModel>) {
        val adapter = recyclerView.adapter as UserListAdapter
        adapter.addUsers(users)
    }

    override fun showEmptyListError() {
        errorView.visibility = View.VISIBLE
    }

    override fun hideEmptyListError() {
        errorView.visibility = View.GONE
    }

    override fun showToastError() {
        Toast.makeText(context, "Error loading data", Toast.LENGTH_SHORT).show()
    }

    override fun clearList() {
        adapter.clearUsers()
    }
    // endregion

    private fun initAdapter() {
        layoutManager = LinearLayoutManager(customApplication)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {

                val lastVisibleItemPosition = layoutManager.findFirstVisibleItemPosition() + layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                presenter.onScrollChanged(lastVisibleItemPosition, totalItemCount)
            }
        })
    }

    private fun showUserClikedSnackbar(user: UserViewModel) {

    }
}