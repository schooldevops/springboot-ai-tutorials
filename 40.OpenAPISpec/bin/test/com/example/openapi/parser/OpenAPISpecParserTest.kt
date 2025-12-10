package com.example.openapi.parser

import com.example.openapi.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource

@SpringBootTest
class OpenAPISpecParserTest {

    @Autowired private lateinit var parser: OpenAPISpecParser

    @Test
    fun `should parse petstore yaml successfully`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()

        // When
        val result = parser.parseSpec(yamlContent)

        // Then
        assertThat(result).isNotNull
        assertThat(result.info.title).isEqualTo("Swagger Petstore")
        assertThat(result.info.version).isEqualTo("1.0.0")
    }

    @Test
    fun `should extract all paths from petstore`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()

        // When
        val result = parser.parseSpec(yamlContent)

        // Then
        assertThat(result.paths).isNotEmpty
        assertThat(result.paths)
                .hasSizeGreaterThanOrEqualTo(3) // GET /pets, POST /pets, GET /pets/{petId}

        val getPets = result.paths.find { it.method == "GET" && it.path == "/pets" }
        assertThat(getPets).isNotNull
        assertThat(getPets?.operationId).isEqualTo("listPets")
        assertThat(getPets?.tags).contains("pets")
    }

    @Test
    fun `should extract parameters from paths`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()

        // When
        val result = parser.parseSpec(yamlContent)

        // Then
        val getPets = result.paths.find { it.method == "GET" && it.path == "/pets" }
        assertThat(getPets?.parameters).isNotEmpty

        val limitParam = getPets?.parameters?.find { it.name == "limit" }
        assertThat(limitParam).isNotNull
        assertThat(limitParam?.location).isEqualTo("query")
        assertThat(limitParam?.type).isEqualTo("integer")
    }

    @Test
    fun `should extract response schemas from paths`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()

        // When
        val result = parser.parseSpec(yamlContent)

        // Then
        val getPets = result.paths.find { it.method == "GET" && it.path == "/pets" }
        assertThat(getPets?.responseSchemas).isNotEmpty
        assertThat(getPets?.responseSchemas).containsKey("200")
    }

    @Test
    fun `should extract all component schemas`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()

        // When
        val result = parser.parseSpec(yamlContent)

        // Then
        assertThat(result.components).isNotEmpty
        assertThat(result.components).hasSizeGreaterThanOrEqualTo(2) // Pet, Error

        val petSchema = result.components.find { it.name == "Pet" }
        assertThat(petSchema).isNotNull
        assertThat(petSchema?.componentType).isEqualTo("schema")
        assertThat(petSchema?.properties).isNotEmpty
    }

    @Test
    fun `should extract properties from component schemas`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()

        // When
        val result = parser.parseSpec(yamlContent)

        // Then
        val petSchema = result.components.find { it.name == "Pet" }
        assertThat(petSchema?.properties).isNotEmpty

        val idProperty = petSchema?.properties?.find { it.name == "id" }
        assertThat(idProperty).isNotNull
        assertThat(idProperty?.type).isEqualTo("integer")
        assertThat(idProperty?.format).isEqualTo("int64")

        val nameProperty = petSchema?.properties?.find { it.name == "name" }
        assertThat(nameProperty).isNotNull
        assertThat(nameProperty?.type).isEqualTo("string")
        assertThat(nameProperty?.required).isTrue()
    }

    @Test
    fun `should generate human-readable content for paths`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()

        // When
        val result = parser.parseSpec(yamlContent)

        // Then
        val getPets = result.paths.find { it.method == "GET" && it.path == "/pets" }
        assertThat(getPets?.content).isNotBlank()
        assertThat(getPets?.content).containsIgnoringCase("pets")
        assertThat(getPets?.content).containsIgnoringCase("list")
    }

    @Test
    fun `should generate human-readable content for components`() {
        // Given
        val yamlContent =
                ClassPathResource("petstore.yaml").inputStream.readBytes().decodeToString()

        // When
        val result = parser.parseSpec(yamlContent)

        // Then
        val petSchema = result.components.find { it.name == "Pet" }
        assertThat(petSchema?.content).isNotBlank()
        assertThat(petSchema?.content).containsIgnoringCase("Pet")
        assertThat(petSchema?.content).containsIgnoringCase("id")
        assertThat(petSchema?.content).containsIgnoringCase("name")
    }
}
