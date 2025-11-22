package com.example.ragchatbot.service

import org.springframework.ai.document.Document
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.stereotype.Service
import java.io.File

/**
 * 문서 로딩 및 분할 서비스
 */
@Service
class DocumentLoaderService {
    
    fun loadAndSplitMarkdownFile(filePath: String): List<Document> {
        val file = File(filePath)
        
        if (!file.exists()) {
            throw IllegalArgumentException("파일을 찾을 수 없습니다: $filePath")
        }
        
        val content = file.readText()
        
        val document = Document(
            content,
            mapOf(
                "source" to file.name,
                "fullPath" to filePath,
                "type" to "markdown",
                "lastModified" to file.lastModified()
            )
        )
        
        return splitDocument(document)
    }
    
    private fun splitDocument(document: Document): List<Document> {
        val textSplitter = TokenTextSplitter()
        return textSplitter.split(listOf(document))
    }
}
