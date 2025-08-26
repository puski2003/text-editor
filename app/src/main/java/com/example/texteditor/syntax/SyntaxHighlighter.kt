package com.example.texteditor.syntax

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

class SyntaxHighlighter(private val configuration: SyntaxConfiguration) {
    
    private val TAG = "SyntaxHighlighter"
    
    init {
        Log.d(TAG, "SyntaxHighlighter initialized with ${configuration.languages.size} languages")
        configuration.languages.forEach { lang ->
            Log.d(TAG, "Language: ${lang.name}, Extensions: ${lang.fileExtensions}")
        }
    }
    
    fun highlightText(text: String, fileExtension: String?): AnnotatedString {
        Log.d(TAG, "=== Starting highlight for extension: '$fileExtension' ===")
        Log.d(TAG, "Text length: ${text.length}")
        Log.d(TAG, "Text preview: '${text.take(50)}...'")
        
        val languageRule = findLanguageRule(fileExtension)
        if (languageRule == null) {
            Log.w(TAG, "No language rule found for extension: $fileExtension")
            return AnnotatedString(text)
        }
        
        Log.d(TAG, "Using language rule: ${languageRule.name}")
        Log.d(TAG, "Keywords count: ${languageRule.keywords.size}")
        Log.d(TAG, "Sample keywords: ${languageRule.keywords.take(5)}")
        
        val theme = configuration.theme
        Log.d(TAG, "Theme colors - Keywords: ${theme.keywords}, Strings: ${theme.strings}")
        
        return buildAnnotatedString {
            var currentIndex = 0
            val textLength = text.length
            var highlightedCount = 0
            
            while (currentIndex < textLength) {
                val remainingText = text.substring(currentIndex)
                
                // Skip whitespace
                if (remainingText[0].isWhitespace()) {
                    append(remainingText[0])
                    currentIndex++
                    continue
                }
                
                when {
                    // Handle line comments
                    languageRule.lineComment != null && 
                    remainingText.startsWith(languageRule.lineComment!!) -> {
                        val endOfLine = text.indexOf('\n', currentIndex).let { 
                            if (it == -1) textLength else it 
                        }
                        val commentText = text.substring(currentIndex, endOfLine)
                        
                        Log.d(TAG, "Found comment: '$commentText'")
                        withStyle(SpanStyle(color = theme.comments)) {
                            append(commentText)
                        }
                        currentIndex = endOfLine
                        highlightedCount++
                    }
                    
                    // Handle strings
                    remainingText[0] == '"' || remainingText[0] == '\'' -> {
                        val delimiter = remainingText[0].toString()
                        val endIndex = remainingText.indexOf(delimiter, 1)
                        val stringText = if (endIndex != -1) {
                            remainingText.substring(0, endIndex + 1)
                        } else {
                            remainingText.substringBefore('\n').ifEmpty { remainingText }
                        }
                        
                        Log.d(TAG, "Found string: '$stringText'")
                        withStyle(SpanStyle(color = theme.strings)) {
                            append(stringText)
                        }
                        currentIndex += stringText.length
                        highlightedCount++
                    }
                    
                    // Handle words (potential keywords)
                    remainingText[0].isLetter() || remainingText[0] == '_' -> {
                        val wordMatch = Regex("^[a-zA-Z_]\\w*").find(remainingText)?.value
                        if (wordMatch != null) {
                            val isKeyword = languageRule.keywords.contains(wordMatch)
                            
                            if (isKeyword) {
                                Log.d(TAG, "Found keyword: '$wordMatch'")
                                withStyle(SpanStyle(color = theme.keywords, fontWeight = FontWeight.Bold)) {
                                    append(wordMatch)
                                }
                                highlightedCount++
                            } else {
                                withStyle(SpanStyle(color = theme.default)) {
                                    append(wordMatch)
                                }
                            }
                            currentIndex += wordMatch.length
                        } else {
                            append(remainingText[0])
                            currentIndex++
                        }
                    }
                    
                    // Handle numbers
                    remainingText[0].isDigit() -> {
                        val numberMatch = Regex("^\\d+(\\.\\d+)?[fFlL]?").find(remainingText)?.value
                        if (numberMatch != null) {
                            Log.d(TAG, "Found number: '$numberMatch'")
                            withStyle(SpanStyle(color = theme.numbers)) {
                                append(numberMatch)
                            }
                            currentIndex += numberMatch.length
                            highlightedCount++
                        } else {
                            append(remainingText[0])
                            currentIndex++
                        }
                    }
                    
                    // Handle operators
                    remainingText[0].toString().matches(Regex("[+\\-*/%=<>!&|^~?:]")) -> {
                        withStyle(SpanStyle(color = theme.operators)) {
                            append(remainingText[0])
                        }
                        currentIndex++
                        highlightedCount++
                    }
                    
                    // Default case
                    else -> {
                        withStyle(SpanStyle(color = theme.default)) {
                            append(remainingText[0])
                        }
                        currentIndex++
                    }
                }
            }
            
            Log.d(TAG, "=== Highlighting complete ===")
            Log.d(TAG, "Total highlighted elements: $highlightedCount")
        }
    }
    
    private fun findLanguageRule(fileExtension: String?): LanguageRule? {
        Log.d(TAG, "Looking for language rule for extension: '$fileExtension'")
        
        if (fileExtension == null) {
            Log.d(TAG, "Extension is null")
            return null
        }
        
        val rule = configuration.languages.find { rule ->
            val matches = rule.fileExtensions.any { ext -> 
                val match1 = fileExtension.lowercase().endsWith(ext.lowercase())
                val match2 = fileExtension.lowercase() == ext.lowercase()
                Log.d(TAG, "Checking '$fileExtension' against '$ext': endsWith=$match1, equals=$match2")
                match1 || match2
            }
            matches
        }
        
        Log.d(TAG, "Found rule: ${rule?.name ?: "none"}")
        return rule
    }
}