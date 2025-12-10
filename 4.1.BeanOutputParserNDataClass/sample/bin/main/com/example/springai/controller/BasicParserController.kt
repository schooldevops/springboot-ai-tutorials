package com.example.springai.controller

import com.example.springai.model.ParseRequest
import com.example.springai.model.UserInfo
import com.example.springai.model.UserProfile
import com.example.springai.util.BeanOutputParser
import org.springframework.ai.chat.messages.*
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.web.bind.annotation.*

/** BeanOutputParser의 기본 사용법을 보여주는 컨트롤러 */
@RestController
@RequestMapping("/api/basic-parser")
class BasicParserController(private val chatModel: ChatModel) {

    /**
     * 기본 BeanOutputParser 사용 예제 POST http://localhost:9000/api/basic-parser/user-info Body:
     * {"question": "사용자 정보를 제공해주세요. 이름은 홍길동, 나이는 30, 이메일은 hong@example.com입니다."}
     */
    @PostMapping("/user-info")
    fun parseUserInfo(@RequestBody request: ParseRequest): UserInfo {
        // 1. Parser 생성
        val parser = BeanOutputParser(UserInfo::class.java)

        // 2. Format 가져오기
        val format = parser.format

        // 3. Prompt 생성 (Format 포함)
        val prompt =
                Prompt(
                        listOf(
                                SystemMessage(
                                        """
                    당신은 JSON 데이터 생성 전문가입니다.
                    사용자의 질문에서 정보를 추출하거나 생성하여 다음 JSON 형식으로 응답해주세요:
                    $format
                    
                    중요한 규칙:
                    1. 응답은 반드시 유효한 JSON 형식이어야 합니다.
                    2. JSON 외에 다른 텍스트나 설명을 포함하지 마세요.
                    3. age는 반드시 숫자(정수)여야 합니다.
                    4. 정보가 명시되지 않은 경우 합리적인 기본값을 사용하세요.
                    5. 조언이나 제안이 아닌 실제 데이터를 반환하세요.
                    """.trimIndent()
                                ),
                                UserMessage(request.question)
                        )
                )

        // 4. LLM 호출
        val response = chatModel.call(prompt)
        val text =
                response.results.firstOrNull()?.output?.text ?: throw IllegalStateException("응답 없음")

        // 5. 파싱 (에러 처리 포함)
        return try {
            parser.parse(text)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                    "JSON 파싱 실패: ${e.message}\n\n" + "LLM 응답 내용:\n$text\n\n" + "예상 형식:\n$format",
                    e
            )
        }
    }

    /**
     * Nullable 필드를 포함한 예제 POST http://localhost:9000/api/basic-parser/profile Body: {"question":
     * "홍길동에 대한 프로필 정보를 제공해주세요."}
     */
    @PostMapping("/profile")
    fun parseProfile(@RequestBody request: ParseRequest): UserProfile {
        val parser = BeanOutputParser(UserProfile::class.java)
        val format = parser.format

        val prompt =
                Prompt(
                        listOf(
                                SystemMessage(
                                        """
                    당신은 JSON 데이터 생성 전문가입니다.
                    사용자의 질문에서 정보를 추출하거나 생성하여 다음 JSON 형식으로 응답해주세요:
                    $format
                    
                    중요한 규칙:
                    1. 응답은 반드시 유효한 JSON 형식이어야 합니다.
                    2. JSON 외에 다른 텍스트나 설명을 포함하지 마세요.
                    3. age는 숫자(정수) 또는 null이어야 합니다.
                    4. 정보가 없는 필드는 null로 설정하세요.
                    5. 조언이나 제안이 아닌 실제 데이터를 반환하세요.
                    """.trimIndent()
                                ),
                                UserMessage(request.question)
                        )
                )

        val response = chatModel.call(prompt)
        val text =
                response.results.firstOrNull()?.output?.text ?: throw IllegalStateException("응답 없음")

        return try {
            parser.parse(text)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                    "JSON 파싱 실패: ${e.message}\n\n" + "LLM 응답 내용:\n$text\n\n" + "예상 형식:\n$format",
                    e
            )
        }
    }
}
