package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.web.bind.annotation.*
import com.example.springai.model.Product
import com.example.springai.model.CompanyInfo
import com.example.springai.model.ParseRequest
import com.example.springai.util.BeanOutputParser

/**
 * BeanOutputParser의 고급 활용 예제
 */
@RestController
@RequestMapping("/api/advanced-parser")
class AdvancedParserController(
    private val chatModel: ChatModel
) {
    
    /**
     * 리스트를 포함한 Data Class 파싱
     * POST http://localhost:8080/api/advanced-parser/product
     * Body: {"description": "스마트폰 제품입니다. 가격은 100만원입니다. 카메라, 배터리, 디스플레이 기능이 있습니다."}
     */
    @PostMapping("/product")
    fun parseProduct(@RequestBody request: ProductRequest): Product {
        val parser = BeanOutputParser(Product::class.java)
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    제품 설명을 분석하여 다음 형식으로 구조화해주세요:
                    $format
                    
                    features 리스트에는 최소 3개 이상의 기능을 포함해주세요.
                    """.trimIndent()
                ),
                UserMessage(request.description)
            )
        )
        
        val response = chatModel.call(prompt)
        val text = response.results.firstOrNull()?.output?.text 
            ?: throw IllegalStateException("응답 없음")
        
        return parser.parse(text)
    }
    
    /**
     * 중첩 구조 파싱
     * POST http://localhost:8080/api/advanced-parser/company
     * Body: {"question": "Spring Corp 회사 정보를 제공해주세요. 주소는 서울시 강남구, 직원 수는 100명입니다."}
     */
    @PostMapping("/company")
    fun parseCompany(@RequestBody request: ParseRequest): CompanyInfo {
        val parser = BeanOutputParser(CompanyInfo::class.java)
        val format = parser.format
        
        val prompt = Prompt(
            listOf(
                SystemMessage(
                    """
                    다음 형식으로 응답해주세요:
                    $format
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

data class ProductRequest(
    val description: String
)

