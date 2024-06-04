package com.erolc.mrouter.lifecycle

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.get
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.savedstate.SavedStateRegistryOwner
import kotlin.reflect.KClass

internal actual class MRouterControllerViewModel : ViewModel(), MRouterViewModelStoreProvider {

    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    actual fun clear(entryId: String) {
        // Clear and remove the entry's ViewModelStore
        val viewModelStore = viewModelStores.remove(entryId)
        viewModelStore?.clear()
    }

    override fun onCleared() {
        for (store in viewModelStores.values) {
            store.clear()
        }
        viewModelStores.clear()
    }

    actual override fun getViewModelStore(entryId: String): ViewModelStore {
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

internal class SimpleViewModelFactory(owner: SavedStateRegistryOwner) :
    AbstractSavedStateViewModelFactory(owner, null) {
    override fun <VM : ViewModel> create(modelClass: KClass<VM>, extras: CreationExtras): VM {
        return extras[EmptyCreateKey]?.invoke() as? VM
            ?: extras[SavedStateHandleCreateKey]?.invoke(extras.createSavedStateHandle()) as? VM
            ?: super.create(modelClass, extras)
    }

    override fun <T : ViewModel> create(
        key: String,
        modelClass: KClass<T>,
        handle: SavedStateHandle
    ): T {
        return super.create(key, modelClass, handle)
    }
}