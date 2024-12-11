package com.example.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LauncherActivity : AppCompatActivity() {
    private lateinit var db: LauncherDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(applicationContext, LauncherDatabase::class.java, "launcher")
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            val launcher = db.launcherDao().getLauncher()
            if (launcher != null) {
                // Переход в LoginActivity
                startActivity(Intent(this@LauncherActivity, LoginActivity::class.java))
            } else {
                // Переход в WelcomeActivity
                startActivity(Intent(this@LauncherActivity, WelcomeActivity::class.java))
            }
            finish()
        }
    }
}
