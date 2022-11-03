package onlab.mlkit.tiktok.drawing

import android.widget.Toast
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import onlab.mlkit.tiktok.CameraActivity

class PoseDrawing(private val activity: CameraActivity, private val skeletonOverlay: SkeletonOverlay) {
    fun drawSkeleton(pose: Pose)  {
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

            skeletonOverlay.clear()

            for (landmark in pose.allPoseLandmarks) {
                skeletonOverlay.drawCircle(landmark)
            }

            // Face
            skeletonOverlay.drawLine(nose, leftEyeInner)
            skeletonOverlay.drawLine(leftEyeInner, leftEye)
            skeletonOverlay.drawLine(leftEye, leftEyeOuter)
            skeletonOverlay.drawLine(leftEyeOuter, leftEar)
            skeletonOverlay.drawLine(nose, rightEyeInner)
            skeletonOverlay.drawLine(rightEyeInner, rightEye)
            skeletonOverlay.drawLine(rightEye, rightEyeOuter)
            skeletonOverlay.drawLine(rightEyeOuter, rightEar)
            skeletonOverlay.drawLine(leftMouth, rightMouth)
            skeletonOverlay.drawLine(leftShoulder, rightShoulder)
            skeletonOverlay.drawLine(leftHip, rightHip)

            // Left body
            skeletonOverlay.drawLine(leftShoulder, leftElbow)
            skeletonOverlay.drawLine(leftElbow, leftWrist)
            skeletonOverlay.drawLine(leftShoulder, leftHip)
            skeletonOverlay.drawLine(leftHip, leftKnee)
            skeletonOverlay.drawLine(leftKnee, leftAnkle)
            skeletonOverlay.drawLine(leftWrist, leftThumb)
            skeletonOverlay.drawLine(leftWrist, leftPinky)
            skeletonOverlay.drawLine(leftWrist, leftIndex)
            skeletonOverlay.drawLine(leftIndex, leftPinky)
            skeletonOverlay.drawLine(leftAnkle, leftHeel)
            skeletonOverlay.drawLine(leftHeel, leftFootIndex)

            // Right body
            skeletonOverlay.drawLine(rightShoulder, rightElbow)
            skeletonOverlay.drawLine(rightElbow, rightWrist)
            skeletonOverlay.drawLine(rightShoulder, rightHip)
            skeletonOverlay.drawLine(rightHip, rightKnee)
            skeletonOverlay.drawLine(rightKnee, rightAnkle)
            skeletonOverlay.drawLine(rightWrist, rightThumb)
            skeletonOverlay.drawLine(rightWrist, rightPinky)
            skeletonOverlay.drawLine(rightWrist, rightIndex)
            skeletonOverlay.drawLine(rightIndex, rightPinky)
            skeletonOverlay.drawLine(rightAnkle, rightHeel)
            skeletonOverlay.drawLine(rightHeel, rightFootIndex)

        } catch (e: java.lang.Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}