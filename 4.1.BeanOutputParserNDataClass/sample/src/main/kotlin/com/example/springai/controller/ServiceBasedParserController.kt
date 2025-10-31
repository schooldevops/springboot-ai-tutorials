package com.example.springai.controller

import org.springframework.web.bind.annotation.*
import com.example.springai.service.ParsingService
import com.example.springai.model.UserInfo
import com.example.springai.model.Product
import com.example.springai.model.Recipe
import com.example.springai.model.ParseRequest

/**
 * ParsingService를 사용하는 컨트롤러
 */
@RestController
@RequestMapping("/api/service-parser")
class ServiceBasedParserController(
    private val parsingService: ParsingService
) {
    
    /**
     * 서비스를 통한 사용자 정보 파싱
     * POST http://localhost:8080/api/service-parser/user
     */
    @PostMapping("/user")
    fun parseUser(@RequestBody request: ParseRequest): UserInfo {
        return parsingService.parseResponse(
            clazz = UserInfo::class.java,
            systemMessage = "사용자 정보를 추출해주세요.",
            userMessage = request.question
        )
    }
    
    /**
     * 서비스를 통한 제품 정보 파싱
     * POST http://localhost:8080/api/service-parser/product
     */
    @PostMapping("/product")
    fun parseProduct(@RequestBody request: ParseRequest): Product {
        return parsingService.parseResponse(
            clazz = Product::class.java,
            systemMessage = "제품 정보를 추출하고 구조화해주세요.",
            userMessage = request.question
        )
    }
    
    /**
     * 서비스를 통한 레시피 파싱
     * POST http://localhost:8080/api/service-parser/recipe
     */
    @PostMapping("/recipe")
    fun parseRecipe(@RequestBody request: ParseRequest): Recipe {
        return parsingService.parseResponse(
            clazz = Recipe::class.java,
            systemMessage = "레시피 정보를 추출해주세요.",
            userMessage = request.question
        )
    }
}

