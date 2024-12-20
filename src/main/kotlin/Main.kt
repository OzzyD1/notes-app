
import controllers.NoteAPI
import io.github.oshai.kotlinlogging.KotlinLogging
import models.Note
import persistence.JSONSerializer
import persistence.XMLSerializer
import utils.readNextDouble
import utils.readNextInt
import utils.readNextLine
import java.io.File
import java.lang.System.exit
import utils.validRange
import utils.validRangeDouble
import utils.isValidCategory

private val logger = KotlinLogging.logger {}
//private val noteAPI = NoteAPI(XMLSerializer(File("notes.xml")))
private val noteAPI = NoteAPI(JSONSerializer(File("notes.json")))

fun main() {
    runMenu()
}

fun mainMenu(): Int {
    print(""" 
        > ----------------------------------
         > |        NOTE KEEPER APP         |
         > ----------------------------------
         > | NOTE MENU                      |
         > |   1) Add a note                |
         > |   2) List all notes            |
         > |   3) Update a note             |
         > |   4) Delete a note             |
         > |   5) Archive a note            |
         > |   6) Search note (by desc)     |
         > ----------------------------------
         > |   20) Save notes               |
         > |   21) Load notes               |
         > ----------------------------------
         > |   0) Exit                      |
         > ---------------------------------- 
         >""".trimMargin(">"))
    return readNextInt(" > ==>>")
}

fun runMenu() {
    do {
        val option = mainMenu()
        when (option) {
            1 -> addNote()
            2 -> listNotes()
            3 -> updateNote()
            4 -> deleteNote()
            5 -> archiveNote()
            6 -> searchNotes()
            20 -> save()
            21 -> load()
            0 -> exitApp()
            else -> println("Invalid option entered: $option")
        }
    } while (true)
}

fun listNotes() {
    if (noteAPI.numberOfNotes() > 0) {
        val option = readNextInt(
            """
                  > --------------------------------
                  > |   1) View ALL notes          |
                  > |   2) View ACTIVE notes       |
                  > |   3) View ARCHIVED notes     |
                  > --------------------------------
         > ==>> """.trimMargin(">"))

        when (option) {
            1 -> listAllNotes();
            2 -> listActiveNotes();
            3 -> listArchivedNotes();
            else -> println("Invalid option entered: $option");
        }
    } else {
        println("Option Invalid | No notes stored");
    }
}

fun addNote(){

    val noteTitle = readNextLine("Enter a title for the note: ")

    var notePriority: Int
    do {
        notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
    } while (!validRange(notePriority, 1, 5))

    var noteCategory: String
    do {
        noteCategory = readNextLine("Enter a category for the note: ")
    } while (!isValidCategory(noteCategory))

    var status: Int
    do {
        status = readNextInt("Enter status - 1:Not Started 2:In Progress, 3:Complete: ")
    } while (!validRange(status, 1, 3))


    var estimatedTime: Double
    do {
        estimatedTime = readNextDouble("How long will it take (h.m): ")
    } while (!validRangeDouble(estimatedTime, 0.0, 999.0))

    val isAdded = noteAPI.add(Note(noteTitle, notePriority, noteCategory, false, status, estimatedTime  ))

    if (isAdded) {
        println("Added Successfully")
    } else {
        println("Add Failed")
    }
}

fun updateNote() {
    //logger.info { "updateNotes() function invoked" }
    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        //only ask the user to choose the note if notes exist
        val indexToUpdate = readNextInt("Enter the index of the note to update: ")
        if (noteAPI.isValidIndex(indexToUpdate)) {

            val noteTitle = readNextLine("Enter a title for the note: ")

            var notePriority: Int
            do {
                notePriority = readNextInt("Enter a priority (1-low, 2, 3, 4, 5-high): ")
            } while (!validRange(notePriority, 1, 5))

            var noteCategory: String
            do {
                noteCategory = readNextLine("Enter a category for the note: ")
            } while (!isValidCategory(noteCategory))

            var status: Int
            do {
                status = readNextInt("Enter status - 1:Not Started 2:In Progress, 3:Complete: ")
            } while (!validRange(status, 1, 3))

            var estimatedTime: Double
            do {
                estimatedTime = readNextDouble("How long will it take (h.m): ")
            } while (!validRangeDouble(estimatedTime, 0.0, 999.0))

            //pass the index of the note and the new note details to NoteAPI for updating and check for success.
            if (noteAPI.updateNote(indexToUpdate, Note(noteTitle, notePriority, noteCategory, false, status, estimatedTime))){
                println("Update Successful")
            } else {
                println("Update Failed")
            }
        } else {
            println("There are no notes for this index number")
        }
    }
}

fun deleteNote(){
    //logger.info { "deleteNotes() function invoked" }
    listNotes()
    if (noteAPI.numberOfNotes() > 0) {
        //only ask the user to choose the note to delete if notes exist
        val indexToDelete = readNextInt("Enter the index of the note to delete: ")
        //pass the index of the note to NoteAPI for deleting and check for success.
        val noteToDelete = noteAPI.deleteNote(indexToDelete)
        if (noteToDelete != null) {
            println("Delete Successful! Deleted note: ${noteToDelete.noteTitle}")
        } else {
            println("Delete NOT Successful")
        }
    }
}

fun searchNotes() {
    val searchTitle = readNextLine("Enter the description to search by: ")
    val searchResults = noteAPI.searchByTitle(searchTitle)
    if (searchResults.isEmpty()) {
        println("No notes found")
    } else {
        println(searchResults)
    }
}

fun listActiveNotes() {
    println(noteAPI.listActiveNotes())
}

fun listAllNotes() {
    println(noteAPI.listAllNotes())
}

fun listArchivedNotes() {
    println(noteAPI.listArchivedNotes())
}

fun archiveNote() {
    listActiveNotes()
    if (noteAPI.numberOfActiveNotes() > 0) {
        //only ask the user to choose the note to archive if active notes exist
        val indexToArchive = readNextInt("Enter the index of the note to archive: ")
        //pass the index of the note to NoteAPI for archiving and check for success.
        if (noteAPI.archiveNote(indexToArchive)) {
            println("Archive Successful!")
        } else {
            println("Archive NOT Successful")
        }
    }
}

fun save() {
    try {
        noteAPI.store()
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        noteAPI.load()
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

fun exitApp() {
    println("Exiting...bye")
    exit(0)
}