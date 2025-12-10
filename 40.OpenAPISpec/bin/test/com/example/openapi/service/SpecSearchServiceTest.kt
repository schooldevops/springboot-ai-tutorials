package com.example.openapi.service

import com.example.openapi.parser.OpenAPISpecParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource

@SpringBootTest
class SpecSearchServiceTest {

    @Autowired private lateinit var searchService: SpecSearchService

    @Autowired private lateinit var documentService: SpecDocumentService

    @Autowired private lateinit var parser: OpenAPISpecParser

    @BeforeEach
    fun setup() {
        // Load and store petstore spec
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()
        val parsedSpec = parser.parseSpec(yamlContent)
        documentService.clearAll()
        documentService.storeSpec(parsedSpec)
    }

    @Test
    fun `should search for API paths using natural language`() {
        // Given
        val query = "How do I get a list of pets?"

        // When
        val results = searchService.search(query)

        // Then
        assertThat(results).isNotEmpty
        assertThat(results[0].type).isEqualTo("path")
        assertThat(results[0].method).contains("GET")
        assertThat(results[0].path).contains("/pets")
    }

    @Test
    fun `should search for component schemas using natural language`() {
        // Given
        val query = "What is the Pet model structure?"

        // When
        val results = searchService.search(query)

        // Then
        assertThat(results).isNotEmpty
        val petResult = results.find { it.name == "Pet" }
        assertThat(petResult).isNotNull
        assertThat(petResult?.type).isEqualTo("component")
    }

    @Test
    fun `should include related component schemas in path results`() {
        // Given
        val query = "list all pets endpoint"

        // When
        val results = searchService.search(query, includeRelatedSchemas = true)

        // Then
        val pathResult = results.find { it.type == "path" && it.path?.contains("/pets") == true }
        assertThat(pathResult).isNotNull
        assertThat(pathResult?.relatedSchemas).isNotEmpty
    }

    @Test
    fun `should return combined results with path and schema information`() {
        // Given
        val query = "get pets API"

        // When
        val results = searchService.search(query, includeRelatedSchemas = true)

        // Then
        assertThat(results).isNotEmpty

        // Should have path result
        val pathResult = results.find { it.type == "path" }
        assertThat(pathResult).isNotNull

        // Path result should have related schemas
        if (pathResult?.relatedSchemas?.isNotEmpty() == true) {
            assertThat(pathResult.relatedSchemas).isNotEmpty
        }
    }

    @Test
    fun `should search by operation type`() {
        // Given
        val query = "POST request to create pet"

        // When
        val results = searchService.search(query)

        // Then
        assertThat(results).isNotEmpty
        val postResult = results.find { it.method == "POST" }
        assertThat(postResult).isNotNull
    }

    @Test
    fun `should search by parameter name`() {
        // Given
        val query = "API with limit parameter"

        // When
        val results = searchService.search(query)

        // Then
        assertThat(results).isNotEmpty
        val resultWithLimit =
                results.find { it.parameters?.any { param -> param.contains("limit") } == true }
        assertThat(resultWithLimit).isNotNull
    }
}
