package com.erolc.mrouter.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore

interface MRouterViewModelStoreProvider {
    fun getViewModelStore(entryId: String): ViewModelStore
}

internal expect class MRouterControllerViewModel:ViewModel, MRouterViewModelStoreProvider {
    fun clear(backStackEntryId: String)

    companion object {
        fun getInstance(viewModelStore: ViewModelStore): MRouterControllerViewModel
    }
}

