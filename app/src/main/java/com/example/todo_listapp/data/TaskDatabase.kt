package com.example.todo_listapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todo_listapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            //db operations
            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insertTask(Task(name = "Wash the dishes", completed = true))
                dao.insertTask(Task(name = "Do the Laundry"))
                dao.insertTask(Task(name = "Call the Manager", important = true))
                dao.insertTask(Task(name = "Prepare the supper"))
                dao.insertTask(Task(name = "Schedule for tomorrow's activity", completed = true))
                dao.insertTask(Task(name = "Go for morning run"))
                dao.insertTask(Task(name = "Attend the meeting"))
                dao.insertTask(Task(name = "Go for lunch"))
                dao.insertTask(Task(name = "Finish the side project"))
            }
        }
    }

}