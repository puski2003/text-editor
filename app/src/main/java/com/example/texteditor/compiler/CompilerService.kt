package com.example.texteditor.compiler

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class CompilerService {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val TAG = "CompilerService"
    
    // Default ADB port forwarding: adb forward tcp:8080 tcp:8080
    private var serverUrl = "http://localhost:8080/compile"
    
    fun setServerUrl(url: String) {
        serverUrl = url
    }
    
    suspend fun compileCode(code: String, fileName: String = "temp.kt"): CompileResponse {
        return withContext(Dispatchers.IO) {
            try {
                val request = CompileRequest(code, fileName)
                val json = gson.toJson(request)
                
                Log.d(TAG, "Sending compile request to: $serverUrl")
                
                val requestBody = json.toRequestBody("application/json".toMediaType())
                val httpRequest = Request.Builder()
                    .url(serverUrl)
                    .post(requestBody)
                    .build()
                
                val response = client.newCall(httpRequest).execute()
                
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Compile response: $responseBody")
                    
                    if (responseBody != null) {
                        gson.fromJson(responseBody, CompileResponse::class.java)
                    } else {
                        CompileResponse(false, "Empty response", emptyList())
                    }
                } else {
                    Log.e(TAG, "HTTP error: ${response.code}")
                    CompileResponse(false, "HTTP Error: ${response.code}", emptyList())
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error", e)
                CompileResponse(false, "Network error: ${e.message}", emptyList())
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error", e)
                CompileResponse(false, "Error: ${e.message}", emptyList())
            }
        }
    }
    
    suspend fun testConnection(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$serverUrl/health")
                    .get()
                    .build()
                
                val response = client.newCall(request).execute()
                response.isSuccessful
            } catch (e: Exception) {
                Log.e(TAG, "Connection test failed", e)
                false
            }
        }
    }
}