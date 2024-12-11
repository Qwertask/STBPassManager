package com.example.test
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.test.LauncherDatabase
import com.example.test.R
import java.security.MessageDigest
import android.content.Intent
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {

    private lateinit var db: LauncherDatabase
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        db = Room.databaseBuilder(applicationContext, LauncherDatabase::class.java, "launcher")
            .build()

        passwordEditText = findViewById(R.id.passEditText)
        repeatPasswordEditText = findViewById(R.id.repeatPassEditText)

        val submitButton: Button = findViewById(R.id.submitButton)
        submitButton.setOnClickListener {
            val password = passwordEditText.text.toString()
            val repeatPassword = repeatPasswordEditText.text.toString()

            if (password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Заполните оба поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length <8 || password.length>16) {
                Toast.makeText(this, "Пароль должен быть от 8 до 16 символов!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            // Если пароли совпадают, продолжаем
            val passwordHash = beltHash(stringToHex(password))

            CoroutineScope(Dispatchers.Main).launch {
                // Очищаем таблицу перед вставкой
                db.launcherDao().clearTable()

                // Вставляем новую запись
                val newLauncher = Launcher(passwordHash = passwordHash)
                db.launcherDao().insert(newLauncher)

                // Переход в LoginActivity
                startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
                finish()
            }
        }

    }
}
