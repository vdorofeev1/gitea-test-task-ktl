package com.jetbrains.git.objects

import com.jetbrains.git.Git
import java.time.LocalDateTime

class Commit internal constructor(val mainTree: Tree,
              internal val message: String,
              internal val author: String,
              internal val parent: Commit?) : Hashable {

    private val hash: String
    private val commitTime: LocalDateTime

    init {
        hash = calculateHash()
        commitTime = LocalDateTime.now()
    }

    fun printContents() {
        println(this.toString())
    }

    private fun calculateHash(): String = Git.sha1(mainTree.hash() + message + author + commitTime)

    override fun hash(): String = hash
    override fun toString(): String {
        return "Commit(hash='$hash', author='$author', message='$message', parent=${parent?.hash() ?: "null"})"
    }

}