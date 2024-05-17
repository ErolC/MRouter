package com.erolc.mrouter.platform

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.savedstate.SavedStateRegistryOwner
import com.erolc.mrouter.lifecycle.SimpleViewModelFactory

internal actual fun getViewModelProvider(
    store: ViewModelStore,
    owner: SavedStateRegistryOwner,
    extras: CreationExtras
) = ViewModelProvider.create(store, SimpleViewModelFactory(owner), extras)