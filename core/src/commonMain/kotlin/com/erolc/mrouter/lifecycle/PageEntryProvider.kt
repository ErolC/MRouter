package com.erolc.mrouter.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.SaveableStateHolder
import com.erolc.mrouter.scope.PageScope

@Composable
expect fun LifecycleOwnerDelegate.LocalOwnersProvider(
    saveableStateHolder: SaveableStateHolder,
    pageScope: PageScope,
    content: @Composable () -> Unit
)
