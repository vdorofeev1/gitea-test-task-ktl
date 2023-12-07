package com.jetbrains.git

import com.jetbrains.git.objects.Blob
import com.jetbrains.git.objects.Commit
import com.jetbrains.git.objects.Tree
import org.apache.commons.codec.digest.DigestUtils


class Git {
    internal var head: Commit?
    internal var blobs: MutableMap<String, Blob>

    init {
        head = null
        blobs = mutableMapOf()
    }

    fun commit(tree: Tree, message: String, author: String): Commit {
        require(tree.isNotLocked()) { "Tree is locked!" }
        return Commit(tree, message, author, head).also {
            tree.lock()
            head = it
        }
    }

    fun blob(data: String): Blob {
        val hash = sha1(data)
        return if (blobs.containsKey(hash)) blobs.getValue(hash)
        else Blob(data, hash).also { blobs[hash] = it }
    }

    fun tree() = Tree()

    fun log(hash: String = "", message: String = "", author: String = "") {
        var current = head
        while (current != null) {
            if (current.hash().startsWith(hash) &&
                current.message.contains(message) &&
                current.author.contains(author)
            ) {
                current.printContents()
                current = current.parent
            }
        }
    }

    fun findByHash(hash: String): Commit {
        var current = head
        while (current != null && current.hash() != hash) {
            current = current.parent
        }
        requireNotNull(current) { "Commit with hash '$hash' not found." }
        return current
    }

    fun findByMessage(message: String): Commit {
        var current = head
        while (current != null && current.message != message) {
            current = current.parent
        }
        requireNotNull(current) { "Commit with message '$message' not found." }
        return current
    }

    fun findByAuthor(author: String): Commit {
        var current = head
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