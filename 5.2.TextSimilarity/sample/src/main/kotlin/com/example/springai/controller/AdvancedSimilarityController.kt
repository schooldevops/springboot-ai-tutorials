package com.example.springai.controller

import com.example.springai.model.DuplicateDetectionRequest
import com.example.springai.model.ClusteringRequest
import com.example.springai.service.DuplicateDetectionService
import com.example.springai.service.TextClusteringService
import org.springframework.web.bind.annotation.*

/**
 * 고급 유사도 기능 컨트롤러
 */
@RestController
@RequestMapping("/api/similarity/advanced")
class AdvancedSimilarityController(
    private val duplicateDetectionService: DuplicateDetectionService,
    private val textClusteringService: TextClusteringService
) {
    
    /**
     * 중복 문서 검사
     * POST http://localhost:8080/api/similarity/advanced/duplicates
     * Body: {"texts": ["텍스트1", "텍스트2", ...], "threshold": 0.95}
     */
    @PostMapping("/duplicates")
    fun detectDuplicates(@RequestBody request: DuplicateDetectionRequest): Map<String, Any> {
        val duplicates = duplicateDetectionService.detectDuplicates(
            texts = request.texts,
            threshold = request.threshold
        )
        
        return mapOf(
            "totalTexts" to request.texts.size,
            "threshold" to request.threshold,
            "duplicateCount" to duplicates.size,
            "duplicates" to duplicates
        )
    }
    
    /**
     * 텍스트 클러스터링
     * POST http://localhost:8080/api/similarity/advanced/cluster
     * Body: {"texts": ["텍스트1", "텍스트2", ...], "similarityThreshold": 0.7}
     */
    @PostMapping("/cluster")
    fun clusterTexts(@RequestBody request: ClusteringRequest): Map<String, Any> {
        val clusters = textClusteringService.clusterTexts(
            texts = request.texts,
            similarityThreshold = request.similarityThreshold
        )
        
        return mapOf(
            "totalTexts" to request.texts.size,
            "similarityThreshold" to request.similarityThreshold,
            "clusterCount" to clusters.size,
            "clusters" to clusters
        )
    }
}

