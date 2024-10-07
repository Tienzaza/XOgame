package com.example.xogame

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TicTacToeScreen(viewModel: GameViewModel = viewModel()) {
    // Observe the game board and grid size from the ViewModel
    val board by viewModel.board
    val gridSize by viewModel.gridSize
    val currentPlayer by viewModel.currentPlayer
    val gameStatus by viewModel.gameStatus  // Observe the game status

    // Define the base size for cells, and reduce size as grid increases
    val baseCellSize = 300.dp
    val cellSize = baseCellSize / gridSize

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Render grid size selector
        GridSizeSelector(viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Display the current player
        Text(text = "Current Player: $currentPlayer",
            style = MaterialTheme.typography.h4.copy(fontSize = 32.sp))

        /*Text(text = "Current Turn: $turnCounter",
            style = MaterialTheme.typography.h4.copy(fontSize = 32.sp))

        Text(text = "Current Game: $gameCounter",
            style = MaterialTheme.typography.h4.copy(fontSize = 32.sp))*/

        // Display the game status (winner or ongoing) only if not ongoing
        if (gameStatus != "Ongoing") {
            Text(
                text = gameStatus,
                style = MaterialTheme.typography.h4.copy(fontSize = 32.sp),
                color = if (gameStatus.contains("draw")) Color.Gray else Color.Red // Color based on the status
            )
        }

        // Render the grid based on the current grid size
        for (row in 0 until gridSize) {
            Row {
                for (col in 0 until gridSize) {
                    TicTacToeCell(
                        row = row,
                        col = col,
                        value = board[row][col],
                        cellSize = cellSize  // Pass calculated cell size
                    ) {
                        // On click, make a move for the current player
                        viewModel.makeMove(row, col, currentPlayer)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to reset the game
        Button(onClick = { viewModel.resetGame(gridSize) }) {
            Text(text = "Reset Game")
        }

    }
}

@Composable
fun TicTacToeReplay(viewModel: GameViewModel = viewModel(), gameHistory: List<GameHistoryData>, gridSizeHis:Int) {
    // Observe the game board and grid size from the ViewModel
    val board by viewModel.board
    val currentPlayer by viewModel.currentPlayer
    val gameStatus by viewModel.gameStatus  // Observe the game status
    val gridSize by viewModel.gridSize

    // Define the base size for cells, and reduce size as grid increases
    val baseCellSize = 300.dp
    val cellSize = baseCellSize / gridSize

    // Variable to track the replay state
    var isReplaying by remember { mutableStateOf(false) }
    var job: Job? by remember { mutableStateOf(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GridSizeSelector(viewModel)


        // Display the current player
        Text(text = "Current Player: $currentPlayer",
            style = MaterialTheme.typography.h4.copy(fontSize = 20.sp))


        // Display the game status (winner or ongoing) only if not ongoing
        if (gameStatus != "Ongoing") {
            Text(
                text = gameStatus,
                style = MaterialTheme.typography.h4.copy(fontSize = 32.sp),
                color = if (gameStatus.contains("draw")) Color.Gray else Color.Red // Color based on the status
            )
        }

        // Render the grid based on the current grid size
        for (row in 0 until gridSize) {
            Row {
                for (col in 0 until gridSize) {
                    TicTacToeCell(
                        row = row,
                        col = col,
                        value = board[row][col],
                        cellSize = cellSize  // Pass calculated cell size
                    ) {
                        // On click, make a move for the current player only if not replaying
                        if (!isReplaying) {
                            viewModel.makeMove(row, col, currentPlayer)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Button to start the replay
        Button(onClick = {
            isReplaying = true
            job = startReplay(gameHistory, viewModel, isReplaying) { isReplaying = false } // Pass the callback to reset isReplaying
        }) {
            Text(text = "Start Replay")
        }

        // Button to stop the replay
        Button(onClick = {
            job?.cancel() // Cancel the replay coroutine
            isReplaying = false // Reset the replay state
        }) {
            Text(text = "Stop Replay")
        }
    }
}

private fun startReplay(
    gameHistory: List<GameHistoryData>,
    viewModel: GameViewModel,
    isReplaying: Boolean, // Pass isReplaying as a parameter
    onReplayFinished: () -> Unit // Callback to reset isReplaying
): Job {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    return coroutineScope.launch {
        for (history in gameHistory) {
            delay(1000) // Delay 1 seconds between moves
            if (!isReplaying) return@launch // Check if replay has been stopped
            history.row?.let { row ->
                history.col?.let { col ->
                    // Make the move for the current player from history
                    viewModel.makeMove(row, col, history.player ?: "X") // Use X or O based on your logic
                }
            }
        }
        onReplayFinished() // Call the callback when replay is finished
    }
}



// Composable function for each cell in the Tic Tac Toe grid
@Composable
fun TicTacToeCell(row: Int, col: Int, value: String, cellSize: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(cellSize)  // Use dynamic cell size
            .border(2.dp, Color.Black)  // Border for the cell
            .clickable { if (value == "") onClick() }  // Allow clicking only on empty cells
            .background(if (value == "") Color.White else Color.Gray),  // Set background color based on cell value
        contentAlignment = Alignment.Center
    ) {
        Text(text = value, style = MaterialTheme.typography.h4)  // Display the value (X or O)
    }
}


// Composable function for selecting grid size
@Composable
fun GridSizeSelector(viewModel: GameViewModel) {
    var sliderValue by remember { mutableStateOf(3f) }  // Default grid size

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(50.dp))
        // Display the selected grid size
        Text(text = "Grid Size: ${sliderValue.toInt()} x ${sliderValue.toInt()}",
            style = MaterialTheme.typography.h4.copy(fontSize = 32.sp))

        // Slider to adjust the grid size
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },  // Update slider as user drags it
            valueRange = 3f..10f,  // Allow grid size between 3 and 10
            steps = 6,  // 7 steps to match grid sizes (3 to 10)
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Button to set the grid size
        Button(onClick = {
            viewModel.resetGrid(sliderValue.toInt())  // Reset the game with new grid size
        }) {
            Text(text = "Set Grid Size")
        }
    }
}