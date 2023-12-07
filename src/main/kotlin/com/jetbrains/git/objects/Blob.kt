package com.jetbrains.git.objects

class Blob internal constructor(internal val data: String, private val hash: String) : Hashable {
    override fun hash() = hash

}