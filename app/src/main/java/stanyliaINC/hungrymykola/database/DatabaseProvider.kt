package stanyliaINC.hungrymykola.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val tempInstance = instance
            if (tempInstance != null) {
                tempInstance
            } else {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                instance = db
                db
            }
        }
    }
}
