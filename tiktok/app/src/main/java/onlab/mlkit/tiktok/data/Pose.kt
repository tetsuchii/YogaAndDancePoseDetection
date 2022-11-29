package onlab.mlkit.tiktok.data


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "poses")
data class Pose (
    @PrimaryKey val ID: Int,
    @ColumnInfo(name = "Name")val name : String,
    @ColumnInfo(name = "Description")val description : String,
    @ColumnInfo(name = "DetailedDescription")val detailedDescription: String,
    @ColumnInfo(name = "Type")var type:Type,
    @ColumnInfo(name = "PregnantSafe") val pregnantSafe : Boolean,
    @ColumnInfo(name = "InjurySafe")val injurySafe: Boolean
) {
    enum class Type {
        DANCE, YOGA;

        companion object {
            @JvmStatic
            @TypeConverter
            fun toType(value: String): Type? {
                return enumValueOf<Type>(value)
            }
        }
    }

}

