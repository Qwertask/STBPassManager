package com.example.test

import android.app.Application
import android.content.Context

class TodoApplication:Application() {
    private val database by lazy { TaskItemDatabase.getDatabase(this) }
    val repository by lazy { TaskItemRepository(database.taskItemDao(), hexToString( getEncryptionKey())) }
    private fun getEncryptionKey(): String {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("encryption_key", "") ?: ""
    }
}