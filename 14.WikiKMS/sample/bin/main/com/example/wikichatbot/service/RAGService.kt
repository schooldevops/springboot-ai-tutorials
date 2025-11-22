package com.example.wikichatbot.service

import com.example.wikichatbot.dto.ChatResponse
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

/**
 * RAG (Retrieval-Augmented Generation) 서비스
 */
@Service
class RAGService(
    private val chatModel: ChatModel,
    private val vectorStore: VectorStore
) {
    
    /**
     * RAG 기반 질문 답변
     */
    fun askQuestion(question: String, topK: Int = 3): ChatResponse {
        // 1. RETRIEVE: 관련 문서 검색
        val searchRequest = SearchRequest.query(question)
            .withTopK(topK)
        
        val relevantDocs = vectorStore.similaritySearch(searchRequest)
        
        if (relevantDocs.isEmpty()) {
            return ChatResponse(
                question = question,
                answer = "관련 문서를 찾을 수 없습니다. 문서를 먼저 인제스트해주세요.",
                sources = emptyList()
            )
        }
        
        // 2. 컨텍스트 구성
        val context = relevantDocs.mapIndexed { index, doc ->
            val source = doc.metadata["source"] ?: "알 수 없음"
            """
            [문서 ${index + 1}: $source]
            ${doc.content}
            """.trimIndent()
        }.joinToString("\n\n")
        
        // 3. GENERATE: 프롬프트 생성 및 답변 생성
        val promptText = createPromptText(question, context)
        val answer = chatModel.call(promptText)
        // 4. 출처 추출
        val sources = relevantDocs.mapNotNull { doc ->
            doc.metadata["source"] as? String
        }.distinct()
        
        return ChatResponse(
            question = question,
            answer = answer,
            sources = sources
        )
    }
    
    /**
     * RAG 프롬프트 생성
     */
    private fun createPromptText(question: String, context: String): String {
        return """
            당신은 사내 위키 전문가입니다.
            다음 참고 문서들을 바탕으로 질문에 정확하게 답변해주세요.
            
            [참고 문서]
            $context
            
            [질문]
            $question
            
            [답변 규칙]
            1. 참고 문서의 내용만을 기반으로 답변하세요
            2. 문서에 없는 내용은 "제공된 문서에서 해당 정보를 찾을 수 없습니다"라고 답변하세요
            3. 답변 시 어느 문서를 참고했는지 명시하세요
            4. 간결하고 명확하게 답변하세요
            5. 불확실한 경우 명시적으로 표현하세요
        """.trimIndent()
    }
    
    /**
     * 간단한 질문 답변 (컨텍스트 없이)
     */
    fun simpleAsk(question: String): String {
        return chatModel.call(question)
    }
}
