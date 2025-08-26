package com.example.texteditor.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.texteditor.ui.components.CodeEditor
import com.example.texteditor.ui.components.FileExplorer
import com.example.texteditor.ui.components.FindReplaceDialog
import com.example.texteditor.ui.components.SaveAsDialog
import com.example.texteditor.ui.components.StatusBar
import com.example.texteditor.viewmodel.TextEditorViewModel
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.documentfile.provider.DocumentFile

@Composable
fun TextEditorScreen(
    viewModel: TextEditorViewModel = viewModel()
) {
    val context = LocalContext.current
    val textFieldValue by viewModel.textFieldValue.collectAsState()
    val currentFile by viewModel.currentFile.collectAsState()
    val files by viewModel.files.collectAsState()
    val isFileExplorerVisible by viewModel.isFileExplorerVisible.collectAsState()
    val isDirty by viewModel.isDirty.collectAsState()
    val wordCount by viewModel.wordCount.collectAsState()
    val charCount by viewModel.charCount.collectAsState()
    val currentWorkspaceDir by viewModel.currentWorkspaceDir.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    
    // Find and Replace state
    val isFindReplaceVisible by viewModel.isFindReplaceVisible.collectAsState()
    val findText by viewModel.findText.collectAsState()
    val replaceText by viewModel.replaceText.collectAsState()
    val matchCase by viewModel.matchCase.collectAsState()
    val matchWholeWord by viewModel.matchWholeWord.collectAsState()
    
    // Save As dialog state
    var showSaveAsDialog by remember { mutableStateOf(false) }
    
    // Directory picker launcher
    val directoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            // Grant persistent permission
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            
            // Open the directory using the URI
            viewModel.openDirectoryFromUri(context, uri.toString())
        }
    }
    
    // Snackbar for status messages
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Show status messages
    LaunchedEffect(statusMessage) {
        statusMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearStatusMessage()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            Row(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .systemBarsPadding()) {
                
                // File Explorer with animated width
                if (isFileExplorerVisible) {
                    Surface {
                        FileExplorer(
                            files = files,
                            onFileClick = { fileName, filePath -> viewModel.openFile(fileName, filePath, context) }, // Pass context directly
                            onCreateFile = { fileName -> viewModel.createNewFile(fileName, context) }, // Pass context directly
                            onCreateDirectory = { dirName -> viewModel.createNewDirectory(dirName, context) }, // Pass context directly
                            onOpenDirectory = { 
                                // Launch the system directory picker
                                directoryPickerLauncher.launch(null)
                            },
                            currentWorkspaceDir = currentWorkspaceDir,
                            modifier = Modifier.width(280.dp)
                        )
                    }
                }

                // Main Editor Area
                CodeEditor(
                    value = textFieldValue,
                    onValueChange = { viewModel.updateText(it) },
                    currentFile = currentFile,
                    currentFilePath = viewModel.currentFilePath.collectAsState().value,
                    onToggleFileExplorer = { viewModel.toggleFileExplorer() },
                    onNewFile = { viewModel.newFile() },
                    onSaveFile = { viewModel.saveCurrentFile(context) },
                    onSaveAsFile = { showSaveAsDialog = true },
                    onCopy = { viewModel.copy(context) },
                    onCut = { viewModel.cut(context) },
                    onPaste = { viewModel.paste(context) },
                    onUndo = { viewModel.undo() },
                    onRedo = { viewModel.redo() },
                    onShowFindReplace = { viewModel.showFindReplace() },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Status Bar
            StatusBar(
                wordCount = wordCount,
                charCount = charCount,
                currentFile = currentFile,
                isDirty = isDirty
            )
        }
        
        // Snackbar for status messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.padding(16.dp)
        )
    }
    
    // Save As Dialog
    SaveAsDialog(
        isVisible = showSaveAsDialog,
        currentFileName = currentFile,
        onSaveAs = { fileName ->
            viewModel.saveAsFile(fileName, context)
            showSaveAsDialog = false
        },
        onDismiss = { showSaveAsDialog = false }
    )
    
    // Find and Replace Dialog
    FindReplaceDialog(
        isVisible = isFindReplaceVisible,
        findText = findText,
        replaceText = replaceText,
        matchCase = matchCase,
        matchWholeWord = matchWholeWord,
        onFindTextChange = { viewModel.updateFindText(it) },
        onReplaceTextChange = { viewModel.updateReplaceText(it) },
        onMatchCaseToggle = { viewModel.toggleMatchCase() },
        onMatchWholeWordToggle = { viewModel.toggleMatchWholeWord() },
        onFindNext = { viewModel.findNext() },
        onReplaceNext = { viewModel.replaceNext() },
        onReplaceAll = { viewModel.replaceAll() },
        onClose = { viewModel.hideFindReplace() }
    )
}