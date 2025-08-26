package com.example.texteditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.texteditor.R
import com.example.texteditor.compiler.CompilerError
import com.example.texteditor.compiler.ErrorSeverity

@Composable
fun CompileErrorPanel(
    errors: List<CompilerError>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF252526)
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
                Text(
                    text = "Compile Errors (${errors.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
            
            Divider(color = Color.Gray)
            
            // Error list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(errors) { error ->
                    CompileErrorItem(error = error)
                }
            }
        }
    }
}

@Composable
fun CompileErrorItem(
    error: CompilerError,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (error.severity) {
        ErrorSeverity.ERROR -> ImageVector.vectorResource(R.drawable.baseline_error_24) to Color.Red
        ErrorSeverity.WARNING -> Icons.Default.Warning to Color(0xFFFF9800)
        ErrorSeverity.INFO -> Icons.Default.Info to Color(0xFF2196F3)
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color(0xFF1E1E1E),
                shape = MaterialTheme.shapes.small
            )
            .padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = error.severity.name,
            tint = color,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = "Line ${error.line}, Column ${error.column}",
                color = Color(0xFF858585),
                fontSize = 12.sp
            )
            
            Text(
                text = error.message,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}