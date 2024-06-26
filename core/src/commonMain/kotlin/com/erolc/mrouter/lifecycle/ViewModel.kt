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
import com.erolc.mrouter.platform.getViewModelProvider
import com.erolc.mrouter.platform.isAndroid
import kotlin.reflect.KClass

internal interface MRouterViewModelStoreProvider {
    fun getViewModelStore(entryId: String): ViewModelStore
}

internal expect class MRouterControllerViewModel : ViewModel, MRouterViewModelStoreProvider {
    fun clear(entryId: String)

    companion object {
        fun getInstance(viewModelStore: ViewModelStore): MRouterControllerViewModel
    }

    override fun getViewModelStore(entryId: String): ViewModelStore
}

/**
 * 包装获取泛型类型
 */
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

/**
 * 实际构造ViewModel的方法
 * @param modelClass ViewModel的KClass
 * @param emptyBlock 空构造函数
 * @param block 带有SaveStateHandle的构造函数
 * @param key ViewModel的键。
 */
@Composable
fun <T : ViewModel> viewModelImpl(
    modelClass: KClass<T>,
    emptyBlock: EmptyConstructor? = null,
    block: SSHConstructor? = null,
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
/**
 * 空构造函数的key
 */
internal object EmptyCreateKey : CreationExtras.Key<EmptyConstructor>

/**
 * 拥有SaveStateHandle构造函数的key
 */
internal object SavedStateHandleCreateKey : CreationExtras.Key<SSHConstructor>


/**
 * 用于构造简单的ViewModel的工厂
 */
@Composable
fun <VM : ViewModel> ViewModelStoreOwner.createVM(
    modelClass: KClass<VM>,
    key: String?,
    extras: CreationExtras
): VM {
    val owner = LocalLifecycleOwner.current as SavedStateRegistryOwner
    val provider = getViewModelProvider(viewModelStore, owner, extras)
    return if (key != null) {
        provider[key, modelClass]
    } else {
        provider[modelClass]
    }
}




