package onlab.mlkit.tiktok.classification

import android.util.Pair
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import onlab.mlkit.tiktok.classification.PoseEmbedding.getPoseEmbedding
import onlab.mlkit.tiktok.classification.PointOperations.maxAbs
import onlab.mlkit.tiktok.classification.PointOperations.multiply
import onlab.mlkit.tiktok.classification.PointOperations.multiplyAll
import onlab.mlkit.tiktok.classification.PointOperations.subtract
import onlab.mlkit.tiktok.classification.PointOperations.sumAbs
import java.lang.Float.max
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

class PoseClassifier constructor(
    private val poseReaders: List<PoseReader>,
    private val maxDistanceTopK: Int = 30,
    private val meanDistanceTopK: Int = 10,
    private val axesWeights: PointF3D = PointF3D.from(1f, 1f, 0.2f)
) {
    fun confidenceRange(): Int {
        return min(maxDistanceTopK, meanDistanceTopK)
    }

    fun classify(pose: Pose): ClassificationResult {
        val landmarks : MutableList<PointF3D> = ArrayList()
        landmarks.let {
            for (poseLandmark in pose.allPoseLandmarks) {
                it.add(poseLandmark.position3D)
            }
        }
        val result = ClassificationResult()
        if (landmarks.isEmpty()) {
            return result
        }
        val flippedLandmarks: MutableList<PointF3D> = ArrayList(landmarks)
        multiplyAll(flippedLandmarks, PointF3D.from(-1f, 1f, 1f))
        val embedding = getPoseEmbedding(landmarks)
        val flippedEmbedding: List<PointF3D> = getPoseEmbedding(flippedLandmarks)
        val maxDistances = PriorityQueue(maxDistanceTopK)
        { o1: Pair<PoseReader, Float?>, o2: Pair<PoseReader, Float?> -> -(o1.second!!).compareTo(o2.second!!) }
        for (poseSample in poseReaders) {
            val sampleEmbedding = poseSample.embedding
            var originalMax = 0f
            var flippedMax = 0f
            for (i in embedding.indices) {
                originalMax = max(
                    originalMax,
                    maxAbs(multiply(subtract(embedding[i], sampleEmbedding[i]), axesWeights))
                )
                flippedMax = max(
                    flippedMax,
                    maxAbs(multiply(subtract(flippedEmbedding[i], sampleEmbedding[i]), axesWeights))
                )
            }
            maxDistances.add(Pair(poseSample, min(originalMax, flippedMax)))
            if (maxDistances.size > maxDistanceTopK) {
                maxDistances.poll()
            }
        }
        val meanDistances = PriorityQueue(meanDistanceTopK)
        { o1: Pair<PoseReader, Float?>, o2: Pair<PoseReader, Float?> -> -(o1.second!!).compareTo(o2.second!!) }
        for (sampleDistances in maxDistances) {
            val poseSample = sampleDistances.first
            val sampleEmbedding = poseSample.embedding
            var originalSum = 0f
            var flippedSum = 0f
            for (i in embedding.indices) {
                originalSum += sumAbs(
                    multiply(
                        subtract(embedding[i], sampleEmbedding[i]),
                        axesWeights
                    )
                )
                flippedSum += sumAbs(
                    multiply(
                        subtract(flippedEmbedding[i], sampleEmbedding[i]),
                        axesWeights
                    )
                )
            }
            val meanDistance = min(originalSum, flippedSum) / (embedding.size * 2)
            meanDistances.add(Pair(poseSample, meanDistance))
            if (meanDistances.size > meanDistanceTopK) {
                meanDistances.poll()
            }
        }
        for (sampleDistances in meanDistances) {
            val className = sampleDistances.first.className
            result.incrementClassConfidence(className)
        }
        return result
    }
}
