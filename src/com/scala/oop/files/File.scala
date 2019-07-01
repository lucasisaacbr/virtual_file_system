package com.scala.oop.files

import com.scala.oop.filesystem.FilesystemException

class File(override val parentPath: String, override val name: String, contents: String)
    extends DirEntry(parentPath, name) {

    override def getType: String = "File"

    override def asDirectory: Directory =
        throw new FilesystemException("A File cannot be converted to a directory")

    def asFile: File = this

    override def isFile: Boolean = true
    override def isDirectory: Boolean = false

}

object File {
    def empty(parentPath: String, name: String): File = {
        new File(parentPath, name, "")
    }
}