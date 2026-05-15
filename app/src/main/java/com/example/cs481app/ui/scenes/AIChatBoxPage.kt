package com.example.cs481app.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


// AI CHAT SCREEN
// Main composable that renders chat UI with AI assistant
@Composable
fun AIChatBoxPage(
    navController: NavController
) {

    
    // STATE: current input text
    var message by remember { mutableStateOf("") }

    
    // STATE: chat message list
    // Uses mutableStateList so UI updates automatically
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Hello Tony 👋\nHow can I help you today?",
                isUser = false
            )
        )
    }

    // Controls scroll position of chat list
    val listState = rememberLazyListState()

    // Auto-scroll to latest message whenever list updates
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    
    // MAIN SCREEN STRUCTURE
    Scaffold(
        containerColor = Color(0xFFF3F4F6),

        
        // TOP BAR (HEADER)
        topBar = {

            Surface(
                shadowElevation = 6.dp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.Bottom
                ) {

                    // BACK BUTTON
                    IconButton(
                        onClick = {
                            navController.navigate(Routes.HOME_PAGE)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // AI ICON CIRCLE
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // TITLE SECTION
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.Bottom)
                    ) {
                        Text(
                            text = "AI Assistant",
                            fontWeight = FontWeight.Bold,
                            fontSize = 43.sp
                        )
                    }
                }
            }
        }

    ) { paddingValues ->

        
        // MAIN CHAT LAYOUT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            
            // CHAT MESSAGES LIST
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 18.dp, bottom = 18.dp)
            ) {

                items(messages) { chat ->
                    ModernChatBubble(chat)
                }
            }

            
            // MESSAGE INPUT AREA
            Surface(
                shadowElevation = 12.dp,
                color = Color.White
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {

                    // TEXT INPUT FIELD
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask me anything...") },
                        shape = RoundedCornerShape(28.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // SEND BUTTON
                    FloatingActionButton(
                        onClick = {

                            // Only send if message is not empty
                            if (message.isNotBlank()) {

                                // Add user message to chat
                                messages.add(
                                    ChatMessage(
                                        text = message,
                                        isUser = true
                                    )
                                )

                                // Simulated AI response (placeholder logic)
                                messages.add(
                                    ChatMessage(
                                        text = "I'm analyzing your request: \"$message\"",
                                        isUser = false
                                    )
                                )

                                // Clear input field after sending
                                message = ""
                            }
                        },
                        containerColor = Color.Black,
                        modifier = Modifier.size(58.dp),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}



// CHAT BUBBLE UI COMPONENT
// Displays individual user or AI messages
@Composable
fun ModernChatBubble(
    chat: ChatMessage
) {

    // Align message left (AI) or right (User)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (chat.isUser)
            Arrangement.End
        else
            Arrangement.Start
    ) {

        Row(verticalAlignment = Alignment.Bottom) {

            
            // AI AVATAR (ONLY FOR AI MESSAGES)
            if (!chat.isUser) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            
            // MESSAGE CARD
            Card(
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomStart = if (chat.isUser) 24.dp else 6.dp,
                    bottomEnd = if (chat.isUser) 6.dp else 24.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (chat.isUser)
                        Color.Black
                    else
                        Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.widthIn(max = 310.dp)
            ) {

                Column(modifier = Modifier.padding(14.dp)) {

                    // MESSAGE TEXT
                    Text(
                        text = chat.text,
                        color = if (chat.isUser) Color.White else Color.Black,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // TIMESTAMP (STATIC FOR NOW)
                    Text(
                        text = "Just now",
                        fontSize = 11.sp,
                        color = if (chat.isUser)
                            Color.White.copy(alpha = 0.7f)
                        else
                            Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}


// DATA MODEL
// Represents a single chat message
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)


// PREVIEW (Android Studio)
@Preview
@Composable
fun AI_Preview() {
    AIChatBoxPage(rememberNavController())
}