package com.example.openapi.controller

import com.example.openapi.parser.OpenAPISpecParser
import com.example.openapi.service.SpecDocumentService
import com.example.openapi.service.SpecSearchService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/** REST API Controller for OpenAPI Spec operations */
@RestController
@RequestMapping("/api/spec")
class SpecController(
        private val parser: OpenAPISpecParser,
        private val documentService: SpecDocumentService,
        private val searchService: SpecSearchService
) {

    /** Upload and parse OpenAPI specification */
    @PostMapping("/upload")
    fun uploadSpec(@RequestParam("file") file: MultipartFile): Map<String, Any> {
        val content = file.inputStream.readBytes().decodeToString()
        val parsedSpec = parser.parseSpec(content)

        documentService.storeSpec(parsedSpec, content)

        return mapOf(
                "message" to "Spec uploaded successfully",
                "title" to parsedSpec.info.title,
                "version" to parsedSpec.info.version,
                "pathsCount" to parsedSpec.paths.size,
                "componentsCount" to parsedSpec.components.size
        )
    }

    /** Upload spec from raw YAML/JSON content */
    @PostMapping("/upload-content")
    fun uploadSpecContent(@RequestBody content: String): Map<String, Any> {
        val parsedSpec = parser.parseSpec(content)
        documentService.storeSpec(parsedSpec, content)

        return mapOf(
                "message" to "Spec uploaded successfully",
                "title" to parsedSpec.info.title,
                "version" to parsedSpec.info.version,
                "pathsCount" to parsedSpec.paths.size,
                "componentsCount" to parsedSpec.components.size
        )
    }

    /** Search for API specifications using natural language */
    @PostMapping("/search", produces = ["application/json", "text/plain"])
    fun search(@RequestBody request: SpecSearchRequest): ResponseEntity<*> {
        val responseFormat = request.responseFormat ?: "meta"

        return if (responseFormat == "yaml") {
            // Return YAML as text/plain with proper newlines and indentation
            val yamlResults =
                    searchService.searchAsYaml(
                            query = request.query,
                            topK = request.topK ?: 5,
                            includeRelatedSchemas = request.includeRelatedSchemas ?: true
                    )

            // Join multiple results with separator
            val yamlContent = yamlResults.joinToString("\n---\n")

            ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(yamlContent)
        } else {
            // Return JSON with SearchResult objects
            val results =
                    searchService.search(
                            query = request.query,
                            topK = request.topK ?: 5,
                            includeRelatedSchemas = request.includeRelatedSchemas ?: true,
                            responseFormat = "meta"
                    )
            ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(results)
        }
    }

    /** Get statistics about stored specifications */
    @GetMapping("/stats")
    fun getStats(): Map<String, Any> {
        return documentService.getStats()
    }

    /** Clear all stored specifications */
    @DeleteMapping("/clear")
    fun clearAll(): Map<String, String> {
        documentService.clearAll()
        return mapOf("message" to "All specifications cleared")
    }

    /** Health check */
    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf("status" to "UP", "service" to "OpenAPI Spec Parser", "version" to "1.0.0")
    }
}

/** Search request model */
data class SpecSearchRequest(
        val query: String,
        val topK: Int? = 5,
        val includeRelatedSchemas: Boolean? = true,
        val responseFormat: String? = "meta" // "yaml" or "meta"
)
