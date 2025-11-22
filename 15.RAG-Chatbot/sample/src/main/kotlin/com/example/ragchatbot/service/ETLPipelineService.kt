package com.example.ragchatbot.service

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

/**
 * ETL 파이프라인 서비스
 * 애플리케이션 시작 시 자동으로 문서를 로드하고 VectorStore에 저장
 */
@Service
class ETLPipelineService(
    private val documentLoader: DocumentLoaderService,
    private val vectorStore: VectorStore,
    private val documentTracker: DocumentTracker,
    
    @Value("\${etl.documents.directory:wiki-documents}")
    private val documentsDirectory: String,
    
    @Value("\${etl.documents.auto-load:true}")
    private val autoLoad: Boolean
) {
    
    private val logger = LoggerFactory.getLogger(javaClass)
    
    @PostConstruct
    fun initializePipeline() {
        if (!autoLoad) {
            logger.info("자동 문서 로드가 비활성화되어 있습니다")
            return
        }
        
        logger.info("=== ETL 파이프라인 시작 ===")
        loadAndProcessDocuments()
        logger.info("=== ETL 파이프라인 완료 ===")
    }
    
    /**
     * 문서 로드 및 처리
     */
    fun loadAndProcessDocuments(): Map<String, Int> {
        val directory = File(documentsDirectory)
        
        if (!directory.exists()) {
            logger.warn("문서 디렉토리가 없습니다: ${directory.absolutePath}")
            return mapOf(
                "new" to 0,
                "updated" to 0,
                "skipped" to 0,
                "total" to 0
            )
        }
        
        // 1. EXTRACT: 파일 수집
        val files = directory.listFiles { file ->
            file.extension.lowercase() == "md"
        } ?: emptyArray()
        
        logger.info("발견된 문서: ${files.size}개")
        
        var newCount = 0
        var updatedCount = 0
        var skippedCount = 0
        
        files.forEach { file ->
            try {
                // 2. TRANSFORM: 해시 계산 및 중복 검사
                val currentHash = documentTracker.calculateFileHash(file)
                val filePath = file.absolutePath
                val wasTracked = documentTracker.getTrackedDocuments().contains(filePath)
                
                if (documentTracker.isDocumentChanged(filePath, currentHash)) {
                    // 3. LOAD: 문서 로드 및 저장
                    val documents = documentLoader.loadAndSplitMarkdownFile(filePath)
                    vectorStore.add(documents)
                    
                    documentTracker.updateDocumentHash(filePath, currentHash)
                    
                    if (wasTracked) {
                        updatedCount++
                        logger.info("✓ 업데이트: ${file.name} (${documents.size} 청크)")
                    } else {
                        newCount++
                        logger.info("✓ 신규: ${file.name} (${documents.size} 청크)")
                    }
                } else {
                    skippedCount++
                    logger.debug("○ 건너뜀 (변경 없음): ${file.name}")
                }
            } catch (e: Exception) {
                logger.error("문서 처리 실패: ${file.name}", e)
            }
        }
        
        logger.info("ETL 완료 - 신규: $newCount, 업데이트: $updatedCount, 건너뜀: $skippedCount, 총: ${files.size}")
        
        return mapOf(
            "new" to newCount,
            "updated" to updatedCount,
            "skipped" to skippedCount,
            "total" to files.size
        )
    }
}
