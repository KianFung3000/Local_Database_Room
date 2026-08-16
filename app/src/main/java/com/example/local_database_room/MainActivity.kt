package com.example.local_database_room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var studentNameInput: EditText
    private lateinit var studentAgeInput: EditText
    private lateinit var addButton: Button
    private lateinit var nextActivityButton: Button
    private lateinit var db: AppDatabase
    private lateinit var studentDao: StudentDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind views
        studentNameInput = findViewById(R.id.studentName)
        studentAgeInput = findViewById(R.id.studentAge)
        addButton = findViewById(R.id.addButton)
        nextActivityButton = findViewById(R.id.nextActivity)

        // Get database & DAO
        db = AppDatabase.getDatabase(this)
        studentDao = db.studentDao()

        // Add button click
        addButton.setOnClickListener {
            val name = studentNameInput.text.toString().trim()
            val ageText = studentAgeInput.text.toString().trim()

            if (name.isNotEmpty() && ageText.isNotEmpty()) {
                val age = ageText.toIntOrNull() ?: 0
                val student = Student(name = name, age = age)
                lifecycleScope.launch {
                    studentDao.insert(student)
                }
                studentNameInput.text.clear()
                studentAgeInput.text.clear()
            }
        }

        // Next activity button click
        nextActivityButton.setOnClickListener {
            val intent = Intent(this, NextActivity::class.java)
            startActivity(intent)
        }
    }
}