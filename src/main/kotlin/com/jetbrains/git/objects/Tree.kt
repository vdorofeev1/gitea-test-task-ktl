package com.jetbrains.git.objects

import com.jetbrains.git.Git

class Tree internal constructor(): Hashable {
    internal val objects: ArrayList<Hashable> = arrayListOf()
    private var hash: String
    private var locked: Boolean

    init {
        hash = calculateHash()
        locked = false

    }

    fun add(obj: Hashable) {
        require(!locked) { "Tree is locked!" }

        objects.add(obj)
        hash = calculateHash()
    }

    fun remove(obj: Hashable) {
        require(!locked) { "Tree is locked!" }
        require(objects.contains(obj)) { ("Tree does not contain given object!") }

        objects.remove(obj)
        hash = calculateHash()
    }

    internal fun lock() {
        locked = true
    }

    fun getSize(): Int = objects.size

    fun isLocked(): Boolean = locked

    fun isNotLocked(): Boolean = !locked

    private fun calculateHash(): String = Git.sha1(objects.joinToString { it.hash() })

    override fun hash(): String = hash
}