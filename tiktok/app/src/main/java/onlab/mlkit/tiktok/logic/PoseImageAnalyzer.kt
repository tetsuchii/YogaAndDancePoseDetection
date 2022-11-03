package onlab.mlkit.tiktok.logic

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Logger
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import onlab.mlkit.tiktok.classification.PoseClassifierProcessor
import onlab.mlkit.tiktok.drawing.PoseDrawing
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class PoseImageAnalyzer(
    val poseDrawing: PoseDrawing,
    val poseLogic: PoseLogic, val mode: String, val context: Context
) : ImageAnalysis.Analyzer{

    private var poseDetector : PoseDetector
    private lateinit var poseClassifierProcessor : PoseClassifierProcessor
    private var classificationExecutor: Executor? = null

    init {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .build()
        poseDetector = PoseDetection.getClient(options)
        classificationExecutor = Executors.newSingleThreadExecutor()

    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageP: ImageProxy) {
        val mediaImage = imageP.image
        if(mediaImage != null){
            val image = InputImage.fromMediaImage(mediaImage,imageP.imageInfo.rotationDegrees)
            poseDetector.process(image)
                .addOnSuccessListener {pose ->
                    poseDrawing.drawSkeleton(pose)
                    val type=mode.split(" ")
                    if(type[1] == "yoga"){
                        poseLogic.updatePoseLandmarksYoga(pose,mode,context)
                    }else{
                        poseLogic.updatePoseLandmarksDance(pose.allPoseLandmarks,mode)
                    }
                }
                .addOnFailureListener { e -> println(e) }
                .addOnCompleteListener{
                    mediaImage.close()
                    imageP.close()}
        }
    }
}
