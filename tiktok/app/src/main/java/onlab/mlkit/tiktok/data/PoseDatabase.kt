package onlab.mlkit.tiktok.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.impl.WorkDatabaseMigrations.MIGRATION_1_2

@Database(
    entities = [Pose::class],
    version = 3,
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
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
        }
}