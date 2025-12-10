package com.example.openapi.service

import com.example.openapi.model.ComponentInfo
import com.example.openapi.model.SearchResult
import org.springframework.ai.document.Document
import org.springframework.stereotype.Service

/**
 * Service for searching OpenAPI specifications using natural language
 *
 * Performs similarity search in VectorDB and combines path results with related component schema
 * information.
 */
@Service
class SpecSearchService(
        private val documentService: SpecDocumentService,
        private val specFormatter: OpenAPISpecFormatter,
        private val yamlExtractor: YamlExtractor
) {

    /**
     * Search for API specifications using natural language
     *
     * @param query Natural language query
     * @param topK Number of results to return
     * @param includeRelatedSchemas Whether to include related component schemas
     * @param responseFormat Format of response: "yaml" (original YAML fragment) or "meta"
     * (generated spec)
     * @return List of search results
     */
    fun search(
            query: String,
            topK: Int = 5,
            includeRelatedSchemas: Boolean = true,
            responseFormat: String = "meta"
    ): List<SearchResult> {
        val vectorStore = documentService.getVectorStore()
        val documents = vectorStore.similaritySearch(query) ?: emptyList()

        val results = documents.take(topK).map { doc -> convertToSearchResult(doc) }

        // Add related schemas for path results
        val resultsWithSchemas =
                if (includeRelatedSchemas) {
                    results.map { result ->
                        if (result.type == "path") {
                            addRelatedSchemas(result)
                        } else {
                            result
                        }
                    }
                } else {
                    results
                }

        // If responseFormat is "yaml", extract from original YAML
        return if (responseFormat == "yaml") {
            extractOriginalYamlFragments(resultsWithSchemas)
        } else {
            resultsWithSchemas
        }
    }

    /**
     * Search and return pure YAML strings
     *
     * @param query Natural language query
     * @param topK Number of results to return
     * @param includeRelatedSchemas Whether to include related component schemas
     * @return List of YAML strings
     */
    fun searchAsYaml(
            query: String,
            topK: Int = 5,
            includeRelatedSchemas: Boolean = true
    ): List<String> {
        val originalYaml = documentService.getOriginalYaml()
        if (originalYaml == null) {
            return emptyList()
        }

        val vectorStore = documentService.getVectorStore()
        val documents = vectorStore.similaritySearch(query) ?: emptyList()

        return documents.take(topK).mapNotNull { doc ->
            val type = doc.metadata["type"] as? String

            when (type) {
                "path" -> {
                    val method = doc.metadata["method"] as? String
                    val path = doc.metadata["path"] as? String
                    if (method != null && path != null) {
                        yamlExtractor.extractPathFragment(originalYaml, method, path)
                    } else null
                }
                "component" -> {
                    val name = doc.metadata["name"] as? String
                    if (name != null) {
                        yamlExtractor.extractComponentFragment(originalYaml, name)
                    } else null
                }
                else -> null
            }
        }
    }

    /** Convert VectorDB document to SearchResult */
    private fun convertToSearchResult(doc: Document): SearchResult {
        val type = doc.metadata["type"] as? String ?: "unknown"

        return if (type == "path") {
            createPathResult(doc)
        } else {
            createComponentResult(doc)
        }
    }

    /** Create SearchResult for path document */
    private fun createPathResult(doc: Document): SearchResult {
        val responseSchemas = parseResponseSchemas(doc.metadata["responseSchemas"] as? String)
        val parameters = parseParameters(doc.metadata["parameters"] as? String)
        val tags = parseTags(doc.metadata["tags"] as? String)
        val method = doc.metadata["method"] as? String
        val path = doc.metadata["path"] as? String

        return SearchResult(
                type = "path",
                content = doc.text ?: "",
                score = 0.0,
                method = method,
                path = path,
                operationId = doc.metadata["operationId"] as? String,
                summary = doc.metadata["summary"] as? String,
                description = doc.metadata["description"] as? String,
                tags = tags,
                parameters = parameters,
                requestSchema = doc.metadata["requestSchema"] as? String,
                responseSchemas = responseSchemas,
                openApiSpec = null // Will be set after adding related schemas
        )
    }

    /** Create SearchResult for component document */
    private fun createComponentResult(doc: Document): SearchResult {
        val properties = parseProperties(doc.metadata["properties"] as? String)
        val required = parseRequired(doc.metadata["required"] as? String)
        val name = doc.metadata["name"] as? String
        val description = doc.metadata["description"] as? String

        // Generate OpenAPI spec for component
        val openApiSpec =
                if (name != null) {
                    specFormatter.generateComponentSpec(
                            name = name,
                            description = description,
                            properties = properties,
                            required = required
                    )
                } else null

        return SearchResult(
                type = "component",
                content = doc.text ?: "",
                score = 0.0,
                name = name,
                componentType = doc.metadata["componentType"] as? String,
                description = description,
                properties = properties,
                required = required,
                openApiSpec = openApiSpec
        )
    }

    /** Add related component schemas to path result */
    private fun addRelatedSchemas(result: SearchResult): SearchResult {
        val schemaNames = mutableSetOf<String>()

        // Extract schema names from response schemas
        result.responseSchemas?.values?.forEach { schemaRef ->
            val schemaName = schemaRef.replace("[]", "")
            schemaNames.add(schemaName)
        }

        // Extract schema name from request schema
        result.requestSchema?.let { schemaNames.add(it) }

        // Fetch component documents for these schemas
        val relatedSchemas =
                schemaNames.mapNotNull { schemaName -> findComponentByName(schemaName) }

        // Generate complete OpenAPI spec with related schemas
        val openApiSpec =
                if (result.method != null && result.path != null) {
                    specFormatter.generatePathSpec(
                            method = result.method,
                            path = result.path,
                            operationId = result.operationId,
                            summary = result.summary,
                            description = result.description,
                            tags = result.tags,
                            parameters = result.parameters,
                            requestSchema = result.requestSchema,
                            responseSchemas = result.responseSchemas,
                            relatedSchemas = relatedSchemas
                    )
                } else null

        return result.copy(relatedSchemas = relatedSchemas, openApiSpec = openApiSpec)
    }

    /** Find component by name */
    private fun findComponentByName(name: String): ComponentInfo? {
        val vectorStore = documentService.getVectorStore()
        val docs = vectorStore.similaritySearch(name) ?: emptyList()

        val componentDoc =
                docs.find { doc ->
                    doc.metadata["type"] == "component" && doc.metadata["name"] == name
                }

        return componentDoc?.let { doc ->
            ComponentInfo(
                    name = doc.metadata["name"] as? String ?: name,
                    description = doc.metadata["description"] as? String,
                    properties = parseProperties(doc.metadata["properties"] as? String),
                    required = parseRequired(doc.metadata["required"] as? String)
            )
        }
    }

    /** Parse response schemas from metadata string */
    private fun parseResponseSchemas(value: String?): Map<String, String>? {
        if (value.isNullOrBlank()) return null

        return value.split(";")
                .mapNotNull { part ->
                    val parts = part.split(":")
                    if (parts.size == 2) parts[0] to parts[1] else null
                }
                .toMap()
    }

    /** Parse parameters from metadata string */
    private fun parseParameters(value: String?): List<String>? {
        if (value.isNullOrBlank()) return null
        return value.split(";")
    }

    /** Parse tags from metadata string */
    private fun parseTags(value: String?): List<String>? {
        if (value.isNullOrBlank()) return null
        return value.split(",").filter { it.isNotBlank() }
    }

    /** Parse properties from metadata string */
    private fun parseProperties(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(";")
    }

    /** Parse required fields from metadata string */
    private fun parseRequired(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").filter { it.isNotBlank() }
    }

    /** Extract original YAML fragments for search results */
    private fun extractOriginalYamlFragments(results: List<SearchResult>): List<SearchResult> {
        val originalYaml = documentService.getOriginalYaml() ?: return results

        return results.map { result ->
            val yamlFragment =
                    when (result.type) {
                        "path" -> {
                            if (result.method != null && result.path != null) {
                                yamlExtractor.extractPathFragment(
                                        originalYaml,
                                        result.method,
                                        result.path
                                )
                            } else null
                        }
                        "component" -> {
                            if (result.name != null) {
                                yamlExtractor.extractComponentFragment(originalYaml, result.name)
                            } else null
                        }
                        else -> null
                    }

            result.copy(openApiSpec = yamlFragment ?: result.openApiSpec)
        }
    }
}
