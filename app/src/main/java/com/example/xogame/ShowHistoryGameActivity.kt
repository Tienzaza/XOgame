package com.example.xogame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.xogame.ui.theme.XOgameTheme

class ShowHistoryGameActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val timeStamp = intent.getLongExtra("timeStamp", 0L)
        val gridSizeHis = intent.getIntExtra("gridSize", 3)
        gameViewModel.fetchGameHistoryFromFirestore(timeStamp)
        setContent {
            XOgameTheme {
                Surface {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top, // Arrange children in vertical space
                        horizontalAlignment = Alignment.CenterHorizontally // Align children in the center horizontally
                    ) {
                        Button(onClick = {
                            // Go back to MainActivity
                            finish()  // This will close the current activity and return to MainActivity
                        }) {
                            Text(text = "Back to Main")
                        }
                        Text(text = "gridSize: $gridSizeHis",
                            style = MaterialTheme.typography.h4.copy(fontSize = 32.sp))
                        TicTacToeReplay(gameHistory = gameViewModel.gameData.value, gridSizeHis = gridSizeHis)


                    }
                }
            }
        }
    }
}