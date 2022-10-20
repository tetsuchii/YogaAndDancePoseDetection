package onlab.mlkit.tiktok.logic

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.mlkit.vision.pose.PoseLandmark
import onlab.mlkit.tiktok.CameraActivity
import onlab.mlkit.tiktok.MainActivity
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

class PoseLogic(private val activity: CameraActivity) {
    private var poseLandmarks : List<PoseLandmark> = listOf()
    private var firstInit = true
    private var kneeToKnee = 0.0f
    private var ankleToAnkle = 0.0f
    private var stepCounter = 0


    fun updatePoseLandmarks(newList : List<PoseLandmark>,mode : Int) {
        poseLandmarks = newList

        val rightAnkle = poseLandmarks.find { it.landmarkType == PoseLandmark.RIGHT_ANKLE }
        val leftAnkle = poseLandmarks.find { it.landmarkType == PoseLandmark.LEFT_ANKLE }
        val rightKnee = poseLandmarks.find { it.landmarkType == PoseLandmark.RIGHT_KNEE }
        val leftKnee = poseLandmarks.find { it.landmarkType == PoseLandmark.LEFT_KNEE }

        if (mode == 0) {

            if (firstInit && rightKnee != null && rightAnkle != null && leftAnkle != null && leftKnee != null) {
                firstInit = false
                kneeToKnee = abs(rightKnee.position.x - leftKnee.position.x)
                ankleToAnkle = abs(rightAnkle.position.x - leftAnkle.position.x)
                Log.i("PoseLogic", "INIT OK")
            } else {
                if (stepCounter == 0 && checkFirstStep()) {
                    stepCounter++
                    Log.i("PoseLogic", "firstStep ok")
                    activity.showOk()
                    activity.changeStepImage(2)
                }
                if (stepCounter == 1 && checkSecondStep()) {
                    stepCounter++
                    Log.i("PoseLogic", "secondStep ok")
                    activity.showOk()
                    activity.changeStepImage(3)
                }
                if (stepCounter == 2 && checkThirdStep()) {
                    stepCounter++
                    Log.i("PoseLogic", "thirdStep ok")
                    activity.showOk()
                    activity.changeStepImage(4)
                }
                if (stepCounter == 3 && checkFourthStep()) {
                    stepCounter++
                    Log.i("PoseLogic", "fourthStep ok")
                    activity.showOk()
                    activity.changeStepImage(5)
                }
                if (stepCounter == 4 && checkSecondStep()) {
                    stepCounter++
                    Log.i("PoseLogic", "fifthStep ok")
                    activity.showOk()
                    activity.changeStepImage(6)
                }
                if (stepCounter == 5 && checkThirdStep()) {
                    stepCounter++
                    Log.i("PoseLogic", "sixthStep ok")
                    activity.showOk()
                }
            }
        } else {
            var score=0
            if (firstInit && rightKnee != null && rightAnkle != null && leftAnkle != null && leftKnee != null) {
                firstInit = false
                kneeToKnee = abs(rightKnee.position.x - leftKnee.position.x)
                ankleToAnkle = abs(rightAnkle.position.x - leftAnkle.position.x)
                Log.i("PoseLogic Practice", "INIT OK")
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        if (checkFirstStep())
                        score++
                        activity.changeStepImage(2)
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                if (checkSecondStep())
                                    score++
                                activity.changeStepImage(3)
                                Handler(Looper.getMainLooper()).postDelayed(
                                    {
                                        if (checkThirdStep())
                                            score++
                                        activity.changeStepImage(4)
                                        Handler(Looper.getMainLooper()).postDelayed(
                                            {
                                                if (checkFourthStep())
                                                    score++
                                                activity.changeStepImage(5)
                                                Handler(Looper.getMainLooper()).postDelayed(
                                                    {
                                                        if (checkSecondStep())
                                                            score++
                                                        activity.changeStepImage(6)
                                                        Handler(Looper.getMainLooper()).postDelayed(
                                                            {
                                                                if (checkThirdStep())
                                                                    score++
                                                                if(score > 3)
                                                                    activity.showBigLike()
                                                            },
                                                            2000)
                                                    },
                                                    2000)
                                            },
                                            2000)
                                    },
                                    2000) // value in milliseconds
                            },
                            2000) // value in milliseconds
                    },
                    2000 // value in milliseconds
                )
            }
        }
    }

    private fun checkFirstStep(): Boolean {
        val rightAnkle = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_ANKLE }
        val leftAnkle = poseLandmarks.find {it.landmarkType==PoseLandmark.LEFT_ANKLE}
        val rightKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_KNEE }
        val leftKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.LEFT_KNEE }

        if( rightKnee != null && rightAnkle != null && leftAnkle != null && leftKnee != null){
            if((abs(rightAnkle.position.y-leftKnee.position.y) <10).xor(abs(rightKnee.position.y-leftAnkle.position.y)<10)){
                return true
            }
        }
        return false
    }

    private fun checkSecondStep(): Boolean { //Good for fifth step
        val rightAnkle = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_ANKLE }
        val leftAnkle = poseLandmarks.find {it.landmarkType==PoseLandmark.LEFT_ANKLE}
        val rightKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_KNEE }
        val leftKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.LEFT_KNEE }
        if(rightKnee != null && rightAnkle != null && leftAnkle != null && leftKnee != null){
            if(abs(rightKnee.position.x - leftKnee.position.x) > kneeToKnee &&
                abs(rightAnkle.position.x - leftAnkle.position.x) > ankleToAnkle &&
                (abs(rightAnkle.position.y-leftKnee.position.y) >= 10).xor(abs(rightKnee.position.y-leftAnkle.position.y) >= 10)
            ){
                return true
            }
        }
        return false
    }

    private fun checkThirdStep(): Boolean { //Good for sixth step
        val rightAnkle = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_ANKLE }
        val leftAnkle = poseLandmarks.find {it.landmarkType==PoseLandmark.LEFT_ANKLE}
        val rightKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_KNEE }
        val leftKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.LEFT_KNEE }
        val leftHip = poseLandmarks.find { it.landmarkType==PoseLandmark.LEFT_HIP }
        val rightHip = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_HIP }

        if(rightKnee != null && rightAnkle != null && leftAnkle != null && leftKnee != null && leftHip != null && rightHip != null){
            val rightKneePoint  = Vec2(rightKnee.position.x,rightKnee.position.y)
            val rightHipPoint = Vec2(rightHip.position.x,rightHip.position.y)
            val rightAnklePoint = Vec2(rightAnkle.position.x,rightAnkle.position.y)
            val hipKneeDst = rightHipPoint.dstTo(rightKneePoint)
            val ankleKneeDst = rightAnklePoint.dstTo(rightKneePoint)
            val angle1 = tan((hipKneeDst/ankleKneeDst).toDouble())

            val leftKneePoint  = Vec2(leftKnee.position.x,leftKnee.position.y)
            val leftHipPoint = Vec2(leftHip.position.x,leftHip.position.y)
            val leftAnklePoint = Vec2(leftAnkle.position.x,leftAnkle.position.y)
            val hipKneeDst2 = leftHipPoint.dstTo(leftKneePoint)
            val ankleKneeDst2 = leftAnklePoint.dstTo(leftKneePoint)
            val angle2 = tan((hipKneeDst2/ankleKneeDst2).toDouble())

            if(( angle1 > 30 || angle2 > 30) && abs(rightAnkle.position.y-leftAnkle.position.y) > 10){
                return true
            }
        }
        return false
    }

    private fun checkFourthStep(): Boolean{
        val rightAnkle = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_ANKLE }
        val leftAnkle = poseLandmarks.find {it.landmarkType==PoseLandmark.LEFT_ANKLE}
        val rightKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_KNEE }
        val leftKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.LEFT_KNEE }

        if(rightKnee != null && rightAnkle != null && leftAnkle != null && leftKnee != null ){
            if(abs(rightKnee.position.y-leftKnee.position.y)>=10 && abs(rightAnkle.position.y-leftAnkle.position.y)>=10)
                return true
        }
        return false
    }


    data class Vec2(
        var x: Float,
        var y: Float
    ) {
        private infix fun dst2To(to: Vec2) = (x - to.x).pow(2) + (y - to.y).pow(2)
        infix fun dstTo(to: Vec2) = sqrt(this dst2To to)
    }

}

