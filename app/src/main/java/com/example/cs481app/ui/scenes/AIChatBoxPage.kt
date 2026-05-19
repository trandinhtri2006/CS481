package com.example.cs481app.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cs481app.ui.ai.AIAssistantViewModel

// Expandable chat overlay — shown above all post-login screens via SceneNavigationGraph.
// Uses ModalBottomSheet so it renders above the bottom nav bar without modifying individual screens.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatOverlay(
    viewModel: AIAssistantViewModel,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.messages.size - 1)
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color(0xFFF3F4F6),
            dragHandle = {
                ChatSheetHeader(
                    isLoading = viewModel.isLoading,
                    onClose = onDismiss
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 320.dp, max = 540.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    items(viewModel.messages) { chat ->
                        ChatBubble(chat)
                    }

                    if (viewModel.isLoading) {
                        item {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                TypingIndicator()
                            }
                        }
                    }
                }

                ChatInputBar(
                    text = inputText,
                    onTextChange = { inputText = it },
                    isSendEnabled = inputText.isNotBlank() && !viewModel.isLoading,
                    onSend = {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                )
            }
        }
    }
}

@Composable
private fun ChatSheetHeader(isLoading: Boolean, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Assistant",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = if (isLoading) "Typing..." else "Ready to help",
                    fontSize = 12.sp,
                    color = if (isLoading) Color.Gray else Color(0xFF4CAF50)
                )
            }

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close assistant"
                )
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    isSendEnabled: Boolean,
    onSend: () -> Unit
) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Ask about your report...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                },
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = { if (isSendEnabled) onSend() },
                containerColor = if (isSendEnabled) Color.Black else Color.LightGray,
                modifier = Modifier.size(52.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send message",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.padding(start = 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Card(
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "• • •",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

// The ? FAB that lives at the NavHost level and toggles the overlay.
@Composable
fun ChatFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color.Black,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.QuestionMark,
            contentDescription = "Open AI assistant",
            tint = Color.White
        )
    }
}

// Renders an individual chat bubble aligned left (AI) or right (user).
@Composable
fun ChatBubble(chat: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (chat.isUser) Arrangement.End else Arrangement.Start
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (!chat.isUser) {
                Box(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (chat.isUser) 20.dp else 4.dp,
                    bottomEnd = if (chat.isUser) 4.dp else 20.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (chat.isUser) Color.Black else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = chat.text,
                    color = if (chat.isUser) Color.White else Color.Black,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)