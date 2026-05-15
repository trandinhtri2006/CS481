package com.example.cs481app.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// HISTORY SCREEN
// Displays a list of previous AI chat conversations
@Composable
fun HistoryPage(
    navController: NavController
) {

    // STATIC CHAT HISTORY DATA
    // Probably come from a database or API
    val chatHistoryList: List<ChatHistory> = listOf(

        ChatHistory(
            title = "Emergency Assistance",
            preview = "How do I contact nearby emergency services?",
            time = "2 mins ago"
        ),

        ChatHistory(
            title = "Medical Help",
            preview = "What should I do during a panic attack?",
            time = "1 hour ago"
        ),

        ChatHistory(
            title = "Safety Report",
            preview = "Create a safety incident report",
            time = "Yesterday"
        ),

        ChatHistory(
            title = "AI Assistant",
            preview = "Nearest hospital information",
            time = "2 days ago"
        ),

        ChatHistory(
            title = "Account Support",
            preview = "How do I reset my password?",
            time = "1 week ago"
        )
    )


    // SCREEN STRUCTURE
    // Includes bottom navigation bar + content area
    Scaffold(

        bottomBar = {
            Bottombar(
                homePage = false,
                reportPage = false,
                settingPage = false,
                historyPage = true, // current active tab
                navController = navController
            )
        }

    ) { paddingValues ->

        // MAIN CONTENT LAYOUT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 18.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))


            // PAGE HEADER
            Text(
                text = "Recent Conversations",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Access your previous AI chats",
                color = Color.Gray,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(20.dp))


            // CHAT HISTORY LIST
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                items(chatHistoryList) { chat ->

                    // Each chat item rendered as a card
                    HistoryCard(
                        chat = chat,
                        onClick = {

                            // Navigate to AI chat screen when clicked
                            navController.navigate(Routes.AI_CHATBOX)
                        }
                    )
                }

                // Extra spacing at bottom of list
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}


// HISTORY CARD COMPONENT
// Displays a single chat history entry
@Composable
fun HistoryCard(
    chat: ChatHistory,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()

            // Make entire card clickable
            .clickable { onClick() },

        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            // CHAT ICON
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chat",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))


            // TEXT CONTENT SECTION
            Column(modifier = Modifier.weight(1f)) {

                // Chat title
                Text(
                    text = chat.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Chat preview message
                Text(
                    text = chat.preview,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Timestamp
                Text(
                    text = chat.time,
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right arrow indicator
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Open Chat",
                tint = Color.Gray
            )
        }
    }
}


// DATA MODEL
// Represents a single chat history item
data class ChatHistory(
    val title: String,
    val preview: String,
    val time: String
)


// PREVIEW FUNCTION
@Preview
@Composable
fun HistoryPreview() {
    HistoryPage(rememberNavController())
}
