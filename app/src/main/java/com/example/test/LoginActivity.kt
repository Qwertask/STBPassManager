package com.example.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {

    private lateinit var db: LauncherDatabase
    private lateinit var db_pass: TaskItemDatabase
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db_pass = Room.databaseBuilder(applicationContext, TaskItemDatabase::class.java, "task_item_database")
            .build()
        db = Room.databaseBuilder(applicationContext, LauncherDatabase::class.java, "launcher")
            .build()

        lifecycleScope.launch {
            db_pass.taskItemDao().allTaskItems().collect { taskItems ->
                for (item in taskItems) {
                    Log.d("PASSWORD_ITEM", item.toString())
                }
            }
        }
        lifecycleScope.launch {
            Log.d("HASH_ITEM", db.launcherDao().getLauncher().toString())
        }




        passwordEditText = findViewById(R.id.loginPasswordInput)

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val enteredPassword = passwordEditText.text.toString()
            val enteredPasswordHash = beltHash(stringToHex(enteredPassword))

            CoroutineScope(Dispatchers.Main).launch {
                val launcher = db.launcherDao().getLauncher()
                if (launcher != null && launcher.passwordHash == enteredPasswordHash) {

                    val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("encryption_key", stringToHex(enteredPassword))
                    editor.apply()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Неправильный пароль!!!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val resetButton: Button = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                db.launcherDao().clearTable()
                db_pass.taskItemDao().clearTable()
                // Возвращаемся в WelcomeActivity
                startActivity(Intent(this@LoginActivity, WelcomeActivity::class.java))
                finish()
            }
        }
    }
}
