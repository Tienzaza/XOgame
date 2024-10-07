package com.example.xogame

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.xogame.ui.theme.XOgameTheme

class HistoryActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // call fetchTimeStampListFromFirestore to get data from Firestore
        gameViewModel.fetchTimeStampListFromFirestore()

        setContent {
            XOgameTheme {
                Surface {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            // Go back to MainActivity
                            finish()  // Close current activity and return to MainActivity
                        }) {
                            Text(text = "Back to Main")
                        }

                        Spacer(modifier = Modifier.height(16.dp))  // Add some spacing

                        // Observe timeStampList from the ViewModel and display it in LazyColumn
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp), // Add padding to LazyColumn
                            verticalArrangement = Arrangement.spacedBy(8.dp) // Space between items
                        ) {

                            items(gameViewModel.timeStampList.value) { timeStamp ->
                                val index = gameViewModel.timeStampList.value.indexOf(timeStamp)
                                val gridSize = gameViewModel.gameGridSize.value.getOrNull(index) ?: 3
                                Log.d(TAG, "gridSizegridSizegridSize: ${gridSize}")
                                // Each item in LazyColumn is a clickable Card
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            // Intent to ShowHistoryGameActivity
                                            val intent = Intent(this@HistoryActivity, ShowHistoryGameActivity::class.java)
                                            intent.putExtra("timeStamp", timeStamp)
                                            intent.putExtra("gridSize", gridSize)
                                            startActivity(intent)
                                        },
                                    elevation = 8.dp, // Shadow effect
                                    backgroundColor = Color.LightGray, // Card background color
                                    shape = RoundedCornerShape(16.dp) // Rounded corners
                                ) {
                                    // Content inside each Card
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp) // Padding inside the card
                                    ) {
                                        Text(
                                            text = "Timestamp: $timeStamp",
                                            fontSize = 20.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
