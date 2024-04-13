package com.erolc.mrouter.model

/**
 * 一个共享组
 * @param start 开始页面的共享元素
 * @param end 结束页面的共享元素
 */
data class ShareElementGroup(val start: ShareElement, val end: ShareElement)

/**
 * 共享条目
 */
data class ShareEntry(val groups: List<ShareElementGroup>)