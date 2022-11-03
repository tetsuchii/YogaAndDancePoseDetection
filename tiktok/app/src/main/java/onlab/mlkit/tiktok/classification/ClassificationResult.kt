package onlab.mlkit.tiktok.classification

import java.util.*

class ClassificationResult {

    private val classConfidences: MutableMap<String, Float>

    init {
        classConfidences = HashMap()
    }

    val allClasses: Set<String>
        get() = classConfidences.keys

    fun getClassConfidence(className: String): Float {
        return if (classConfidences.containsKey(className)) classConfidences[className]!! else 0f
    }

    val maxConfidenceClass: String
        get() = Collections.max<Map.Entry<String, Float>>(
            classConfidences.entries
        ) { (_, value): Map.Entry<String, Float>, (_, value1): Map.Entry<String, Float> -> (value - value1).toInt() }
            .key

    fun incrementClassConfidence(className: String) {
        classConfidences[className] =
            if (classConfidences.containsKey(className)) classConfidences[className]!! + 1 else 1f
    }

    fun putClassConfidence(className: String, confidence: Float) {
        classConfidences[className] = confidence
    }
}