package com.example.wikichatbot.service

import org.springframework.ai.document.Document
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

/**
 * 문서 로딩 및 분할 서비스
 */
@Service
class DocumentService(
    @Value("\${wiki.documents.chunk-size:500}")
    private val chunkSize: Int,
    
    @Value("\${wiki.documents.chunk-overlap:50}")
    private val chunkOverlap: Int
) {
    
    /**
     * Markdown 파일 로드 및 분할
     */
    fun loadAndSplitMarkdownFile(filePath: String): List<Document> {
        val file = File(filePath)
        
        if (!file.exists()) {
            throw IllegalArgumentException("파일을 찾을 수 없습니다: $filePath")
        }
        
        // 파일 읽기
        val content = file.readText()
        
        // Document 생성
        val document = Document(
            content,
            mapOf(
                "source" to file.name,
                "fullPath" to filePath,
                "type" to "markdown",
                "lastModified" to file.lastModified()
            )
        )
        
        // 텍스트 분할
        return splitDocument(document)
    }
    
    /**
     * 디렉토리의 모든 Markdown 파일 로드
     */
    fun loadMarkdownDirectory(directoryPath: String): List<Document> {
        val directory = File(directoryPath)
        
        if (!directory.exists() || !directory.isDirectory) {
            throw IllegalArgumentException("디렉토리를 찾을 수 없습니다: $directoryPath")
        }
        
        val markdownFiles = directory.listFiles { file ->
            file.extension.lowercase() == "md"
        } ?: emptyArray()
        
        return markdownFiles.flatMap { file ->
            loadAndSplitMarkdownFile(file.absolutePath)
        }
    }
    
    /**
     * 문서 분할
     */
    private fun splitDocument(document: Document): List<Document> {
        val textSplitter = TokenTextSplitter()
        return textSplitter.split(listOf(document))
    }
    
    /**
     * 텍스트 직접 분할
     */
    fun splitText(text: String, metadata: Map<String, Any> = emptyMap()): List<Document> {
        val document = Document(text, metadata)
        return splitDocument(document)
    }
}
