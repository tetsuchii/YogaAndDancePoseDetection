package onlab.mlkit.tiktok

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.size
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import onlab.mlkit.tiktok.databinding.ActivityCameraBinding
import onlab.mlkit.tiktok.drawing.RectOverlay
import onlab.mlkit.tiktok.logic.PoseImageAnalyzer
import onlab.mlkit.tiktok.logic.PoseLogic
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class CameraActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraBinding


    private lateinit var cameraExecutor: ExecutorService

    private val poseLogic = PoseLogic(this)
    private lateinit var analyser : PoseImageAnalyzer
    private lateinit var rectOverlay: RectOverlay
    private var modeDance by Delegates.notNull<Int>()
    private var originalCameraImage : Bitmap? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        rectOverlay=viewBinding.rectOverlay
        modeDance = if(intent.type=="learn")
            0
        else
            1

        //analyser=PoseImageAnalyzer(poseLogic,modeDance)

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

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(480, 360))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), (PoseImageAnalyzer(::onTextFound)))

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, imageAnalysis, preview)

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

    companion object {
        private const val TAG = "TikTokDanceApp"
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun changeStepImage(number : Int){

        when(number) {
            1 -> viewBinding.stepPhoto.setImageResource(R.drawable.first)
            2 -> viewBinding.stepPhoto.setImageResource(R.drawable.second)
            3 -> viewBinding.stepPhoto.setImageResource(R.drawable.third)
            4 -> viewBinding.stepPhoto.setImageResource(R.drawable.fourth)
            5 -> viewBinding.stepPhoto.setImageResource(R.drawable.fifth)
            6 -> viewBinding.stepPhoto.setImageResource(R.drawable.sixth)
        }
    }
    fun showOk (){
        viewBinding.like.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
            {
                viewBinding.like.visibility = View.INVISIBLE
            },
            3000 // value in milliseconds
        )
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
            val leftEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER)
            val leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER)
            val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)
            val rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER)
            val rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER)

            val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
            val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
            val nose = pose.getPoseLandmark(PoseLandmark.NOSE)

            val leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)
            val rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)

            rectOverlay.clear()

            for (landmark in pose.allPoseLandmarks) {
                rectOverlay.drawCircle(landmark)
            }





 // Face
    rectOverlay.drawLine(nose, leftEyeInner)
    rectOverlay.drawLine(leftEyeInner, leftEye)
    rectOverlay.drawLine(leftEye, leftEyeOuter)
    rectOverlay.drawLine(leftEyeOuter, leftEar)
    rectOverlay.drawLine(nose, rightEyeInner)
    rectOverlay.drawLine(rightEyeInner, rightEye)
    rectOverlay.drawLine(rightEye, rightEyeOuter)
    rectOverlay.drawLine(rightEyeOuter, rightEar)
    rectOverlay.drawLine(leftMouth, rightMouth)

    rectOverlay.drawLine(leftShoulder, rightShoulder)
    rectOverlay.drawLine(leftHip, rightHip)

    // Left body
    rectOverlay.drawLine(leftShoulder, leftElbow)
    rectOverlay.drawLine(leftElbow, leftWrist)
    rectOverlay.drawLine(leftShoulder, leftHip)
    rectOverlay.drawLine(leftHip, leftKnee)
    rectOverlay.drawLine(leftKnee, leftAnkle)
    rectOverlay.drawLine(leftWrist, leftThumb)
    rectOverlay.drawLine(leftWrist, leftPinky)
    rectOverlay.drawLine(leftWrist, leftIndex)
    rectOverlay.drawLine(leftIndex, leftPinky)
    rectOverlay.drawLine(leftAnkle, leftHeel)
    rectOverlay.drawLine(leftHeel, leftFootIndex)

    // Right body
    rectOverlay.drawLine(rightShoulder, rightElbow)
    rectOverlay.drawLine(rightElbow, rightWrist)
    rectOverlay.drawLine(rightShoulder, rightHip)
    rectOverlay.drawLine(rightHip, rightKnee)
    rectOverlay.drawLine(rightKnee, rightAnkle)
    rectOverlay.drawLine(rightWrist, rightThumb)
    rectOverlay.drawLine(rightWrist, rightPinky)
    rectOverlay.drawLine(rightWrist, rightIndex)
    rectOverlay.drawLine(rightIndex, rightPinky)
    rectOverlay.drawLine(rightAnkle, rightHeel)
    rectOverlay.drawLine(rightHeel, rightFootIndex)

        } catch (e: java.lang.Exception) {
            Toast.makeText(this@CameraActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }


    fun showBigLike() {
        viewBinding.like.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
            {
                viewBinding.bigLike.visibility = View.INVISIBLE
            },
            8000 // value in milliseconds
        )    }

}