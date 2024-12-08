package com.cs407.user_data

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import java.sql.Time
import java.util.Date

@Entity
data class Class(
    @PrimaryKey val classId: Int,
    val className: String,
)

@Entity
data class Student(
    @PrimaryKey val studentId: Int,
    val name: String,
    val email: String
)

@Entity
data class Teacher(
    @PrimaryKey val teacherId: Int,
    val name: String,
    val email: String
)

@Entity
data class Message(
    @PrimaryKey val messageId: Int,
    val classId: Int,
    val senderName: String,
    val message: String,
    val time: Time,
    val date: Date
)

@Entity(
    primaryKeys = ["classId", "studentId"],
    foreignKeys = [
        ForeignKey(entity = Class::class, parentColumns = ["classId"], childColumns = ["classId"]),
        ForeignKey(entity = Student::class, parentColumns = ["studentId"], childColumns = ["studentId"])
    ]
)
data class StudentClassRelation(
    val classId: Int,
    val studentId: Int
)

@Entity(
    primaryKeys = ["classId", "teacherId"],
    foreignKeys = [
        ForeignKey(entity = Class::class, parentColumns = ["classId"], childColumns = ["classId"]),
        ForeignKey(entity = Teacher::class, parentColumns = ["teacherId"], childColumns = ["teacherId"])
    ]
)
data class TeacherClassRelation(
    val classId: Int,
    val teacherId: Int
)

@Dao
interface ClassDao {
    @Insert(entity = Class::class)
    suspend fun insertClass(_class: Class)

    @Query("SELECT * FROM Class WHERE classId = :classId")
    suspend fun getClassById(classId: Int): Class?
}

@Dao
interface StudentDao {
    @Insert(entity = Student::class)
    suspend fun insertStudent(student: Student)

    @Query("SELECT * FROM Student WHERE studentId = :studentId")
    suspend fun getStudentById(studentId: Int): Student?

    @Query(
        """SELECT Student.* FROM Student, Class, StudentClassRelation
            WHERE Class.classId = :classId
                AND StudentClassRelation.studentId = Student.studentId
                AND Class.classId = StudentClassRelation.classId
                """
    )
    suspend fun getStudentsInClass(classId: Int): List<Student>
}

@Dao
interface TeacherDao {
    @Insert(entity = Teacher::class)
    suspend fun insertTeacher(teacher: Teacher)

    @Query("SELECT * FROM Teacher WHERE teacherId = :teacherId")
    suspend fun getTeacherById(teacherId: String): Teacher?

    @Query(
        """SELECT Teacher.* FROM Teacher, Class, TeacherClassRelation
            WHERE Class.classId = :classId
                AND TeacherClassRelation.teacherId = Teacher.teacherId
                AND Class.classId = TeacherClassRelation.classId
                """
    )
    suspend fun getTeachersInClass(classId: Int): List<Teacher>
}

@Dao
interface MessageDao {
    @Insert(entity = Message::class)
    suspend fun insertMessage(message: Message)

    @Query("SELECT * FROM Message WHERE classId = :classId AND date = :date")
    suspend fun getMessagesByClassIdAndDay(classId: Int, date: Date): List<Message>

    @Query("SELECT * FROM Message WHERE classId = :classId AND time = :time")
    suspend fun getMessagesByClassIdAfterTime(classId: Int, time: Time): List<Message>
}

@Dao
interface DeleteDao {
    @Query("DELETE FROM student WHERE studentId = :studentId")
    suspend fun deleteStudent(studentId: Int)
}

@Database(
    entities = [Class::class, Student::class, Teacher::class, StudentClassRelation::class,
        TeacherClassRelation::class, Message::class],
    version = 1
)
abstract class ClassDatabase : RoomDatabase() {

    abstract fun classDao(): ClassDao
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile private var instance: ClassDatabase? = null

        fun getInstance(context: Context): ClassDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ClassDatabase::class.java,
                    "Class Database"
                ).build()
            }
    }
}
