package onlab.mlkit.tiktok.classification

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.common.base.Preconditions
import com.google.mlkit.vision.pose.Pose
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class PoseClassifierThread @WorkerThread constructor(context: Context) {
    private lateinit var poseClassifier: PoseClassifier
    private val poseSamplesFile = "pose/yoga_poses_3.csv"

    init {
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        usePoseReader(context)
    }

    private fun usePoseReader(context: Context) {
        val poses: MutableList<PoseReader> = ArrayList()
        try {
            val reader = BufferedReader(
                InputStreamReader(context.assets.open(poseSamplesFile))
            )
            var csvLine = reader.readLine()
            while (csvLine != null) {
                val poseReader = PoseReader.readPose(csvLine, ",")
                if (poseReader != null) {
                    poses.add(poseReader)
                }
                csvLine = reader.readLine()
            }
        } catch (e: IOException) {
            Log.e(
                "E",
                "Error when loading pose samples.\n$e"
            )
        }
        poseClassifier = PoseClassifier(poses)

    }

    @WorkerThread
    fun getPoseResult(pose: Pose): MutableList<String> {
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        val result: MutableList<String> = ArrayList()
        val classification = poseClassifier.classify(pose)

        if (pose.allPoseLandmarks.isNotEmpty()) {
            val maxConfidenceClass = classification.maxConfidenceClass
            val maxConfidenceClassResult = String.format(
                Locale.US,
                "%s %.2f",
                maxConfidenceClass, classification.getClassConfidence(maxConfidenceClass)
                        / poseClassifier.confidenceRange()
            )
            result.add(maxConfidenceClassResult)
        }
        return result
    }
}