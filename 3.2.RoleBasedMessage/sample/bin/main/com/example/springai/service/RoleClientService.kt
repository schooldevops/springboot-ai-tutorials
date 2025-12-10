package com.example.springai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.stereotype.Service

/** ChatClient를 사용한 역할 기반 메시지 서비스 ChatModel 대신 더 현대적인 ChatClient API를 사용 */
@Service
class RoleClientService(private val chatClientBuilder: ChatClient.Builder) {

        private val chatClient = chatClientBuilder.build()

        /** 역할에 따른 SystemMessage 생성 */
        fun createSystemMessage(role: String): SystemMessage {
                return when (role.lowercase()) {
                        "teacher" ->
                                SystemMessage(
                                        """
                당신은 친절한 선생님입니다.
                
                다음 원칙을 따라주세요:
                1. 복잡한 개념을 쉽게 설명하기
                2. 예시를 통해 이해를 돕기
                3. 학생의 수준에 맞춰 설명하기
                
                응답 형식:
                - 간단한 요약
                - 자세한 설명
                - 실용적인 예시
                """.trimIndent()
                                )
                        "doctor" ->
                                SystemMessage(
                                        """
                당신은 전문 의사입니다.
                
                다음 원칙을 따라주세요:
                1. 의학적 지식을 바탕으로 정확하게 답변
                2. 전문 용어는 쉽게 설명
                3. 진단은 하지 않고 일반적인 정보만 제공
                
                응답 형식:
                - 일반적인 설명
                - 주의사항
                - 의료진 상담 권장
                """.trimIndent()
                                )
                        "chef" ->
                                SystemMessage(
                                        """
                당신은 유명한 셰프입니다.
                
                다음 원칙을 따라주세요:
                1. 요리에 대한 열정을 보여주기
                2. 실용적인 팁 제공
                3. 단계별 설명을 명확하게
                
                응답 형식:
                - 요리 소개
                - 재료 및 분량
                - 조리 방법
                """.trimIndent()
                                )
                        "developer" ->
                                SystemMessage(
                                        """
                당신은 숙련된 소프트웨어 개발자입니다.
                
                다음 원칙을 따라주세요:
                1. 명확하고 실행 가능한 코드 예제 제공
                2. 코드에는 주석 추가
                3. 베스트 프랙티스 설명
                
                응답 형식:
                - 간단한 설명
                - 코드 예제
                - 추가 팁
                """.trimIndent()
                                )
                        else -> SystemMessage("당신은 도움이 되는 어시스턴트입니다.")
                }
        }

        /** 커스텀 역할 메시지 생성 */
        fun createCustomSystemMessage(
                role: String,
                instructions: String,
                principles: List<String>
        ): SystemMessage {
                return SystemMessage(
                        """
            당신은 $role 역할을 맡고 있습니다.
            $instructions
            
            다음 원칙을 따라주세요:
            ${principles.joinToString("\n") { "- $it" }}
            """.trimIndent()
                )
        }

        /** 역할 기반 채팅 (ChatClient 사용) */
        fun chat(role: String, message: String): String {
                val systemMessage = createSystemMessage(role)

                return chatClient.prompt().system(systemMessage.text).user(message).call().content()
                        ?: "응답 없음"
        }

        /** 커스텀 역할 채팅 (ChatClient 사용) */
        fun customChat(
                role: String,
                instructions: String,
                principles: List<String>,
                message: String
        ): String {
                val systemMessage = createCustomSystemMessage(role, instructions, principles)

                return chatClient.prompt().system(systemMessage.text).user(message).call().content()
                        ?: "응답 없음"
        }
}
