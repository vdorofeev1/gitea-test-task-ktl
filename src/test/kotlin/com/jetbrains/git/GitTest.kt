package com.jetbrains.git

import org.apache.commons.codec.digest.DigestUtils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

import java.lang.Exception

import kotlin.test.assertFailsWith

import com.github.stefanbirkner.systemlambda.SystemLambda.*
import com.jetbrains.git.objects.Tree


class GitTest {
    private val defaultData = "data"
    private val defaultMessage = "message"
    private val defaultAuthor = "author"
    private val defaultTree = Tree()

    @Test
    fun blobCreationTest() {
        val git = Git()

        val blob1 = git.blob(defaultData)
        val blob2 = git.blob(defaultData)

        assertEquals(blob1, blob2)
        assertEquals(1, git.blobs.size)
    }

    @Test
    fun commitCreationTest() {
        val git = Git()

        val initialCommit = git.commit(defaultTree, defaultMessage, defaultAuthor)

        assertNull(initialCommit.parent)
        assertEquals(initialCommit, git.head.getCommit())
        assert(initialCommit.mainTree.isLocked())

        assertFailsWith<Exception> { git.commit(defaultTree, defaultMessage, defaultAuthor) }
    }

    @Test
    fun commitHierarchyTest() {
        val git = Git()

        val firstTree = git.tree()
        val firstCommit = git.commit(firstTree, defaultMessage, defaultAuthor)

        val secondTree = git.tree()
        secondTree.add(firstTree)

        val secondCommit = git.commit(secondTree, defaultMessage, defaultAuthor)

        assertEquals(firstCommit, secondCommit.parent)
        assertEquals(secondCommit, git.head.getCommit())
    }

    @Test
    fun treeCreationTest() {
        val git = Git()

        val tree = git.tree()
        val hashBefore = tree.hash()

        assertEquals(tree.getSize(), 0)

        val blob1 = git.blob(defaultData)
        tree.add(blob1)

        assertNotEquals(hashBefore, tree.hash())
        assertEquals(1, tree.getSize())
    }

    @Test
    fun treeRemoveTest() {
        val git = Git()

        val blob1 = git.blob("data1")
        val tree = git.tree()
        tree.add(blob1)
        val hash = tree.hash()

        val blob2 = git.blob("data2")
        tree.add(blob2)

        assertNotEquals(hash, tree.hash())

        tree.remove(blob2)

        assertEquals(hash, tree.hash())
    }

    @Test
    fun treeRemoveAndAddFailTest() {
        val git = Git()

        val blob1 = git.blob("data1")
        val blob2 = git.blob("data2")

        val tree = git.tree()
        tree.add(blob1)

        assertFailsWith<Exception> { tree.remove(blob2) }

        val commit = git.commit(tree, defaultMessage, defaultAuthor)

        assertFailsWith<Exception> { tree.add(blob2) }
        assertEquals(tree, commit.mainTree)

    }

    @Test
    fun logTest() {
        val git = Git()

        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)
        val expectedOutput = "Hash: ${commit.hash()}\n" +
                "Message: ${commit.message}\n" +
                "Author: ${commit.author}\n" +
                "Main Tree: ${commit.mainTree.hash()}\n" +
                "Parent: null\n\n"
        val actualOutput = tapSystemOut { git.log(hash = "0f34c", message = "mes", author = "a") }
        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    fun searchByHashTest() {
        val git = Git()

        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)
        val hash = commit.hash()

        assertEquals(commit, git.findByHash(hash))
        assertFailsWith<Exception> { git.findByHash("") }
    }

    @Test
    fun searchByMessageTest() {
        val git = Git()

        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)

        assertEquals(commit, git.findByMessage(defaultMessage))
        assertFailsWith<Exception> { git.findByMessage("") }
    }

    @Test
    fun searchByAuthorTest() {
        val git = Git()

        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)

        assertEquals(commit, git.findByAuthor(defaultAuthor))
        assertFailsWith<Exception> { git.findByAuthor("") }
    }

    @Test
    fun hashTest() {
        assertEquals(Git.sha1(defaultData), DigestUtils.sha1Hex(defaultData))
        //             ¯\_(ツ)_/¯
    }
}