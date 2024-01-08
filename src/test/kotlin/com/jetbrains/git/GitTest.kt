package com.jetbrains.git

import com.github.stefanbirkner.systemlambda.SystemLambda
import com.jetbrains.git.objects.Tree
import org.apache.commons.codec.digest.DigestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
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

    fun logHelper(git: Git) {

    }

    @Test
    fun logTest() {
        val commit1 = git.commit(defaultTree, "commit1", defaultAuthor)
        val commit2 = git.commit(git.tree(), "commit2", defaultAuthor)
        var expectedOutput = commit1.toString() + "\n"
        var actualOutput = SystemLambda.tapSystemOut { git.log(hash = "b1ef6", message = "com", author = "a") }
        assertEquals(expectedOutput, actualOutput)

        actualOutput = SystemLambda.tapSystemOut { git.log(hash = "b1ef6") }
        assertEquals(expectedOutput, actualOutput)

        expectedOutput = commit2.toString() + "\n" + commit1.toString() + "\n"
        actualOutput = SystemLambda.tapSystemOut { git.log(message = "commit") }
        assertEquals(expectedOutput, actualOutput)

        expectedOutput = commit2.toString() + "\n"
        actualOutput = SystemLambda.tapSystemOut { git.log(message = "commit2") }
        assertEquals(expectedOutput, actualOutput)

    }

    @Test
    fun findByHashTest() {
        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)
        val hash = commit.hash()

        assertEquals(commit, git.findByHash(hash))
        assertFailsWith<Exception> { git.findByHash("") }
    }

    @Test
    fun findByMessageTest() {
        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)

        assertEquals(commit, git.findByMessage(defaultMessage))
        assertFailsWith<Exception> { git.findByMessage("") }
    }

    @Test
    fun findByAuthorTest() {
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