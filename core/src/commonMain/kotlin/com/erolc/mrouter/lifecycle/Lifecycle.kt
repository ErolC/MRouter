package com.erolc.mrouter.lifecycle

import androidx.annotation.RestrictTo
import androidx.core.bundle.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner


fun Lifecycle.addEventObserver(body: (source: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    addObserver(LifecycleEventObserver { source, event ->
        body(source, event)
    })
}

expect class LifecycleOwnerDelegate :
    LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory {
    constructor(
        viewModelStoreProvider: MRouterViewModelStoreProvider? = null,
        hostLifecycleState: Lifecycle.State,
        args: Bundle? = null
    )

    override val lifecycle: Lifecycle

    val id: String
    val arguments:Bundle?

    val savedStateHandle: SavedStateHandle

    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @set:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    var maxLifecycle: Lifecycle.State

    fun resetLifecycle()

    /**
     * Update the state to be the lower of the two constraints:
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun updateState()

    fun handleLifecycleEvent(event: Lifecycle.Event)
}