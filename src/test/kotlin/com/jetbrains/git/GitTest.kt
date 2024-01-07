package com.jetbrains.git

import com.github.stefanbirkner.systemlambda.SystemLambda
import com.jetbrains.git.objects.Tree
import org.apache.commons.codec.digest.DigestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

open class GitTest {
    internal val defaultData = "data"
    internal val defaultMessage = "message"
    internal val defaultAuthor = "author"
    internal val defaultTree = Tree()
    var git = Git()

    @BeforeEach
    fun gitInit() {
        git = Git()
    }

    @Test
    fun logTest() {
        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)
        val expectedOutput = "Hash: ${commit.hash()}\n" +
                "Message: ${commit.message}\n" +
                "Author: ${commit.author}\n" +
                "Main Tree: ${commit.mainTree.hash()}\n" +
                "Parent: null\n\n"
        val actualOutput = SystemLambda.tapSystemOut { git.log(hash = "0f34c", message = "mes", author = "a") }
        assertEquals(expectedOutput, actualOutput)

    }

    @Test
    fun searchByHashTest() {
        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)
        val hash = commit.hash()

        assertEquals(commit, git.findByHash(hash))
        assertFailsWith<Exception> { git.findByHash("") }
    }

    @Test
    fun searchByMessageTest() {
        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)

        assertEquals(commit, git.findByMessage(defaultMessage))
        assertFailsWith<Exception> { git.findByMessage("") }
    }

    @Test
    fun searchByAuthorTest() {
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