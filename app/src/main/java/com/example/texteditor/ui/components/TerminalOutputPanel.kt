package com.example.texteditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TerminalOutputPanel(
    output: String?,
    isCompiling: Boolean,
    onDismiss: () -> Unit,
    onClearOutput: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E) // Dark terminal background
        )
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Terminal",
                        tint = Color.Green,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Output Terminal",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    
                    if (isCompiling) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color.Green
                        )
                        Text(
                            text = "Compiling...",
                            color = Color.Yellow,
                            fontSize = 12.sp
                        )
                    }
                }
                
                Row {
                    // Clear button
                    IconButton(
                        onClick = onClearOutput,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Output",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Terminal",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Divider(color = Color.Gray, thickness = 1.dp)
            
            // Terminal content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0C0C0C)) // Darker background for content
                    .padding(12.dp)
            ) {
                val scrollState = rememberScrollState()
                
                LaunchedEffect(output) {
                    // Auto-scroll to bottom when new output arrives
                    if (output?.isNotEmpty() == true) {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
                
                if (output?.isNotEmpty() == true) {
                    Text(
                        text = output,
                        color = Color.Green,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                    )
                } else if (isCompiling) {
                    Text(
                        text = "> Compiling and executing code...",
                        color = Color.Yellow,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                } else {
                    Text(
                        text = "> Ready. Press 'Compile & Run' to execute your code.",
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}