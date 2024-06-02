package com.erolc.mrouter.lifecycle

import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import androidx.core.bundle.Bundle
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.erolc.mrouter.platform.randomUUID
import kotlin.reflect.KClass


actual class LifecycleOwnerDelegate private constructor(
    private val viewModelStoreProvider: MRouterViewModelStoreProvider?,
    private var hostLifecycleState: Lifecycle.State = Lifecycle.State.CREATED,
    private val immutableArgs: Bundle?,
    actual val id: String = randomUUID()
) : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory {
    private val _lifecycle = LifecycleRegistry(this)

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private var savedStateRegistryAttached = false


    private val savedState: Bundle? = null


    actual val arguments: Bundle?
        get() = if (immutableArgs == null) {
            null
        } else {
            Bundle(immutableArgs)
        }

    actual constructor(delegate: LifecycleOwnerDelegate, arguments: Bundle?) : this(
        delegate.viewModelStoreProvider,
        delegate.hostLifecycleState,
        arguments,
        delegate.id
    ){
        hostLifecycleState =
            if (delegate.hostLifecycleState == Lifecycle.State.DESTROYED)
                Lifecycle.State.INITIALIZED
            else
                delegate.hostLifecycleState
        maxLifecycle = delegate.maxLifecycle
    }

    companion object {
        fun create(
            viewModelStoreProvider: MRouterViewModelStoreProvider?,
            hostLifecycleState: Lifecycle.State = Lifecycle.State.CREATED,
            immutableArgs: Bundle?,
            id: String = randomUUID()
        ) = LifecycleOwnerDelegate(
            viewModelStoreProvider,
            hostLifecycleState,
            immutableArgs,
            id
        )
    }

    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @set:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    actual var maxLifecycle: Lifecycle.State = Lifecycle.State.INITIALIZED
        set(maxState) {
            field = maxState
            updateState()
        }

    actual override val lifecycle: Lifecycle
        get() = _lifecycle
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry


    actual fun handleLifecycleEvent(event: Lifecycle.Event) {
        hostLifecycleState = event.targetState
        updateState()
    }

    override val defaultViewModelProviderFactory = object : ViewModelProvider.Factory {

    }

    override val defaultViewModelCreationExtras: CreationExtras
        get() {
            val extras = MutableCreationExtras()
            extras[SAVED_STATE_REGISTRY_OWNER_KEY] = this
            extras[VIEW_MODEL_STORE_OWNER_KEY] = this
            arguments?.let { args ->
                extras[DEFAULT_ARGS_KEY] = args
            }
            return extras
        }
    override val viewModelStore: ViewModelStore

        get() {
            check(savedStateRegistryAttached) {
                "You cannot access the PageEntry's ViewModels until it is added to " +
                        "the MRouter's back stack (i.e., the Lifecycle of the " +
                        "PageEntry reaches the CREATED state)."
            }
            check(lifecycle.currentState != Lifecycle.State.DESTROYED) {
                "You cannot access the PageEntry's ViewModels after the " +
                        "PageEntry is destroyed."
            }
            checkNotNull(viewModelStoreProvider) {
                "You must call setViewModelStore() on your MRouter before " +
                        "accessing the ViewModelStore of a route register."
            }
            return viewModelStoreProvider.getViewModelStore(id)
        }

    @get:MainThread
    actual val savedStateHandle: SavedStateHandle by lazy {
        check(savedStateRegistryAttached) {
            "You cannot access the PageEntry's SavedStateHandle until it is added to " +
                    "the RouteHost's back stack (i.e., the Lifecycle of the PageEntry " +
                    "reaches the CREATED state)."
        }
        check(lifecycle.currentState != Lifecycle.State.DESTROYED) {
            "You cannot access the PageEntry's SavedStateHandle after the " +
                    "PageEntry is destroyed."
        }
        ViewModelProvider.create(
            this, RouteResultSavedStateFactory(this)
        )[SavedStateViewModel::class].handle
    }

    private class RouteResultSavedStateFactory(
        owner: SavedStateRegistryOwner
    ) : AbstractSavedStateViewModelFactory(owner, null) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: KClass<T>,
            handle: SavedStateHandle
        ): T {
            return SavedStateViewModel(handle) as T
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    actual fun updateState() {
        if (!savedStateRegistryAttached) {
            savedStateRegistryController.performAttach()
            savedStateRegistryAttached = true
            if (viewModelStoreProvider != null) {
                enableSavedStateHandles()
            }
            // Perform the restore just once, the first time updateState() is called
            // and specifically *before* we move up the Lifecycle
            savedStateRegistryController.performRestore(savedState)
        }
//        loge("tag","$hostLifecycleState $maxLifecycle $this")
        if (hostLifecycleState.ordinal < maxLifecycle.ordinal) {
            _lifecycle.currentState = hostLifecycleState
        } else {
            _lifecycle.currentState = maxLifecycle
        }
    }

    private class SavedStateViewModel(val handle: SavedStateHandle) : ViewModel()
}

actual fun createLifecycleOwnerDelegate(
    viewModelStoreProvider: MRouterViewModelStoreProvider?,
    hostLifecycleState: Lifecycle.State,
    immutableArgs: Bundle?
): LifecycleOwnerDelegate =
    LifecycleOwnerDelegate.create(viewModelStoreProvider,hostLifecycleState,immutableArgs)