package com.example.texteditor.compiler

import com.google.gson.annotations.SerializedName

data class CompileRequest(
    val code: String,
    val fileName: String = "temp.kt"
)

data class CompileResponse(
    val success: Boolean,
    val output: String,
    val errors: List<CompilerError>
)

data class CompilerError(
    val line: Int,
    val column: Int,
    val message: String,
    val severity: ErrorSeverity
)

enum class ErrorSeverity {
    @SerializedName("ERROR")
    ERROR,
    @SerializedName("WARNING")
    WARNING,
    @SerializedName("INFO")
    INFO
}