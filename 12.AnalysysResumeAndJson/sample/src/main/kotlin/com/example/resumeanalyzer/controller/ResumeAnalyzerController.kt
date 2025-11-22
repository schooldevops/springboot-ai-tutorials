package com.example.resumeanalyzer.controller

import com.example.resumeanalyzer.dto.ResumeRequest
import com.example.resumeanalyzer.model.*
import com.example.resumeanalyzer.util.BeanOutputParser
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/resume")
class ResumeAnalyzerController(
    private val chatModel: ChatModel
) {

    /**
     * 기본 이력서 정보 추출
     * POST /api/resume/basic
     */
    @PostMapping("/basic")
    fun analyzeBasicInfo(@RequestBody request: ResumeRequest): BasicResumeInfo {
        val parser = BeanOutputParser(BasicResumeInfo::class.java)
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    당신은 이력서 분석 전문가입니다.
                    주어진 이력서에서 기본 정보만 추출하여 다음 JSON 형식으로 반환해주세요:
                    
                    $format
                    
                    규칙:
                    - 정보가 없으면 null로 설정
                    - yearsOfExperience는 총 경력 년수 (정수)
                    - currentPosition은 현재 또는 가장 최근 직책
                    - 반드시 유효한 JSON만 반환
                    """.trimIndent()
                ),
                UserMessage(request.resumeText)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.result.output.content
        
        return parser.parse(text)
    }

    /**
     * 전체 이력서 분석 (경력, 학력, 스킬 포함)
     * POST /api/resume/analyze
     */
    @PostMapping("/analyze")
    fun analyzeResume(@RequestBody request: ResumeRequest): ResumeInfo {
        val parser = BeanOutputParser(ResumeInfo::class.java)
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    당신은 이력서 분석 전문가입니다.
                    주어진 이력서 텍스트를 분석하여 다음 JSON 형식으로 정보를 추출해주세요:
                    
                    $format
                    
                    규칙:
                    - 정보가 없는 필드는 null로 설정
                    - 날짜는 "YYYY-MM" 형식 사용 (예: "2020-01")
                    - 현재 재직중이면 endDate는 null
                    - graduationYear는 졸업년도 (정수)
                    - skills의 category는 Backend, Frontend, Database, DevOps, Mobile 등으로 분류
                    - proficiency는 Beginner, Intermediate, Advanced, Expert 중 하나
                    - 반드시 유효한 JSON 형식으로 응답
                    - 배열이 비어있으면 빈 배열 [] 반환
                    """.trimIndent()
                ),
                UserMessage(request.resumeText)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.result.output.content
        
        return parser.parse(text)
    }

    /**
     * 스킬만 추출
     * POST /api/resume/extract-skills
     */
    @PostMapping("/extract-skills")
    fun extractSkills(@RequestBody request: ResumeRequest): List<Skill> {
        val parser = BeanOutputParser(Array<Skill>::class.java)
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    이력서에서 기술 스택과 스킬을 추출하여 JSON 배열로 반환해주세요.
                    각 스킬은 다음 형식을 따릅니다:
                    {
                      "name": "스킬명",
                      "category": "카테고리 (Backend, Frontend, Database, DevOps, Mobile, Cloud 등)",
                      "proficiency": "숙련도 (Beginner/Intermediate/Advanced/Expert)"
                    }
                    
                    규칙:
                    - 이력서에 명시된 기술만 추출
                    - 경력 기간과 사용 빈도를 고려하여 숙련도 추정
                    - 유사한 기술은 통합 (예: Java, Java 8 → Java)
                    - 반드시 JSON 배열 형식으로 반환
                    """.trimIndent()
                ),
                UserMessage(request.resumeText)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.result.output.content
        
        return parser.parse(text).toList()
    }

    /**
     * 경력 사항만 추출
     * POST /api/resume/extract-experience
     */
    @PostMapping("/extract-experience")
    fun extractExperience(@RequestBody request: ResumeRequest): List<WorkExperience> {
        val parser = BeanOutputParser(Array<WorkExperience>::class.java)
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    이력서에서 경력 사항을 추출하여 JSON 배열로 반환해주세요.
                    각 경력은 다음 형식을 따릅니다:
                    {
                      "company": "회사명",
                      "position": "직책",
                      "startDate": "시작일 (YYYY-MM)",
                      "endDate": "종료일 (YYYY-MM) 또는 null (현재 재직중)",
                      "description": "업무 내용"
                    }
                    
                    규칙:
                    - 최신 경력부터 순서대로 정렬
                    - 날짜는 YYYY-MM 형식
                    - 현재 재직중이면 endDate는 null
                    - 반드시 JSON 배열 형식으로 반환
                    """.trimIndent()
                ),
                UserMessage(request.resumeText)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.result.output.content
        
        return parser.parse(text).toList()
    }

    /**
     * 헬스 체크
     */
    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "Resume Analyzer API",
            "timestamp" to System.currentTimeMillis().toString()
        )
    }
}
