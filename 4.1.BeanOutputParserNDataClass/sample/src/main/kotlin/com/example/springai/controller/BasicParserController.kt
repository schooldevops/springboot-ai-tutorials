package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.web.bind.annotation.*
import com.example.springai.model.UserInfo
import com.example.springai.model.UserProfile
import com.example.springai.model.ParseRequest
import com.example.springai.util.BeanOutputParser

/**
 * BeanOutputParser의 기본 사용법을 보여주는 컨트롤러
 */
@RestController
@RequestMapping("/api/basic-parser")
class BasicParserController(
    private val chatModel: ChatModel
) {
    
    /**
     * 기본 BeanOutputParser 사용 예제
     * POST http://localhost:8080/api/basic-parser/user-info
     * Body: {"question": "사용자 정보를 제공해주세요. 이름은 홍길동, 나이는 30, 이메일은 hong@example.com입니다."}
     */
    @PostMapping("/user-info")
    fun parseUserInfo(@RequestBody request: ParseRequest): UserInfo {
        // 1. Parser 생성
        val parser = BeanOutputParser(UserInfo::class.java)
        
        // 2. Format 가져오기
        val format = parser.format
        
        // 3. Prompt 생성 (Format 포함)
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 JSON 형식으로 응답해주세요:
                    $format
                    
                    응답은 반드시 JSON 형식이어야 합니다.
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        // 4. LLM 호출
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        // 5. 파싱
        return parser.parse(text)
    }
    
    /**
     * Nullable 필드를 포함한 예제
     * POST http://localhost:8080/api/basic-parser/profile
     * Body: {"question": "홍길동에 대한 프로필 정보를 제공해주세요."}
     */
    @PostMapping("/profile")
    fun parseProfile(@RequestBody request: ParseRequest): UserProfile {
        val parser = BeanOutputParser(UserProfile::class.java)
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 JSON 형식으로 응답해주세요:
                    $format
                    
                    없는 정보는 null로 설정해주세요.
                    """.trimIndent()
                ),
                UserMessage(request.question)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        return parser.parse(text)
    }
}

