package onlab.mlkit.tiktok.classification

import android.util.Log
import com.google.common.base.Splitter
import com.google.mlkit.vision.common.PointF3D

class PoseReader(val name: String, val className: String, landmarks: List<PointF3D>?) {
    val embedding: List<PointF3D>

    init {
        embedding = PoseEmbedding.getPoseEmbedding(landmarks as MutableList<PointF3D>)
    }

    companion object {
        private const val landmarkNumbers = 33
        private const val dimNumbers = 3
        fun readPose(csvLine: String, separator: String): PoseReader? {
            val tokens = Splitter.onPattern(separator.toString()).splitToList(csvLine.toString())
            if (tokens.size != landmarkNumbers * dimNumbers + 2) {
                Log.e("E", "Invalid number of tokens for PoseSample")
                return null
            }
            val name = tokens[0]
            val className = tokens[1]
            val landmarks: MutableList<PointF3D> = ArrayList()
            var i = 2
            while (i < tokens.size) {
                try {
                    landmarks.add(
                        PointF3D.from(
                            tokens[i].toFloat(),
                            tokens[i + 1].toFloat(),
                            tokens[i + 2].toFloat()
                        )
                    )
                } catch (e: NullPointerException) {
                    Log.e("E", "Invalid value " + tokens[i] + " for landmark position.")
                    return null
                } catch (e: NumberFormatException) {
                    Log.e("E", "Invalid value " + tokens[i] + " for landmark position.")
                    return null
                }
                i += dimNumbers
            }
            return PoseReader(name, className, landmarks)
        }
    }
}