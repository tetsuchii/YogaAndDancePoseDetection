package onlab.mlkit.tiktok.data

data class Details (
    val name : String,
    val description : String,
    val detailedDescription: String,
    val type:Type
)

enum class Type{
DANCE,YOGA
}
