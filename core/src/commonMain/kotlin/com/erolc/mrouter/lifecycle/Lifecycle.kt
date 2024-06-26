package com.erolc.mrouter.lifecycle

import androidx.annotation.RestrictTo
import androidx.core.bundle.Bundle
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner

/**
 * 添加事件监听
 * @param body 事件回调
 */
fun Lifecycle.addEventObserver(body: (source: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    addObserver(LifecycleEventObserver { source, event ->
        body(source, event)
    })
}

/**
 * 生命周期拥有者的代理，
 */
expect class LifecycleOwnerDelegate :
    LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory {
    internal constructor(delegate: LifecycleOwnerDelegate, arguments: Bundle?)

    override val lifecycle: Lifecycle

    val id: String
    val arguments: Bundle?

    val savedStateHandle: SavedStateHandle

    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @set:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    var maxLifecycle: Lifecycle.State

    override val viewModelStore: ViewModelStore

    override val savedStateRegistry: SavedStateRegistry

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory

    override val defaultViewModelCreationExtras: CreationExtras

    /**
     * Update the state to be the lower of the two constraints:
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun updateState()

    fun handleLifecycleEvent(event: Lifecycle.Event)
}

internal expect fun createLifecycleOwnerDelegate(
    viewModelStoreProvider: MRouterViewModelStoreProvider?,
    hostLifecycleState: Lifecycle.State = Lifecycle.State.CREATED,
    immutableArgs: Bundle?
):LifecycleOwnerDelegate