package com.example.tamaskozmer.kotlinrxexample.presentation.presenters

import com.example.tamaskozmer.kotlinrxexample.domain.interactors.GetUsers
import com.example.tamaskozmer.kotlinrxexample.presentation.view.UserListView
import com.example.tamaskozmer.kotlinrxexample.presentation.view.viewmodels.UserViewModel
import com.example.tamaskozmer.kotlinrxexample.testutil.ImmediateSchedulerRule
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.`when` as whenever

/**
 * Created by Tamas_Kozmer on 7/21/2017.
 */
class UserListPresenterTest {

    @Rule @JvmField
    val immediateSchedulerRule = ImmediateSchedulerRule()

    @Mock
    lateinit var mockGetUsers: GetUsers

    @Mock
    lateinit var mockView: UserListView

    lateinit var userListPresenter: UserListPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        userListPresenter = UserListPresenter(mockGetUsers)
    }

    @Test
    fun testGetUsers_errorCase_showError() {
        // Given
        val error = "Test error"
        val single: Single<List<UserViewModel>> = Single.create {
            emitter ->
            emitter.onError(Exception(error))
        }

        // When
        whenever(mockGetUsers.execute(anyInt(), anyBoolean())).thenReturn(single)

        userListPresenter.attachView(mockView)
        userListPresenter.getUsers()

        // Then
        verify(mockView).hideLoading()
        verify(mockView).showEmptyListError()
    }

    @Test
    fun testGetUsers_successCaseFirstPage_clearList() {
        // Given
        val users = listOf(UserViewModel(1, "Name", 1000, ""))
        val single: Single<List<UserViewModel>> = Single.create {
            emitter ->
            emitter.onSuccess(users)
        }

        // When
        whenever(mockGetUsers.execute(anyInt(), anyBoolean())).thenReturn(single)

        userListPresenter.attachView(mockView)
        userListPresenter.getUsers()

        // Then
        verify(mockView).clearList()
    }

    @Test
    fun testGetUsers_successCaseMultipleTimes_clearListOnlyOnce() {
        // Given
        val users = listOf(UserViewModel(1, "Name", 1000, ""))
        val single: Single<List<UserViewModel>> = Single.create {
            emitter ->
            emitter.onSuccess(users)
        }

        // When
        whenever(mockGetUsers.execute(anyInt(), anyBoolean())).thenReturn(single)

        userListPresenter.attachView(mockView)
        userListPresenter.getUsers()
        userListPresenter.getUsers()

        // Then
        verify(mockView).clearList()
        verify(mockView, times(2)).hideLoading()
        verify(mockView, times(2)).addUsersToList(users)
    }

    @Test
    fun testGetUsers_forcedSuccessCaseMultipleTimes_clearListEveryTime() {
        // Given
        val users = listOf(UserViewModel(1, "Name", 1000, ""))
        val single: Single<List<UserViewModel>> = Single.create {
            emitter ->
            emitter.onSuccess(users)
        }

        // When
        whenever(mockGetUsers.execute(anyInt(), anyBoolean())).thenReturn(single)

        userListPresenter.attachView(mockView)
        userListPresenter.getUsers(forced = true)
        userListPresenter.getUsers(forced = true)

        // Then
        verify(mockView, times(2)).clearList()
        verify(mockView, times(2)).hideLoading()
        verify(mockView, times(2)).addUsersToList(users)
    }
}