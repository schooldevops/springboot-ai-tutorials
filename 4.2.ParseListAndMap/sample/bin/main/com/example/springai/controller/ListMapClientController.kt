package com.example.springai.controller

import com.example.springai.model.ParseRequest
import com.example.springai.service.ListMapClientService
import org.springframework.web.bind.annotation.*

/** ChatClient를 사용하는 리스트/맵 파싱 컨트롤러 */
@RestController
@RequestMapping("/api/client")
class ListMapClientController(private val listMapClientService: ListMapClientService) {

    /**
     * 기본 리스트 파싱 (ChatClient 사용) POST http://localhost:9000/api/client/list/parse Body: {"question":
     * "5가지 프로그래밍 언어를 나열해주세요"}
     */
    @PostMapping("/list/parse")
    fun parseList(@RequestBody request: ParseRequest): Map<String, Any> {
        val items = listMapClientService.parseList(request.question)

        return mapOf("items" to items, "count" to items.size)
    }

    /**
     * CSV 형식 리스트 파싱 (ChatClient 사용) POST http://localhost:9000/api/client/list/csv Body:
     * {"question": "인기있는 프로그래밍 언어 5개를 쉼표로 구분해서 알려주세요"}
     */
    @PostMapping("/list/csv")
    fun parseCsvList(@RequestBody request: ParseRequest): Map<String, Any> {
        val items = listMapClientService.parseCsvList(request.question)

        return mapOf("items" to items, "count" to items.size)
    }

    /** 안전한 리스트 파싱 (에러 처리 포함) POST http://localhost:9000/api/client/list/safe */
    @PostMapping("/list/safe")
    fun safeParseList(@RequestBody request: ParseRequest): Map<String, Any> {
        val result = listMapClientService.safeParseList(request.question)

        return if (result.isSuccess) {
            val items = result.getOrNull() ?: emptyList()
            mapOf("success" to true, "items" to items, "count" to items.size)
        } else {
            mapOf("success" to false, "error" to (result.exceptionOrNull()?.message ?: "알 수 없는 오류"))
        }
    }

    /**
     * 맵 파싱 (ChatClient 사용) POST http://localhost:9000/api/client/map/parse Body: {"question":
     * "프로그래밍 언어별 특징을 Key-Value 형식으로 제공해주세요"}
     */
    @PostMapping("/map/parse")
    fun parseMap(@RequestBody request: ParseRequest): Map<String, Any> {
        val resultMap = listMapClientService.parseMap(request.question)

        return mapOf("data" to resultMap, "count" to resultMap.size)
    }

    /** 안전한 맵 파싱 (에러 처리 포함) POST http://localhost:9000/api/client/map/safe */
    @PostMapping("/map/safe")
    fun safeParseMap(@RequestBody request: ParseRequest): Map<String, Any> {
        val result = listMapClientService.safeParseMap(request.question)

        return if (result.isSuccess) {
            val data = result.getOrNull() ?: emptyMap()
            mapOf("success" to true, "data" to data, "count" to data.size)
        } else {
            mapOf("success" to false, "error" to (result.exceptionOrNull()?.message ?: "알 수 없는 오류"))
        }
    }
}
