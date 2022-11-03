package onlab.mlkit.tiktok.classification

import android.os.SystemClock
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

class EMASmoothing constructor(
    private val windowSize: Int = DEFAULT_WINDOW_SIZE,
    private val alpha: Float = DEFAULT_ALPHA
) {
    private val window: Deque<ClassificationResult>
    private var lastInputMs: Long = 0

    init {
        window = LinkedBlockingDeque(windowSize)
    }

    fun getSmoothedResult(classificationResult: ClassificationResult): ClassificationResult {
        val nowMs = SystemClock.elapsedRealtime()
        if (nowMs - lastInputMs > RESET_THRESHOLD_MS) {
            window.clear()
        }
        lastInputMs = nowMs

        if (window.size == windowSize) {
            window.pollLast()
        }
        window.addFirst(classificationResult)
        val allClasses: MutableSet<String> = HashSet()
        for (result in window) {
            allClasses.addAll(result.allClasses)
        }
        val smoothedResult = ClassificationResult()
        for (className in allClasses) {
            var factor = 1f
            var topSum = 0f
            var bottomSum = 0f
            for (result in window) {
                val value = result.getClassConfidence(className)
                topSum += factor * value
                bottomSum += factor
                factor = (factor * (1.0 - alpha)).toFloat()
            }
            smoothedResult.putClassConfidence(className, topSum / bottomSum)
        }
        return smoothedResult
    }

    companion object {
        private const val DEFAULT_WINDOW_SIZE = 10
        private const val DEFAULT_ALPHA = 0.2f
        private const val RESET_THRESHOLD_MS: Long = 100
    }
}