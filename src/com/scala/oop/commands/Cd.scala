package com.scala.oop.commands

import com.scala.oop.files.{DirEntry, Directory}
import com.scala.oop.filesystem.State

import scala.annotation.tailrec

class Cd(dir: String) extends Command {

    override def apply(state: State): State = {

        val root = state.root
        val wd = state.wd

        val absolutePath =
            if (dir.startsWith(Directory.SEPARATOR)) dir
            else if (wd.isRoot) wd.path + dir
            else wd.path + Directory.SEPARATOR + dir

        val destinationDirectory = doFindEntry(root, absolutePath)

        if (destinationDirectory == null || !destinationDirectory.isDirectory)
            state.setMessage(dir + ": no such directory!")
        else State(root, destinationDirectory.asDirectory)


    }

    def doFindEntry(root: Directory, path: String): DirEntry = {

        @tailrec
        def findEntryHelper(currentDirectory: Directory, path: List[String]): DirEntry = {
            if (path.isEmpty || path.head.isEmpty) currentDirectory
            else if(path.tail.isEmpty) currentDirectory.findEntry(path.head)
            else {
                val nextDir = currentDirectory.findEntry(path.head).asDirectory
                if (nextDir == null || !nextDir.isDirectory) null
                else findEntryHelper(nextDir, path.tail)
            }
        }

        @tailrec
        def collapseRelativeTokes(path: List[String], result: List[String]): List[String] = {
            if (path.isEmpty) result
            else if (".".equals(path.head)) collapseRelativeTokes(path.tail, result)
            else if ("..".equals(path.head)) {
                if (result.isEmpty) null
                else collapseRelativeTokes(path.tail, result.init)
            }   else collapseRelativeTokes(path.tail, result :+ path.head)
        }

        val tokens = path.substring(1).split(Directory.SEPARATOR).toList

        val newTokens: List[String] = collapseRelativeTokes(tokens, List())


        findEntryHelper(root, newTokens)
    }
}
