package onlab.mlkit.tiktok.classification

import android.util.Pair
import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.Pose
import onlab.mlkit.tiktok.classification.PoseEmbedding.getPoseEmbedding
import onlab.mlkit.tiktok.classification.Utils.maxAbs
import onlab.mlkit.tiktok.classification.Utils.multiply
import onlab.mlkit.tiktok.classification.Utils.multiplyAll
import onlab.mlkit.tiktok.classification.Utils.subtract
import onlab.mlkit.tiktok.classification.Utils.sumAbs
import java.lang.Float.max
import java.util.*
import kotlin.math.min

class PoseClassifier constructor(
  private val poseSamples: List<PoseSample>,
  private val maxDistanceTopK: Int = MAX_DISTANCE_TOP_K,
  private val meanDistanceTopK: Int = MEAN_DISTANCE_TOP_K,
  private val axesWeights: PointF3D = AXES_WEIGHTS
) {
  fun confidenceRange(): Int {
    return min(maxDistanceTopK, meanDistanceTopK)
  }

  fun classify(pose: Pose): ClassificationResult {
    return classify(extractPoseLandmarks(pose))
  }

  private fun classify(landmarks: List<PointF3D?>): ClassificationResult {
    val result = ClassificationResult()
    if (landmarks.isEmpty()) {
      return result
    }
    val flippedLandmarks: MutableList<PointF3D?> = ArrayList(landmarks)
    multiplyAll(flippedLandmarks, PointF3D.from(-1f, 1f, 1f))
    val embedding: List<PointF3D> = getPoseEmbedding(landmarks as MutableList<PointF3D?>)
    val flippedEmbedding: List<PointF3D> = getPoseEmbedding(flippedLandmarks)
    val maxDistances = PriorityQueue(maxDistanceTopK)
    { o1: Pair<PoseSample, Float?>, o2: Pair<PoseSample, Float?> -> -(o1.second!!).compareTo(o2.second!!) }
    for (poseSample in poseSamples) {
      val sampleEmbedding = poseSample.embedding
      var originalMax = 0f
      var flippedMax = 0f
      for (i in embedding.indices) {
        originalMax = max(originalMax, maxAbs(multiply(subtract(embedding[i], sampleEmbedding[i]), axesWeights)))
        flippedMax = max(flippedMax, maxAbs(multiply(subtract(flippedEmbedding[i], sampleEmbedding[i]), axesWeights)))
      }
      maxDistances.add(Pair(poseSample, min(originalMax, flippedMax)))
      if (maxDistances.size > maxDistanceTopK) {
        maxDistances.poll()
      }
    }
    val meanDistances = PriorityQueue(meanDistanceTopK)
    { o1: Pair<PoseSample, Float?>, o2: Pair<PoseSample, Float?> -> -(o1.second!!).compareTo(o2.second!!) }
    for (sampleDistances in maxDistances) {
      val poseSample = sampleDistances.first
      val sampleEmbedding = poseSample.embedding
      var originalSum = 0f
      var flippedSum = 0f
      for (i in embedding.indices) {
        originalSum += sumAbs(multiply(subtract(embedding[i], sampleEmbedding[i]), axesWeights))
        flippedSum += sumAbs(multiply(subtract(flippedEmbedding[i], sampleEmbedding[i]), axesWeights))
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

  companion object {
    private const val MAX_DISTANCE_TOP_K = 30
    private const val MEAN_DISTANCE_TOP_K = 10

    // Note Z has a lower weight as it is generally less accurate than X & Y.
    private val AXES_WEIGHTS = PointF3D.from(1f, 1f, 0.2f)
    private fun extractPoseLandmarks(pose: Pose): List<PointF3D?> {
      val landmarks: MutableList<PointF3D?> = ArrayList()
      for (poseLandmark in pose.allPoseLandmarks) {
        landmarks.add(poseLandmark.position3D)
      }
      return landmarks
    }
  }
}