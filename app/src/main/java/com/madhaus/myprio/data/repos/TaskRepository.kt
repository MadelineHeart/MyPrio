package com.madhaus.myprio.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

interface TaskRepository {
    suspend fun fetchTasks(): List<Task>
    fun fetchTasksFlow(): Flow<List<Task>>
    suspend fun fetchTask(taskId: UUID): Task?

    suspend fun saveTask(newTask: Task): Boolean
    suspend fun deleteTask(toDelete: UUID): Boolean
}

/** Room Database Objects **/
@Dao
interface TaskDAO {
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id IN (:taskId)")
    suspend fun fetchTask(taskId: UUID): Task?

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}

@Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDAO
}
/** End Room Database Objects **/

class TaskRepositoryImpl(appContext: Context): TaskRepository {
    private val db: AppDatabase

    init {
        db = Room.databaseBuilder(appContext,
            AppDatabase::class.java, "task-db").build()
    }

    override suspend fun fetchTasks(): List<Task> {
        return db.taskDao().getAllTasks()
    }

    override fun fetchTasksFlow(): Flow<List<Task>> {
        return db.taskDao().getAllTasksFlow()
    }

    override suspend fun fetchTask(taskId: UUID): Task? {
        return db.taskDao().fetchTask(taskId)
    }

    override suspend fun saveTask(newTask: Task): Boolean {
        db.taskDao().fetchTask(newTask.id)?.let { db.taskDao().updateTask(newTask) }
            ?: db.taskDao().insertTask(newTask)
        return true
    }

    override suspend fun deleteTask(toDelete: UUID): Boolean {
        return db.taskDao().fetchTask(toDelete)?.let {
            db.taskDao().deleteTask(it)
            true } ?: false
    }
}
