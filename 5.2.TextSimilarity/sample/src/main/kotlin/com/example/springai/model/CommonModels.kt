package com.example.springai.model

/**
 * 공통으로 사용되는 데이터 클래스들
 */
data class SimilarityRequest(
    val text1: String,
    val text2: String
)

data class MultipleSimilarityRequest(
    val query: String,
    val texts: List<String>
)

data class PairwiseSimilarityRequest(
    val texts: List<String>
)

data class ThresholdSimilarityRequest(
    val query: String,
    val texts: List<String>,
    val threshold: Double = 0.7
)

data class TopKSimilarityRequest(
    val query: String,
    val texts: List<String>,
    val topK: Int = 5
)

data class DuplicateDetectionRequest(
    val texts: List<String>,
    val threshold: Double = 0.95
)

data class ClusteringRequest(
    val texts: List<String>,
    val similarityThreshold: Double = 0.7
)

