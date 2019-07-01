package com.scala.oop.files

import com.scala.oop.filesystem.FilesystemException

import scala.annotation.tailrec

class Directory(override val parentPath: String, override val name: String, val contents: List[DirEntry])
    extends DirEntry(parentPath, name) {

    def findEntry(entryName: String): DirEntry = {

        @tailrec
        def findEntryHelper(name: String, contentList: List[DirEntry]): DirEntry = {
            if (contentList.isEmpty) null
            else if (contentList.head.name.equals(name)) contentList.head
            else findEntryHelper(name, contentList.tail)
        }

        findEntryHelper(entryName, contents)

    }

    def hasEntry(name: String): Boolean =
        findEntry(name) != null

    def getAllFolders: List[String] =
        path.substring(1).split(Directory.SEPARATOR).toList.filter(dir => !dir.isEmpty)

    def findDescendant(path: List[String]): Directory =
        if (path.isEmpty) this
        else findEntry(path.head).asDirectory.findDescendant(path.tail)

    def addEntry(newEntry: DirEntry): Directory =
        new Directory(parentPath, name, contents :+ newEntry)


    def replaceEntry(entryName: String, newEntry: DirEntry): Directory =
        new Directory(parentPath, name, contents.filter(entry => !entry.name.equals(entryName)) :+ newEntry)

    def asDirectory: Directory = this

    def asFile: File = throw new FilesystemException("Directories cannot be converted to files!")

    def isRoot: Boolean = parentPath.isEmpty

    override def isDirectory: Boolean = true
    override def isFile: Boolean = false

    override def getType: String = "Directory"

}

object Directory {

    val SEPARATOR = "/"
    val ROOT_PATH = "/"

    def ROOT: Directory = Directory.empty("", "")

    def empty(parentPath: String, name: String): Directory =
        new Directory(parentPath, name, List())

}