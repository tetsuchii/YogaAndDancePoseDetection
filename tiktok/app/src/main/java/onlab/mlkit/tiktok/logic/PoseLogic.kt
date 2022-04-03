package onlab.mlkit.tiktok.logic

import com.google.mlkit.vision.pose.PoseLandmark
import onlab.mlkit.tiktok.MainActivity

class PoseLogic {
    var poseLandmarks : List<PoseLandmark> = listOf()
    var baseinitialized = false
    var kneeToKnee = 0.0f
    var ankleToAnkle = 0.0f

    fun updatePoseLandmarks(newList : List<PoseLandmark>){
        poseLandmarks=newList



        val rightAnkle = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_ANKLE }
        val leftAnkle = poseLandmarks.find {it.landmarkType==PoseLandmark.LEFT_ANKLE}
        val rightKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_KNEE }
        val leftKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.LEFT_KNEE }

        if(baseinitialized==null &&  rightKnee != null && rightAnkle != null && leftAnkle != null && leftKnee != null){
            baseinitialized=true
            kneeToKnee=rightKnee.position.x - leftKnee.position.x
            ankleToAnkle=rightAnkle.position.x - leftAnkle.position.x
            println("helo")
            println(kneeToKnee)
        }
        else {
            if(checkFirstStep()){
                println("First step ok!")

            }
        }
    }

    private fun checkFirstStep(): Boolean {
        val rightAnkle = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_ANKLE }
        val leftAnkle = poseLandmarks.find {it.landmarkType==PoseLandmark.LEFT_ANKLE}
        val rightKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.RIGHT_KNEE }
        val leftKnee = poseLandmarks.find { it.landmarkType==PoseLandmark.LEFT_KNEE }

        if( rightKnee != null && rightAnkle != null && leftAnkle != null && leftKnee != null)
        if(Math.abs(rightAnkle.position.y-leftKnee.position.y)<10){
            println("Nagyjabol egy vonalban vannak")
            return true
        }
        return false
    }

}
