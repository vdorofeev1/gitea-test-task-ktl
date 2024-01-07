package com.jetbrains.git

import com.jetbrains.git.objects.Blob
import com.jetbrains.git.objects.Commit
import com.jetbrains.git.objects.Tree
import org.apache.commons.codec.digest.DigestUtils


class Git {

    internal var head: Branch = Branch("master", null)
    internal var blobs: MutableMap<String, Blob> = mutableMapOf()
    internal var branches: MutableList<Branch> = mutableListOf()

    init {
        branches.add(head)
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

    fun branch(name: String) = Branch(name, head.getCommit()).also { branches.add(it) }

    fun checkout(name: String) {
        val branch = branches.find { it.name == name }
        requireNotNull(branch) { "No branch with given name." }
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
        requireNotNull(current) { "Commit with message '$author' not found." }
        return current
    }

    companion object {
        internal fun sha1(data: String): String {
            return DigestUtils.sha1Hex(data)
        }
    }
}