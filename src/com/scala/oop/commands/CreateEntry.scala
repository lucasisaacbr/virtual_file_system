package com.scala.oop.commands

import com.scala.oop.files.{DirEntry, Directory}
import com.scala.oop.filesystem.State

abstract class CreateEntry(name: String) extends Command {

    override def apply(state: State): State = {
        val wd = state.wd
        if (wd.hasEntry(name)) {
            state.setMessage("Entry " + name + " already exists.")
        } else if (name.contains(Directory.SEPARATOR)) {
            state.setMessage(name + "must not have separators!")
        } else if (checkIllegal(name)){
            state.setMessage(name + " is a illegal name!")
        } else {
            doCreateEntry(state, name)
        }
    }

    def checkIllegal(name: String): Boolean = {
        name.contains(".")
    }

    def doCreateEntry(state: State, name: String): State = {

        def updateStructure(currentDirectory: Directory, path: List[String], newEntry: DirEntry): Directory = {
            if (path.isEmpty) currentDirectory.addEntry(newEntry)
            else {
                val oldEntry = currentDirectory.findEntry(path.head).asDirectory
                currentDirectory.replaceEntry(oldEntry.name, updateStructure(oldEntry, path.tail, newEntry))
            }
        }


        val wd = state.wd

        val allDirsInPath = wd.getAllFolders

        //    val newDir = Directory.empty(wd.path, name)

        val newEntry: DirEntry = createSpecificEntry(state)

        val newRoot = updateStructure(state.root, allDirsInPath, newEntry)

        val newWd = newRoot.findDescendant(allDirsInPath)

        State(newRoot, newWd)

    }

    def createSpecificEntry(state: State): DirEntry
}
