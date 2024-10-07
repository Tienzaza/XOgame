package com.example.xogame

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class GameViewModel : ViewModel() {
    private var db = Firebase.firestore

    var gridSize = mutableStateOf(3) // Default grid size
    var board = mutableStateOf(
        MutableList(gridSize.value) { MutableList(gridSize.value) { "" } } // Initialize grid
    )
    var currentPlayer = mutableStateOf("X") // Track the current player
    var gameStatus = mutableStateOf("Ongoing") // Game status
    var turnCounter = mutableStateOf(0)
    var gameCounter = mutableStateOf(1)
    var timeStamp = System.currentTimeMillis()
    var timeStampList = mutableStateOf(listOf<Long>())
    var gameData = mutableStateOf(listOf<GameHistoryData>())
    var gameGridSize = mutableStateOf(listOf<Int>())
    // List to store all game records


    // Current game history
    private var currentGameHistory = mutableListOf<GameHistoryData>()

    // Function to reset the game and set grid size
    fun resetGame(size: Int) {

        gridSize.value = size
        board.value = MutableList(size) { MutableList(size) { "" } } // Reset the board
        currentPlayer.value = "X" // Reset player to "X" when game restarts
        gameStatus.value = "Ongoing" // Reset game status
        turnCounter.value = 0
        gameCounter.value++
        currentGameHistory = mutableListOf()

        val timeStampList = hashMapOf(
            "timeStamp" to timeStamp.toString().trim(),
            "gridSize"  to gridSize.value.toString().trim()
        )
        db.collection("timeStampList").add(timeStampList).addOnSuccessListener {documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
        timeStamp = System.currentTimeMillis()
    }
    fun resetGrid(size: Int) {

        gridSize.value = size
        board.value = MutableList(size) { MutableList(size) { "" } } // Reset the board
        currentPlayer.value = "X" // Reset player to "X" when game restarts
        gameStatus.value = "Ongoing" // Reset game status
        turnCounter.value = 0
        gameCounter.value++
        currentGameHistory = mutableListOf()
        timeStamp = System.currentTimeMillis()
    }



    // Function to check if a player has won
    fun checkWin(player: String): Boolean {
        // Check rows for a win
        for (row in board.value) {
            if (row.all { it == player }) {
                return true
            }
        }

        // Check columns for a win
        for (col in 0 until gridSize.value) {
            if (board.value.all { it[col] == player }) {
                return true
            }
        }

        // Check diagonals for a win
        if ((0 until gridSize.value).all { i -> board.value[i][i] == player }) {
            return true
        }

        if ((0 until gridSize.value).all { i -> board.value[i][gridSize.value - 1 - i] == player }) {
            return true
        }

        return false // No winner found
    }

    // Function to make a move on the board and switch player
    fun makeMove(row: Int, col: Int, player: String) {
        if (gameStatus.value == "Ongoing"){
            if (board.value[row][col].isEmpty()) { // Only proceed if the cell is empty
                val updatedRow = board.value[row].toMutableList()
                updatedRow[col] = player // Place the player's mark
                board.value = board.value.toMutableList().apply {
                    this[row] = updatedRow // Update the board
                }

                // Check for a win after the move
                if (checkWin(player)) {
                    gameStatus.value = "$player wins!" // Update game status with the winner
                    turnCounter.value++


                } else if (board.value.flatten().none { it == "" }) {
                    gameStatus.value = "It's a draw!" // Check for a draw
                    turnCounter.value++

                } else {
                    // Switch the current player
                    currentPlayer.value = if (currentPlayer.value == "X") "O" else "X"
                    turnCounter.value++
                }


                val playHistoryMap = hashMapOf(
                    "gridSize" to gridSize.value.toString().trim(),
                    "row" to row.toString().trim(),
                    "col" to col.toString().trim(),
                    "player" to player.trim(),
                    "turnCounter" to turnCounter.value.toString().trim(),
                    "gameCounter" to gameCounter.value.toString().trim()
                )

                db.collection("$timeStamp").add(playHistoryMap).addOnSuccessListener {documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            }
        }
    }

    fun makeMoveReplay(row: Int, col: Int, player: String) {
        if (gameStatus.value == "Ongoing"){
            if (board.value[row][col].isEmpty()) { // Only proceed if the cell is empty
                val updatedRow = board.value[row].toMutableList()
                updatedRow[col] = player // Place the player's mark
                board.value = board.value.toMutableList().apply {
                    this[row] = updatedRow // Update the board
                }

                // Check for a win after the move
                if (checkWin(player)) {
                    gameStatus.value = "$player wins!" // Update game status with the winner
                    turnCounter.value++


                } else if (board.value.flatten().none { it == "" }) {
                    gameStatus.value = "It's a draw!" // Check for a draw
                    turnCounter.value++

                } else {
                    // Switch the current player
                    currentPlayer.value = if (currentPlayer.value == "X") "O" else "X"
                    turnCounter.value++
                }


                val playHistoryMap = hashMapOf(
                    "gridSize" to gridSize.value.toString().trim(),
                    "row" to row.toString().trim(),
                    "col" to col.toString().trim(),
                    "player" to player.trim(),
                    "turnCounter" to turnCounter.value.toString().trim(),
                    "gameCounter" to gameCounter.value.toString().trim()
                )

                db.collection("$timeStamp").add(playHistoryMap).addOnSuccessListener {documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            }
        }
    }


    fun fetchTimeStampListFromFirestore() {
        db.collection("timeStampList")
            .get()
            .addOnSuccessListener { result ->
                val fetchedTimeStampList = mutableListOf<Long>()
                val fetchedGridSizeList = mutableListOf<Int>()
                for (document in result) {
                    val timeStamp = document.getString("timeStamp")?.toLongOrNull()
                    val gridSizeHis = document.getString("gridSize")?.toIntOrNull()
                    if (timeStamp != null) {
                        fetchedTimeStampList.add(timeStamp)
                    }
                    if (gridSizeHis != null) {
                        fetchedGridSizeList.add(gridSizeHis)
                    }
                }
                gameGridSize.value = fetchedGridSizeList
                timeStampList.value = fetchedTimeStampList // Update the state
                Log.d(TAG, "TimeStampLsit: ${timeStampList.value} ${gameGridSize.value}")

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
    fun fetchGameHistoryFromFirestore(timestamp: Long) {
        db.collection("$timestamp")
            .get()
            .addOnSuccessListener { result ->
                val fetchedGameData = mutableListOf<GameHistoryData>()
                for (document in result) {
                    val col = document.getString("col")?.toIntOrNull()
                    val row = document.getString("row")?.toIntOrNull()
                    val gridSize = document.getString("gridSize")?.toIntOrNull()
                    val gameCounter = document.getString("gameCounter")?.toIntOrNull()
                    val turnCounter = document.getString("turnCounter")?.toIntOrNull()
                    val player = document.getString("player")

                    // creat object GameHistoryData
                    val gameHistoryData = GameHistoryData(
                        gridSize = gridSize,
                        player = player,
                        row = row,
                        col = col,
                        turnCounter = turnCounter,
                        gameCounter = gameCounter
                    )

                    // add data in fetchedGameData
                    if (turnCounter != null) {
                        fetchedGameData.add(gameHistoryData)
                    }
                }

                // sort data in fetchedGameData
                val sortedGameData = fetchedGameData.sortedBy { it.turnCounter }

                // updata data
                gameData.value = sortedGameData
                Log.d(TAG, "Sorted GameData: ${gameData.value}")

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

}