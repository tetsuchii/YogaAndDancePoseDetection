package com.example.posedetection_game

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import androidx.camera.core.ImageAnalysis
import com.example.posedetection_game.databinding.ActivityMainBinding
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.atan2


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var rectOverlay: RectOverlay
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor,PoseAnalyzer(::onTextFound))
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector ,preview, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun onTextFound(pose: Pose)  {
        try {
            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
            val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
            val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
            val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
            val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
            val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
            val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
            val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
            val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
            val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
            val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
            val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
            val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
            val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
            val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
            val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
            val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
            val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
            val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
            val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
            val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)

            val leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
            val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)

            val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
            val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)

            rectOverlay.clear()

            // disegno il collo come la media tra occhi e orecchie
            if( leftEye != null && rightEye != null && leftShoulder != null && rightShoulder != null  ){
                rectOverlay.drawNeck(leftEye, rightEye, leftShoulder, rightShoulder);
            }

            // disegno il collo visto lateralmente da sinistra
            if(leftEar != null && leftShoulder != null){
                rectOverlay.drawLine(leftEar, leftShoulder)
            }

            // disegno il collo visto lateralmente da destra
            if(rightEar != null && rightShoulder != null){
                rectOverlay.drawLine(rightEar, rightShoulder)
            }

            if(leftShoulder != null && rightShoulder != null){
                rectOverlay.drawLine(leftShoulder, rightShoulder)
            }

            if(leftHip != null &&  rightHip != null){
                rectOverlay.drawLine(leftHip, rightHip)
            }

            if(leftShoulder != null &&  leftElbow != null){
                rectOverlay.drawLine(leftShoulder, leftElbow)
            }

            if(leftElbow != null &&  leftWrist != null){
                rectOverlay.drawLine(leftElbow, leftWrist)
            }

            if(leftShoulder != null &&  leftHip != null){
                rectOverlay.drawLine(leftShoulder, leftHip)
            }

            if(leftHip != null &&  leftKnee != null){
                rectOverlay.drawLine(leftHip, leftKnee)
            }

            if(leftKnee != null &&  leftAnkle != null){
                rectOverlay.drawLine(leftKnee, leftAnkle)
            }

            if(leftWrist != null &&  leftThumb != null){
                rectOverlay.drawLine(leftWrist, leftThumb)
            }

            if(leftWrist != null &&  leftPinky != null){
                rectOverlay.drawLine(leftWrist, leftPinky)
            }

            if(leftWrist != null &&  leftIndex != null){
                rectOverlay.drawLine(leftWrist, leftIndex)
            }

            if(leftIndex != null &&  leftPinky != null){
                rectOverlay.drawLine(leftIndex, leftPinky)
            }

            if(leftAnkle != null &&  leftHeel != null){
                rectOverlay.drawLine(leftAnkle, leftHeel)
            }

            if(leftHeel != null &&  leftFootIndex != null){
                rectOverlay.drawLine(leftHeel, leftFootIndex)
            }

            if(rightShoulder != null &&  rightElbow != null){
                rectOverlay.drawLine(rightShoulder, rightElbow)
            }

            if(rightElbow != null &&  rightWrist != null){
                rectOverlay.drawLine(rightElbow, rightWrist)
            }

            if(rightShoulder != null &&  rightHip != null){
                rectOverlay.drawLine(rightShoulder, rightHip)
            }

            if(rightHip != null &&  rightKnee != null){
                rectOverlay.drawLine(rightHip, rightKnee)
            }

            if(rightKnee != null &&  rightAnkle != null){
                rectOverlay.drawLine(rightKnee, rightAnkle)
            }

            if(rightWrist != null &&  rightThumb != null){
                rectOverlay.drawLine(rightWrist, rightThumb)
            }

            if(rightWrist != null &&  rightPinky != null){
                rectOverlay.drawLine(rightWrist, rightPinky)
            }

            if(rightWrist != null &&  rightIndex != null){
                rectOverlay.drawLine(rightWrist, rightIndex)
            }

            if(rightIndex != null &&  rightPinky != null){
                rectOverlay.drawLine(rightIndex, rightPinky)
            }

            if(rightAnkle != null &&  rightHeel != null){
                rectOverlay.drawLine(rightAnkle, rightHeel)
            }

            if(rightHeel != null &&  rightFootIndex != null){
                rectOverlay.drawLine(rightHeel, rightFootIndex)
            }

        } catch (e: java.lang.Exception) {
            Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}