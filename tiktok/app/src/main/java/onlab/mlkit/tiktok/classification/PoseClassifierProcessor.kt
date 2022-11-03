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

class PoseClassifierProcessor @WorkerThread constructor(context: Context) {
    private var emaSmoothing: EMASmoothing
    private lateinit var poseClassifier: PoseClassifier

    init {
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        emaSmoothing = EMASmoothing()
        loadPoseSamples(context)
    }

    private fun loadPoseSamples(context: Context) {
        val poseSamples: MutableList<PoseSample> = ArrayList()
        try {
            val reader = BufferedReader(
                InputStreamReader(context.assets.open(POSE_SAMPLES_FILE))
            )
            var csvLine = reader.readLine()
            while (csvLine != null) {
                val poseSample = PoseSample.getPoseSample(csvLine, ",")
                if (poseSample != null) {
                    poseSamples.add(poseSample)
                }
                csvLine = reader.readLine()
            }
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Error when loading pose samples.\n$e"
            )
        }
        poseClassifier = PoseClassifier(poseSamples)

    }

    @WorkerThread
    fun getPoseResult(pose: Pose): MutableList<String?> {
        Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper())
        val result: MutableList<String?> = ArrayList()
        var classification = poseClassifier.classify(pose)

        classification = emaSmoothing.getSmoothedResult(classification)


        // Add maxConfidence class of current frame to result if pose is found.
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

    companion object {
        private const val TAG = "PoseClassifierProcessor"
        private const val POSE_SAMPLES_FILE = "pose/yoga_poses_csvs_out_basic.csv"
    }
}