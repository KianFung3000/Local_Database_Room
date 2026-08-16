package com.example.local_database_room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDAO {

    // Insert one or more students
    @Insert
    suspend fun insert(vararg student: Student)

    // Update one or more students
    @Update
    suspend fun update(vararg student: Student)

    // Delete one or more students
    @Delete
    suspend fun delete(vararg student: Student)

    // Delete student by ID
    suspend fun deleteStudentById(studentId: Long) {

    }

    // Get all students
    @Query("SELECT * FROM students")
    suspend fun getAllStudents():List<Student>

    // Get students by name
    @Query("SELECT * FROM students WHERE student_name = :studentName")
    suspend fun getStudentsByName(studentName: String): List<Student>

    //Get students by ID
    @Query("SELECT * FROM students WHERE id = :studentId LIMIT 1")
    suspend fun getStudentById(studentId: Long): Student?
}