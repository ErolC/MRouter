package com.erolc.mrouter.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import kotlin.reflect.KClass

interface MRouterViewModelStoreProvider {
    fun getViewModelStore(entryId: String): ViewModelStore
}

internal expect class MRouterControllerViewModel : ViewModel, MRouterViewModelStoreProvider {
    fun clear(entryId: String)

    companion object {
        fun getInstance(viewModelStore: ViewModelStore): MRouterControllerViewModel
    }
}


inline fun <reified T : Any> getKClassForGenericType(): KClass<T> = T::class

/**
 * 用于构造具有空构造函数的ViewModel，如果需要更加强大的构造方式可以使用其他第三方库，比如koin
 * ```
 * val vm = viewModel(::TestViewModel)
 * ```
 */
@Composable
inline fun <reified VM : ViewModel> viewModel(
    noinline block: () -> VM,
    key: String? = null
): VM = viewModelImpl(getKClassForGenericType(), emptyBlock = block, key = key)

/**
 * 用于构造有且只有[SavedStateHandle]作为参数的构造函数的ViewModel
 * ```
 * val vm = viewModel(::TestViewModel)
 * ```
 */
@Composable
inline fun <reified VM : ViewModel> viewModel(
    noinline block: (SavedStateHandle) -> VM,
    key: String? = null
): VM = viewModelImpl(getKClassForGenericType(), block = block, key = key)

@Composable
fun <T : ViewModel> viewModelImpl(
    modelClass: KClass<T>,
    emptyBlock: (() -> T)? = null,
    block: ((SavedStateHandle) -> T)? = null,
    key: String? = null
): T {
    val viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    val extras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras as MutableCreationExtras
    } else {
        MutableCreationExtras()
    }
    block?.let { extras[SavedStateHandleCreateKey] = it }
        ?: emptyBlock?.let { extras[EmptyCreateKey] = it }
    return viewModelStoreOwner.createVM(modelClass, key, extras)

}

internal typealias EmptyConstructor = () -> ViewModel
internal typealias SSHConstructor = (SavedStateHandle) -> ViewModel

object EmptyCreateKey : CreationExtras.Key<EmptyConstructor>
object SavedStateHandleCreateKey : CreationExtras.Key<SSHConstructor>

/**
 * 用于构造简单的ViewModel的工厂
 */
private class SimpleViewModelFactory(owner: SavedStateRegistryOwner) :
    AbstractSavedStateViewModelFactory(owner, null) {
    override fun <VM : ViewModel> create(modelClass: KClass<VM>, extras: CreationExtras): VM {
        return extras[EmptyCreateKey]?.invoke() as? VM
            ?: extras[SavedStateHandleCreateKey]?.invoke(extras.createSavedStateHandle()) as? VM
            ?: super.create(modelClass, extras)
    }
}

@Composable
fun <VM : ViewModel> ViewModelStoreOwner.createVM(
    modelClass: KClass<VM>,
    key: String?,
    extras: CreationExtras
): VM {
    val owner = LocalLifecycleOwner.current as SavedStateRegistryOwner
    val provider =
        ViewModelProvider.create(this.viewModelStore, SimpleViewModelFactory(owner), extras)
    return if (key != null) {
        provider[key, modelClass]
    } else {
        provider[modelClass]
    }
}




