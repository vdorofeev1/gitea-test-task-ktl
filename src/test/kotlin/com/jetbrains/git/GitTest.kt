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
        val commit1 = git.commit(defaultTree, defaultMessage, defaultAuthor)
        val hash1 = commit1.hash()
        val commit2 = git.commit(git.tree(), "message2", "author2")
        val hash2 = commit2.hash()

        assertEquals(commit1, git.findByHash(hash1))
        assertEquals(commit2, git.findByHash(hash2))
        assertFailsWith<Exception> { git.findByHash("") }
    }

    @Test
    fun findByMessageTest() {
        val commit1 = git.commit(defaultTree, "commit1", defaultAuthor)
        val commit2 = git.commit(git.tree(), "commit2", defaultAuthor)

        assertEquals(commit1, git.findByMessage("commit1"))
        assertEquals(commit2, git.findByMessage("commit2"))
        assertFailsWith<Exception> { git.findByMessage("") }
    }

    @Test
    fun findByAuthorTest() {
        val commit1 = git.commit(defaultTree, defaultMessage, "author1")
        val commit2 = git.commit(git.tree(), defaultMessage, "author2")

        assertEquals(commit1, git.findByAuthor("author1"))
        assertEquals(commit2, git.findByAuthor("author2"))
        assertFailsWith<Exception> { git.findByAuthor("") }
    }

    @Test
    fun hashTest() {
        assertEquals(Git.sha1(defaultData), DigestUtils.sha1Hex(defaultData))
        //             ¯\_(ツ)_/¯
    }
}