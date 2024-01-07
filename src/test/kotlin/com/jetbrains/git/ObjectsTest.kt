package com.jetbrains.git

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith


class ObjectsTest: GitTest() {

    @Test
    fun blobTest() {
        val blob1 = git.blob(defaultData)
        val blob2 = git.blob(defaultData)

        assertEquals(blob1, blob2)
        assertEquals(1, git.blobs.size)
    }

    @Test
    fun commitTest() {
        val initialCommit = git.commit(defaultTree, defaultMessage, defaultAuthor)

        assertNull(initialCommit.parent)
        assertEquals(initialCommit, git.head.getCommit())
        assert(initialCommit.mainTree.isLocked())

        assertFailsWith<Exception> { git.commit(defaultTree, defaultMessage, defaultAuthor) }
    }

    @Test
    fun commitHierarchyTest() {
        val firstTree = git.tree()
        val firstCommit = git.commit(firstTree, defaultMessage, defaultAuthor)

        val secondTree = git.tree()
        secondTree.add(firstTree)

        val secondCommit = git.commit(secondTree, defaultMessage, defaultAuthor)

        assertEquals(firstCommit, secondCommit.parent)
        assertEquals(secondCommit, git.head.getCommit())
    }

    @Test
    fun treeTest() {
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
        val blob1 = git.blob("data1")
        val blob2 = git.blob("data2")

        val tree = git.tree()
        tree.add(blob1)

        assertFailsWith<Exception> { tree.remove(blob2) }

        val commit = git.commit(tree, defaultMessage, defaultAuthor)

        assertFailsWith<Exception> { tree.add(blob2) }
        assertEquals(tree, commit.mainTree)

    }
}