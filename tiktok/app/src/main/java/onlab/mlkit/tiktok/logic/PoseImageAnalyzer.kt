package onlab.mlkit.tiktok.logic

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlin.reflect.KFunction1

class PoseImageAnalyzer(
    val poseFoundListener: KFunction1<Pose, Unit>,
    /*val poseLogic: PoseLogic, val mode: Int*/) : ImageAnalysis.Analyzer{

    private lateinit var poseDetector : PoseDetector

    init {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .build()
        poseDetector = PoseDetection.getClient(options)
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageP: ImageProxy) {
        val mediaImage = imageP.image
        if(mediaImage != null){
            val image = InputImage.fromMediaImage(mediaImage,imageP.imageInfo.rotationDegrees)
            poseDetector.process(image)
                .addOnSuccessListener {pose ->
                    poseFoundListener(pose)
                    /*results -> poseLogic.updatePoseLandmarks(results.allPoseLandmarks,mode)*/
                }
                .addOnFailureListener { e -> println(e) }
                .addOnCompleteListener{
                    mediaImage.close()
                    imageP.close()}
        }
    }
}