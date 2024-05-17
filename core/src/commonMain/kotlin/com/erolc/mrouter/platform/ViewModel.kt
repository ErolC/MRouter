package com.erolc.mrouter.platform

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.savedstate.SavedStateRegistryOwner

internal expect fun getViewModelProvider(
    store: ViewModelStore,
    owner: SavedStateRegistryOwner,
    extras: CreationExtras
):ViewModelProvider