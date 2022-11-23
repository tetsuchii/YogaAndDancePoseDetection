package onlab.mlkit.tiktok.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PoseDao {
    @Query("SELECT * FROM poses")
    fun getAll(): List<Pose>
}