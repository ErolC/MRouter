package com.erolc.mrouter.lifecycle

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner


fun Lifecycle.addEventObserver(body: (source: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    addObserver(LifecycleEventObserver { source, event ->
        body(source, event)
    })
}

expect class LifecycleOwnerDelegate :
    LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory {
    constructor(viewModelStoreProvider: MRouterViewModelStoreProvider? = null,hostLifecycleState: Lifecycle.State)

    override val lifecycle: Lifecycle

    val id: String

    val savedStateHandle: SavedStateHandle

    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @set:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    var maxLifecycle: Lifecycle.State

    /**
     * Update the state to be the lower of the two constraints:
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun updateState()

    fun handleLifecycleEvent(event: Lifecycle.Event)
}