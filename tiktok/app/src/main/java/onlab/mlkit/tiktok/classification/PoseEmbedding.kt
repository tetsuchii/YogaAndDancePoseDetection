package onlab.mlkit.tiktok.classification

import com.google.mlkit.vision.common.PointF3D
import com.google.mlkit.vision.pose.PoseLandmark
import onlab.mlkit.tiktok.classification.Utils.average
import onlab.mlkit.tiktok.classification.Utils.l2Norm2D
import onlab.mlkit.tiktok.classification.Utils.multiplyAllFloat
import onlab.mlkit.tiktok.classification.Utils.subtract
import onlab.mlkit.tiktok.classification.Utils.subtractAll

object PoseEmbedding {
  private const val TORSO_MULTIPLIER = 2.5f
  fun getPoseEmbedding(landmarks: MutableList<PointF3D?>): MutableList<PointF3D> {
    val normalizedLandmarks = normalize(landmarks)
    return getEmbedding(normalizedLandmarks)
  }

  private fun normalize(landmarks: MutableList<PointF3D?>): List<PointF3D> {
      landmarks as MutableList<PointF3D>
      val normalizedLandmarks: MutableList<PointF3D> = ArrayList(landmarks)
      val center: PointF3D = average(
        landmarks[PoseLandmark.LEFT_HIP], landmarks[PoseLandmark.RIGHT_HIP]
      )
      subtractAll(center, normalizedLandmarks)

      multiplyAllFloat(normalizedLandmarks, 1 / getPoseSize(normalizedLandmarks))
      return normalizedLandmarks
  }

  private fun getPoseSize(landmarks: List<PointF3D>): Float {
    val hipsCenter: PointF3D = average(
      landmarks[PoseLandmark.LEFT_HIP], landmarks[PoseLandmark.RIGHT_HIP]
    )
    val shouldersCenter: PointF3D = average(
      landmarks[PoseLandmark.LEFT_SHOULDER],
      landmarks[PoseLandmark.RIGHT_SHOULDER]
    )
    val torsoSize: Float = l2Norm2D(subtract(hipsCenter, shouldersCenter))
    var maxDistance = torsoSize * TORSO_MULTIPLIER
    for (landmark in landmarks) {
      val distance: Float = l2Norm2D(subtract(hipsCenter, landmark))
      if (distance > maxDistance) {
        maxDistance = distance
      }
    }
    return maxDistance
  }

  private fun getEmbedding(lm: List<PointF3D>): MutableList<PointF3D> {
    val embedding: MutableList<PointF3D> = ArrayList()

    // One joint.
    embedding.add(
      subtract(
        average(lm[PoseLandmark.LEFT_HIP], lm[PoseLandmark.RIGHT_HIP]),
        average(lm[PoseLandmark.LEFT_SHOULDER], lm[PoseLandmark.RIGHT_SHOULDER]))
    )
    embedding.add(subtract(lm[PoseLandmark.LEFT_SHOULDER], lm[PoseLandmark.LEFT_ELBOW]))
    embedding.add(subtract(lm[PoseLandmark.RIGHT_SHOULDER], lm[PoseLandmark.RIGHT_ELBOW]))
    embedding.add(subtract(lm[PoseLandmark.LEFT_ELBOW], lm[PoseLandmark.LEFT_WRIST]))
    embedding.add(subtract(lm[PoseLandmark.RIGHT_ELBOW], lm[PoseLandmark.RIGHT_WRIST]))
    embedding.add(subtract(lm[PoseLandmark.LEFT_HIP], lm[PoseLandmark.LEFT_KNEE]))
    embedding.add(subtract(lm[PoseLandmark.RIGHT_HIP], lm[PoseLandmark.RIGHT_KNEE]))
    embedding.add(subtract(lm[PoseLandmark.LEFT_KNEE], lm[PoseLandmark.LEFT_ANKLE]))
    embedding.add(subtract(lm[PoseLandmark.RIGHT_KNEE], lm[PoseLandmark.RIGHT_ANKLE]))

    // Two joints.
    embedding.add(
      subtract(
        lm[PoseLandmark.LEFT_SHOULDER], lm[PoseLandmark.LEFT_WRIST]
      )
    )
    embedding.add(
      subtract(
        lm[PoseLandmark.RIGHT_SHOULDER], lm[PoseLandmark.RIGHT_WRIST]
      )
    )
    embedding.add(subtract(lm[PoseLandmark.LEFT_HIP], lm[PoseLandmark.LEFT_ANKLE]))
    embedding.add(subtract(lm[PoseLandmark.RIGHT_HIP], lm[PoseLandmark.RIGHT_ANKLE]))

    // Four joints.
    embedding.add(subtract(lm[PoseLandmark.LEFT_HIP], lm[PoseLandmark.LEFT_WRIST]))
    embedding.add(subtract(lm[PoseLandmark.RIGHT_HIP], lm[PoseLandmark.RIGHT_WRIST]))

    // Five joints.
    embedding.add(
      subtract(
        lm[PoseLandmark.LEFT_SHOULDER], lm[PoseLandmark.LEFT_ANKLE]
      )
    )
    embedding.add(
      subtract(
        lm[PoseLandmark.RIGHT_SHOULDER], lm[PoseLandmark.RIGHT_ANKLE]
      )
    )
    embedding.add(subtract(lm[PoseLandmark.LEFT_HIP], lm[PoseLandmark.LEFT_WRIST]))
    embedding.add(subtract(lm[PoseLandmark.RIGHT_HIP], lm[PoseLandmark.RIGHT_WRIST]))

    // Cross body.
    embedding.add(subtract(lm[PoseLandmark.LEFT_ELBOW], lm[PoseLandmark.RIGHT_ELBOW]))
    embedding.add(subtract(lm[PoseLandmark.LEFT_KNEE], lm[PoseLandmark.RIGHT_KNEE]))
    embedding.add(subtract(lm[PoseLandmark.LEFT_WRIST], lm[PoseLandmark.RIGHT_WRIST]))
    embedding.add(subtract(lm[PoseLandmark.LEFT_ANKLE], lm[PoseLandmark.RIGHT_ANKLE]))
    return embedding
  }
}