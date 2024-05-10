package com.erolc.mrouter.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import java.lang.ref.WeakReference
import java.util.UUID.randomUUID

@Composable
actual fun LifecycleOwnerDelegate.LocalOwnersProvider(
    saveableStateHolder: SaveableStateHolder,
    pageScope: PageScope,
    content: @Composable () -> Unit
){
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides this,
        LocalPageScope provides pageScope,
        LocalLifecycleOwner provides this,
        // TODO: LocalSavedStateRegistryOwner provides this
    ) {
        saveableStateHolder.SaveableStateProvider(content)
    }
}


@Composable
private fun SaveableStateHolder.SaveableStateProvider(content: @Composable () -> Unit) {
    val viewModel = viewModel(
        // TODO investigate why inline with refined type triggers
        //  "Compilation failed: Symbol for ... is unbound"
        //  https://github.com/JetBrains/compose-multiplatform/issues/3147
        PageEntryIdViewModel::class,
        factory = viewModelFactory {
            initializer { PageEntryIdViewModel(createSavedStateHandle()) }
        }
    )
    // Stash a reference to the SaveableStateHolder in the ViewModel so that
    // it is available when the ViewModel is cleared, marking the permanent removal of this
    // NavBackStackEntry from the back stack. Which, because of animations,
    // only happens after this leaves composition. Which means we can't rely on
    // DisposableEffect to clean up this reference (as it'll be cleaned up too early)
    viewModel.saveableStateHolderRef = WeakReference(this)
    SaveableStateProvider(viewModel.id, content)
}


internal class PageEntryIdViewModel(handle: SavedStateHandle) : ViewModel() {

    private val IdKey = "SaveableStateHolder_PageEntryKey"

    // we create our own id for each back stack entry to support multiple entries of the same
    // destination. this id will be restored by SavedStateHandle
    val id: String = handle.get<String>(IdKey) ?: randomUUID().toString().also {
        handle.set(IdKey, it)
    }

    lateinit var saveableStateHolderRef: WeakReference<SaveableStateHolder>

    // onCleared will be called on the entries removed from the back stack. here we notify
    // SaveableStateProvider that we should remove any state is had associated with this
    // destination as it is no longer needed.
    override fun onCleared() {
        super.onCleared()
        saveableStateHolderRef.get()?.removeState(id)
        saveableStateHolderRef.clear()
    }
}


