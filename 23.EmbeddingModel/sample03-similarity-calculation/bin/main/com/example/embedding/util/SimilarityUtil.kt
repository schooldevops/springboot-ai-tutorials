package com.example.embedding.util

import kotlin.math.sqrt

/** 벡터 유사도 계산 유틸리티 */
object SimilarityUtil {

    /** 코사인 유사도 계산 값 범위: -1.0 (완전 반대) ~ 1.0 (완전 동일) */
    fun cosineSimilarity(vec1: FloatArray, vec2: FloatArray): Double {
        require(vec1.size == vec2.size) { "Vectors must have same dimensions" }

        val dotProduct = vec1.zip(vec2).sumOf { (a, b) -> (a * b).toDouble() }
        val magnitude1 = sqrt(vec1.sumOf { (it * it).toDouble() })
        val magnitude2 = sqrt(vec2.sumOf { (it * it).toDouble() })

        return dotProduct / (magnitude1 * magnitude2)
    }

    /** 유클리드 거리 계산 */
    fun euclideanDistance(vec1: FloatArray, vec2: FloatArray): Double {
        require(vec1.size == vec2.size) { "Vectors must have same dimensions" }
        return sqrt(
                vec1.zip(vec2).sumOf { (a, b) ->
                    val diff = a - b
                    (diff * diff).toDouble()
                }
        )
    }
}
