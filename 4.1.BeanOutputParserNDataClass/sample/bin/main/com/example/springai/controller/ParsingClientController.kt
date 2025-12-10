package com.example.springai.controller

import com.example.springai.model.*
import com.example.springai.service.ParsingClientService
import org.springframework.web.bind.annotation.*

/** ChatClient를 사용하는 파싱 컨트롤러 */
@RestController
@RequestMapping("/api/client/parse")
class ParsingClientController(private val parsingClientService: ParsingClientService) {

    /**
     * 사용자 정보 파싱 (ChatClient 사용) POST http://localhost:9000/api/client/parse/user Body: {"text":
     * "이름은 홍길동이고, 나이는 30세입니다. 이메일은 hong@example.com입니다."}
     */
    @PostMapping("/user")
    fun parseUserInfo(@RequestBody request: ParseRequest): UserInfo {
        return parsingClientService.parseResponse(
                UserInfo::class.java,
                "다음 텍스트에서 사용자 정보를 추출해주세요.",
                request.text
        )
    }

    /** 사용자 프로필 파싱 (선택적 필드 포함) POST http://localhost:9000/api/client/parse/profile */
    @PostMapping("/profile")
    fun parseUserProfile(@RequestBody request: ParseRequest): UserProfile {
        return parsingClientService.parseResponse(
                UserProfile::class.java,
                "다음 텍스트에서 사용자 프로필 정보를 추출해주세요. 없는 정보는 null로 설정하세요.",
                request.text
        )
    }

    /** 제품 정보 파싱 POST http://localhost:9000/api/client/parse/product */
    @PostMapping("/product")
    fun parseProduct(@RequestBody request: ParseRequest): Product {
        return parsingClientService.parseResponse(
                Product::class.java,
                "다음 텍스트에서 제품 정보를 추출해주세요.",
                request.text
        )
    }

    /** 주소 정보 파싱 POST http://localhost:9000/api/client/parse/address */
    @PostMapping("/address")
    fun parseAddress(@RequestBody request: ParseRequest): Address {
        return parsingClientService.parseResponse(
                Address::class.java,
                "다음 텍스트에서 주소 정보를 추출해주세요.",
                request.text
        )
    }

    /** 레시피 정보 파싱 POST http://localhost:9000/api/client/parse/recipe */
    @PostMapping("/recipe")
    fun parseRecipe(@RequestBody request: ParseRequest): Recipe {
        return parsingClientService.parseResponse(
                Recipe::class.java,
                "다음 텍스트에서 레시피 정보를 추출해주세요.",
                request.text
        )
    }

    /** 간단한 파싱 (기본 시스템 메시지 사용) POST http://localhost:9000/api/client/parse/simple/user */
    @PostMapping("/simple/user")
    fun simpleParseUser(@RequestBody request: ParseRequest): UserInfo {
        return parsingClientService.parse(UserInfo::class.java, request.text)
    }
}

data class ParseRequest(val text: String)
