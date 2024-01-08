package com.jetbrains.git

import com.jetbrains.git.objects.Commit

class Branch internal constructor(
            internal val name: String,
            private var commit: Commit?) {
    fun setCommit(commit: Commit?) {
        this.commit = commit
    }

    fun getCommit() = commit

    fun getName() = name
}