package com.example.local_database_room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.view.LayoutInflater

class NextActivity : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var backButton: Button
    private lateinit var dao: StudentDAO
    private lateinit var deleteIdInput: EditText
    private lateinit var deleteByIdButton: Button
    private lateinit var addNewStudentButton: Button

    override fun onCreate(savedInstancedState: Bundle?) {
        super.onCreate(savedInstancedState)
        setContentView(R.layout.activity_next)

        container = findViewById(R.id.containerStudents)
        backButton = findViewById(R.id.backButton)
        dao = AppDatabase.getDatabase(this).studentDao()
        deleteIdInput = findViewById(R.id.deleteIdInput)
        deleteByIdButton = findViewById(R.id.deleteByIdButton)
        addNewStudentButton = findViewById(R.id.addNewStudentButton)

        // Back button closes this activity
        backButton.setOnClickListener {
            finish()
        }

        // Delete by ID button click
        deleteByIdButton.setOnClickListener {
            val idText = deleteIdInput.text.toString().trim()
            if (idText.isNotEmpty()) {
                val studentId = idText.toLongOrNull()
                if (studentId != null) {
                    deleteStudentById(studentId)
                } else {
                    // Show error for invalid ID
                    AlertDialog.Builder(this)
                        .setTitle("Invalid ID")
                        .setMessage("Please enter a valid student ID")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }

        addNewStudentButton.setOnClickListener {
            showAddStudentDialog()
        }

        loadStudents()
    }

    private fun loadStudents() {
        lifecycleScope.launch {
            val students = dao.getAllStudents()
            container.removeAllViews()
            students.forEachIndexed { index, student ->
                // Inflate the student item layout
                val studentItemView = LayoutInflater.from(this@NextActivity)
                    .inflate(R.layout.student_item, container, false)

                val studentNumber = studentItemView.findViewById<TextView>(R.id.studentNumber)
                val studentName = studentItemView.findViewById<TextView>(R.id.studentName)
                val studentAge = studentItemView.findViewById<TextView>(R.id.studentAge)
                val editButton = studentItemView.findViewById<Button>(R.id.editButton)
                val deleteButton = studentItemView.findViewById<Button>(R.id.deleteButton)

                // Set student info with numbering
                studentNumber.text = "${index + 1}. ID: ${student.id}"
                studentName.text = student.name
                studentAge.text = student.age.toString()

                // Edit button click
                editButton.setOnClickListener {
                    showEditStudentDialog(student)
                }

                // Delete button click
                deleteButton.setOnClickListener {
                    showDeleteConfirmationDialog(student)
                }

                container.addView(studentItemView)
            }
        }
    }

    private fun deleteStudentById(studentId: Long) {
        lifecycleScope.launch {
            val student = dao.getStudentById(studentId)
            if (student != null) {
                dao.delete(student)
                loadStudents()
                deleteIdInput.text.clear()

                // To show the success message
                AlertDialog.Builder(this@NextActivity)
                    .setTitle("Success")
                    .setMessage("Student with ID $studentId deleted successfully")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                // Student not found
                AlertDialog.Builder(this@NextActivity)
                    .setTitle("Not Found")
                    .setMessage("Student with ID $studentId not found")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }

    private fun showEditStudentDialog(student: Student) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_student, null)
        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editAge = dialogView.findViewById<EditText>(R.id.editAge)

        // Pre-fill with current values
        editName.setText(student.name)
        editAge.setText(student.age.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Student")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, which ->
                val newName = editName.text.toString().trim()
                val newAge = editAge.text.toString().trim().toIntOrNull() ?: 0

                if (newName.isNotEmpty()) {
                    val updatedStudent = student.copy(name = newName, age = newAge)
                    lifecycleScope.launch {
                        dao.update(updatedStudent)
                        loadStudents() // Refresh the list
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Add New Student Dialog
    private fun showAddStudentDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_student, null)
        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editAge = dialogView.findViewById<EditText>(R.id.editAge)

        // Clear any existing text
        editName.text.clear()
        editAge.text.clear()

        AlertDialog.Builder(this)
            .setTitle("Add New Student")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, which ->
                val name = editName.text.toString().trim()
                val ageText = editAge.text.toString().trim()

                if (name.isNotEmpty() && ageText.isNotEmpty()) {
                    val age = ageText.toIntOrNull() ?: 0
                    val newStudent = Student(name = name, age = age)
                lifecycleScope.launch {
                    dao.insert(newStudent)
                    loadStudents()
                }
            } else {
            // Show error message if fields are empty
            AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Please enter both name and age")
                .setPositiveButton("OK", null)
                .show()
        }
    }
    .setNegativeButton("Cancel", null)
    .show()
}

    private fun showDeleteConfirmationDialog(student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete ${student.name}?")
            .setPositiveButton("Delete") { dialog, which ->
                lifecycleScope.launch {
                    dao.delete(student)
                    loadStudents() // Refresh the list
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}