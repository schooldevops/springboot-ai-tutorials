package com.example.openapi.service

import com.example.openapi.parser.OpenAPISpecParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource

@SpringBootTest
class SpecDocumentServiceTest {

    @Autowired private lateinit var specDocumentService: SpecDocumentService

    @Autowired private lateinit var parser: OpenAPISpecParser

    @Test
    fun `should store parsed spec in VectorDB`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()
        val parsedSpec = parser.parseSpec(yamlContent)

        // When
        specDocumentService.storeSpec(parsedSpec)

        // Then
        val stats = specDocumentService.getStats()
        assertThat(stats["totalDocuments"]).isNotNull
        assertThat(stats["pathDocuments"] as Int).isGreaterThan(0)
        assertThat(stats["componentDocuments"] as Int).isGreaterThan(0)
    }

    @Test
    fun `should create documents for all paths`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()
        val parsedSpec = parser.parseSpec(yamlContent)

        // When
        specDocumentService.clearAll()
        specDocumentService.storeSpec(parsedSpec)

        // Then
        val stats = specDocumentService.getStats()
        assertThat(stats["pathDocuments"]).isEqualTo(parsedSpec.paths.size)
    }

    @Test
    fun `should create documents for all components`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()
        val parsedSpec = parser.parseSpec(yamlContent)

        // When
        specDocumentService.clearAll()
        specDocumentService.storeSpec(parsedSpec)

        // Then
        val stats = specDocumentService.getStats()
        assertThat(stats["componentDocuments"]).isEqualTo(parsedSpec.components.size)
    }

    @Test
    fun `should include proper metadata in path documents`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()
        val parsedSpec = parser.parseSpec(yamlContent)

        // When
        specDocumentService.clearAll()
        val documents = specDocumentService.createPathDocuments(parsedSpec.paths)

        // Then
        val getPetsDoc =
                documents.find { it.metadata["method"] == "GET" && it.metadata["path"] == "/pets" }
        assertThat(getPetsDoc).isNotNull
        assertThat(getPetsDoc?.metadata)
                .containsKeys("type", "method", "path", "operationId", "tags")
        assertThat(getPetsDoc?.metadata?.get("type")).isEqualTo("path")
    }

    @Test
    fun `should include proper metadata in component documents`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()
        val parsedSpec = parser.parseSpec(yamlContent)

        // When
        specDocumentService.clearAll()
        val documents = specDocumentService.createComponentDocuments(parsedSpec.components)

        // Then
        val petDoc = documents.find { it.metadata["name"] == "Pet" }
        assertThat(petDoc).isNotNull
        assertThat(petDoc?.metadata)
                .containsKeys("type", "componentType", "name", "properties", "required")
        assertThat(petDoc?.metadata?.get("type")).isEqualTo("component")
    }

    @Test
    fun `should link paths with component schemas in metadata`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()
        val parsedSpec = parser.parseSpec(yamlContent)

        // When
        specDocumentService.clearAll()
        val documents = specDocumentService.createPathDocuments(parsedSpec.paths)

        // Then
        val getPetsDoc =
                documents.find { it.metadata["method"] == "GET" && it.metadata["path"] == "/pets" }
        assertThat(getPetsDoc?.metadata).containsKey("responseSchemas")
        assertThat(getPetsDoc?.metadata?.get("responseSchemas") as? String).isNotBlank()
    }
}
