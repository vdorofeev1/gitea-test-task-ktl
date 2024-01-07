package com.jetbrains.git

import com.jetbrains.git.objects.Blob
import com.jetbrains.git.objects.Commit
import com.jetbrains.git.objects.Tree
import org.apache.commons.codec.digest.DigestUtils


class Git {
    companion object {
        internal fun sha1(data: String): String {
            return DigestUtils.sha1Hex(data)
        }

        var MASTER = "master"
    }

    internal var head: Branch = Branch(MASTER, null)
    internal var blobs: MutableMap<String, Blob> = mutableMapOf()
    internal var branches: MutableMap<String, Branch> = mutableMapOf()

    init {
        branches[MASTER] = head
    }

    fun commit(tree: Tree, message: String, author: String): Commit {
        require(tree.isNotLocked()) { "Tree is locked!" }
        return Commit(tree, message, author, head.getCommit()).also {
            tree.lock()
            head.setCommit(it)
        }
    }

    fun blob(data: String): Blob {
        val hash = sha1(data)
        return if (blobs.containsKey(hash)) blobs.getValue(hash)
        else Blob(data, hash).also { blobs[hash] = it }
    }

    fun tree() = Tree()

    fun branch(name: String): Branch {
        require(!branches.containsKey(name)) { "Branch with name $name already exists." }

        return Branch(name, head.getCommit()).also { branches[name] = it }
    }

    fun checkout(name: String) {
        require(branches.containsKey(name)) { "No branch with name '$name'." }

        val branch = branches.getValue(name)
        head = branch
    }

    fun log(hash: String = "", message: String = "", author: String = "") {
        var current = head.getCommit()
        while (current != null) {
            if (current.hash().startsWith(hash) &&
                current.message.contains(message) &&
                current.author.contains(author)
            ) {
                current.printContents()
            }
            current = current.parent
        }
    }

    fun findByHash(hash: String): Commit {
        var current = head.getCommit()
        while (current != null && current.hash() != hash) {
            current = current.parent
        }
        requireNotNull(current) { "Commit with hash '$hash' not found." }
        return current
    }

    fun findByMessage(message: String): Commit {
        var current = head.getCommit()
        while (current != null && current.message != message) {
            current = current.parent
        }
        requireNotNull(current) { "Commit with message '$message' not found." }
        return current
    }

    fun findByAuthor(author: String): Commit {
        var current = head.getCommit()
        while (current != null && current.author != author) {
            current = current.parent
        }
        requireNotNull(current) { "Commit with author '$author' not found." }
        return current
    }
}