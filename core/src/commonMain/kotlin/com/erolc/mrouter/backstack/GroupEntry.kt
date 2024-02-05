package com.erolc.mrouter.backstack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.erolc.lifecycle.Lifecycle
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.scope.GroupScope


class GroupEntry internal constructor(scope: GroupScope, address: Address) :
    StackEntry(scope, address) {
    private val stacks = mutableListOf<BackStack>()

    @Composable
    internal fun getBackStack(key: String) = BackStack(key).apply {
        stacks.add(this)
    }.backStack.collectAsState()


    fun addEntry(key: String, entry: StackEntry) {
        stacks.find { it.name == key }?.addEntry(entry)
    }
}

