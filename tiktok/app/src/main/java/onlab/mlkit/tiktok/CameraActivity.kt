package onlab.mlkit.tiktok

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import onlab.mlkit.tiktok.databinding.ActivityCameraBinding
import onlab.mlkit.tiktok.logic.PoseImageAnalyzer
import onlab.mlkit.tiktok.logic.PoseLogic
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class CameraActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraBinding

    private lateinit var cameraExecutor: ExecutorService

    private val poseLogic = PoseLogic(this)
    private lateinit var analyser : PoseImageAnalyzer
    private var modeDance by Delegates.notNull<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        modeDance = if(intent.type=="learn")
            0
        else
            1

        analyser=PoseImageAnalyzer(poseLogic,modeDance)

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

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), analyser)

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

    fun showBigLike() {
        viewBinding.like.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
            {
                viewBinding.bigLike.visibility = View.INVISIBLE
            },
            8000 // value in milliseconds
        )    }

}