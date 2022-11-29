package onlab.mlkit.tiktok.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PoseDao {
    @Query("SELECT * FROM poses")
    fun getAll(): List<Pose>

    @Query("SELECT * FROM poses WHERE PregnantSafe = 1 ")
    fun getPregSafe(): List<Pose>

    @Query("SELECT * FROM poses WHERE InjurySafe = 1")
    fun getInjSafe(): List<Pose>

    @Query("SELECT * FROM poses WHERE InjurySafe=1 and PregnantSafe = 1")
    fun getInjPregSafe(): List<Pose>
}