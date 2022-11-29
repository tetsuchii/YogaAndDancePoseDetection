package onlab.mlkit.tiktok.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Pose::class],
    version = 4,
    exportSchema = false
)
abstract class PoseDatabase : RoomDatabase(){
    abstract fun poseDao(): PoseDao

    companion object {
        fun getDatabase(applicationContext: Context): PoseDatabase {
            return Room.databaseBuilder(applicationContext, PoseDatabase::class.java, "poses.db")
                .createFromAsset("database/poses.db")
                .addMigrations(MIGRATION_1_2)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }


        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
    }
}