package com.jetbrains.git

import com.jetbrains.git.Git.Companion.MASTER
import com.jetbrains.git.objects.Tree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class BranchTest: GitTest() {
    @Test
    fun branchTest() {
        val commit = git.commit(defaultTree, defaultMessage, defaultAuthor)
        assertEquals(git.head.name, MASTER)
        assertEquals(git.head.getCommit(), commit)

        val testBranch = git.branch("test")
        git.checkout("test")
        assertEquals(git.head, testBranch)
        assertEquals(git.head.getCommit(), commit)

        git.checkout(MASTER)
        assertEquals(git.head.name, MASTER)
        assertEquals(git.head.getCommit(), commit)
    }

    @Test
    fun checkoutTest() {
        val tree1 = git.tree()
        val blob1 = git.blob("data1")
        tree1.add(blob1)
        val commit1 = git.commit(tree1, "", "")

        val testBranch = git.branch("test")
        git.checkout("test")

        val tree2 = git.tree()
        tree2.add(tree1)
        tree2.add(git.blob("data2"))

        val commit2 = git.commit(tree2, "", "")
        assertEquals(git.head.getCommit(), commit2)
        assertEquals(git.head, testBranch)

        git.checkout(MASTER)
        assertEquals(git.head.getCommit(), commit1)

        val commit3 = git.commit(Tree(), "", "")
        assertEquals(git.head.getCommit(), commit3)
    }
}