package com.erolc.mrouter.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.get
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlin.reflect.KClass

internal actual class MRouterControllerViewModel : ViewModel(), MRouterViewModelStoreProvider {

    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    actual fun clear(entryId: String) {
        // Clear and remove the NavGraph's ViewModelStore
        val viewModelStore = viewModelStores.remove(entryId)
        viewModelStore?.clear()
    }

    override fun onCleared() {
        for (store in viewModelStores.values) {
            store.clear()
        }
        viewModelStores.clear()
    }

    override fun getViewModelStore(entryId: String): ViewModelStore {
        var viewModelStore = viewModelStores[entryId]
        if (viewModelStore == null) {
            viewModelStore = ViewModelStore()
            viewModelStores[entryId] = viewModelStore
        }
        return viewModelStore
    }

    override fun toString(): String {
        val sb = StringBuilder("NavControllerViewModel{")
        sb.append(hashCode())
        sb.append("} ViewModelStores (")
        val viewModelStoreIterator: Iterator<String> = viewModelStores.keys.iterator()
        while (viewModelStoreIterator.hasNext()) {
            sb.append(viewModelStoreIterator.next())
            if (viewModelStoreIterator.hasNext()) {
                sb.append(", ")
            }
        }
        sb.append(')')
        return sb.toString()
    }

    actual companion object {
        private val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer { MRouterControllerViewModel() }
        }

        actual fun getInstance(viewModelStore: ViewModelStore): MRouterControllerViewModel {
            val viewModelProvider = ViewModelProvider.create(viewModelStore, FACTORY)
            return viewModelProvider.get()
        }
    }
}

class DefaultViewModelProviderFactory:ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return super.create(modelClass, extras)
    }
}