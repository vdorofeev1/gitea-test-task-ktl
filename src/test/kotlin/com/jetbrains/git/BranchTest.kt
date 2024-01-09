package com.jetbrains.git

import com.jetbrains.git.Git.Companion.MASTER
import com.jetbrains.git.objects.Tree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith


class BranchTest: GitTest() {
    @Test
    fun branchTest() {
        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)
        assertEquals(git.head.name, MASTER)
        assertEquals(git.head.getCommit(), commit)

        val branch1 = git.branch("branch")
        assertFailsWith<Exception> { git.branch("branch") }
    }

    @Test
    fun checkoutTest() {
        assertEquals(git.head.name, MASTER)
        val branch1 = git.branch("branch1")
        val branch2 = git.branch("branch2")

        assertEquals(git.head.name, MASTER)

        git.checkout("branch1")
        assertEquals(git.head, branch1)

        git.checkout("branch2")
        assertEquals(git.head, branch2)

        git.checkout(MASTER)
        assertEquals(git.head.name, MASTER)

        assertFailsWith<Exception> { git.checkout("wrong") }

    }

    @Test
    fun checkoutCommitsTest() {
        val tree1 = git.tree()
        val blob1 = git.blob("data1")
        tree1.add(blob1)
        val commit1 = git.commit(tree1, defaultMessage, defaultAuthor)
        assertEquals(git.head.getCommit(), commit1)

        val testBranch = git.branch("test")
        git.checkout("test")
        val tree2 = git.tree()
        tree2.add(tree1)
        tree2.add(git.blob("data2"))

        val commit2 = git.commit(tree2, defaultMessage, defaultAuthor)
        assertEquals(git.head.getCommit(), commit2)

        git.checkout(MASTER)
        assertEquals(git.head.getCommit(), commit1)

        val commit3 = git.commit(Tree(), defaultMessage, defaultAuthor)
        assertEquals(git.head.getCommit(), commit3)

    }

}