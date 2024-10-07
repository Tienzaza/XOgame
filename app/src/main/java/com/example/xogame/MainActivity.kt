package com.example.xogame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.Surface
import androidx.compose.material.MaterialTheme
import com.example.xogame.ui.theme.XOgameTheme
import android.content.Intent
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.foundation.layout.* // Import this to use Column and Spacer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private lateinit var gameViewModel: GameViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Enable edge-to-edge display
        setContent {
            XOgameTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top, // Arrange children in vertical space
                        horizontalAlignment = Alignment.CenterHorizontally // Align children in the center horizontally
                    ) {
                        TicTacToeScreen()  // Call the game screen

                        Spacer(modifier = Modifier.height(10.dp)) // Create space that takes up remaining vertical space

                        Button(onClick = {
                            // Open HistoryActivity
                            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Add this line to set the flag
                            startActivity(intent)
                        }) {
                            Text(text = "History")
                        }

                    }
                }
            }
        }
    }
}