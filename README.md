
# Git Library
This is a simple Git library implemented in Kotlin, providing basic Git functionality. The library includes three main entities: Blob, Tree, and Commit. The implementation aims to mimic the essential features of Git, with a focus on simplicity and avoiding data redundancy.

## Entities
### Blob
The Blob represents a piece of data stored as a string. It includes a SHA-1 hash for unique identification.
```kotlin
val git = Git()
val data = "some data"
val blob = git.blob(data)
prinln(blob.hash())  
```

### Tree
The Tree acts as a container for a collection of named blobs or other trees. It has a SHA-1 hash that uniquely identifies the tree. Trees can be modified by adding or removing objects, and once locked(see below in Commit), no further modifications are allowed.
```kotlin
val tree = git.tree()
tree.add(blob)
tree.remove(blob)

val anotherTree = git.tree()
tree.add(anotherTree)
tree.remove(anotherTree)
```

### Commit
The Commit serves as a pointer to the main Tree object and stores metadata related to the commit. Metadata includes the author, commit message, commit time, and the SHA-1 hash of the commit itself. Commits are stored chronologically, allowing tracking of their relationships.
```kotlin
val commit = git.commit(tree, "message", "author")
val anotherBlob = git.blob("another data")
tree.add(anotherBlob) // error

anotherTree.add(anotherBlob)
val anotherCommit = git.commit(anotherTree, "another message", "author")

println(anotherCommit.parent == commit) // true
```

### Branch
The Branch serves as a pointer to the last commit, adding the possibility to create a new sequence of commits. Users can switch between branches to manage different lines of development. When instance of Git is created, `head` is set to the `Branch` with name `"master"`
```kotlin
println(git.getHead().getName() == "master") // true

val branch = git.branch("test")

println(git.getHead().getName() == "test") // false
```

## Library Features
### Create New Commits 
The library allows the creation of new commits by specifying a tree, commit message, and author.
```kotlin
val commit = git.commit(tree, message, author)
```

### List Commits 
The library provides a log function to list commits based on optional filtering criteria such as hash, message, and author.
```kotlin
git.log(hash = hash, message = message, author = author)
```

### Search for Specific Commit 
The library includes functions (`findByHash`, `findByMessage`, `findByAuthor`) to search for a specific commit based on hash, message, or author.
```kotlin
val commit1 = git.findByHash(hash)
val commit2 = git.findByMesssage(message)
val commit3 = git.findByAuthor(author)
```

### Switch to another branch
The library allows to switch between branches using their names
```kotlin
val branch = git.branch("test")
git.checkout("test")

println(git.getHead() == branch) // true
```



