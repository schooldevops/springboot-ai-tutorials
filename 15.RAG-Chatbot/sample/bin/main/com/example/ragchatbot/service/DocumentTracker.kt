package com.example.ragchatbot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

/**
 * 문서 추적 및 중복 검사
 */
@Component
class DocumentTracker {
    
    private val logger = LoggerFactory.getLogger(javaClass)
    private val documentHashes = ConcurrentHashMap<String, String>()
    
    /**
     * 파일 해시 계산 (MD5)
     */
    fun calculateFileHash(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } > 0) {
                md.update(buffer, 0, read)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }
    
    /**
     * 문서 변경 여부 확인
     */
    fun isDocumentChanged(filePath: String, currentHash: String): Boolean {
        val previousHash = documentHashes[filePath]
        return previousHash == null || previousHash != currentHash
    }
    
    /**
     * 문서 해시 업데이트
     */
    fun updateDocumentHash(filePath: String, hash: String) {
        documentHashes[filePath] = hash
        logger.debug("문서 해시 업데이트: $filePath")
    }
    
    /**
     * 추적 중인 문서 수
     */
    fun getTrackedDocumentCount(): Int = documentHashes.size
    
    /**
     * 추적 중인 문서 목록
     */
    fun getTrackedDocuments(): Set<String> = documentHashes.keys
    
    /**
     * 모든 추적 정보 초기화
     */
    fun clear() {
        documentHashes.clear()
        logger.info("문서 추적 정보 초기화")
    }
}
