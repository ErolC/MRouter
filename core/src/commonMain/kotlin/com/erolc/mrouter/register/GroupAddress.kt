package com.erolc.mrouter.register

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import com.erolc.mrouter.model.PageConfig
import com.erolc.mrouter.scope.GroupScope

data class GroupAddress(override val path: String, override val config: PageConfig,override val content: @Composable () -> Unit):Address(path,config,content){

    @Composable
    fun Content(){
    }
}