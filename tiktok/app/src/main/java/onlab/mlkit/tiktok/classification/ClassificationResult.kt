package onlab.mlkit.tiktok.classification

import java.util.*

class ClassificationResult {

    private val classConfidences: MutableMap<String, Float>
    val maxConfidenceClass: String
        get() = Collections.max<Map.Entry<String, Float>>(
            classConfidences.entries
        ) { (_, value): Map.Entry<String, Float>, (_, value1): Map.Entry<String, Float> -> (value - value1).toInt() }
            .key
    init {
        classConfidences = HashMap()
    }

    fun getClassConfidence(className: String): Float {
        return if (classConfidences.containsKey(className)) classConfidences[className]!! else 0f
    }

    fun incrementClassConfidence(className: String) {
        classConfidences[className] =
            if (classConfidences.containsKey(className)) classConfidences[className]!! + 1 else 1f
    }
}