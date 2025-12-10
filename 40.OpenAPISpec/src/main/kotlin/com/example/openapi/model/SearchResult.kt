package com.example.openapi.model

/** Search result combining path and component information */
data class SearchResult(
        val type: String, // "path" or "component"
        val content: String,
        val score: Double,

        // Path-specific fields
        val method: String? = null,
        val path: String? = null,
        val operationId: String? = null,
        val summary: String? = null,
        val description: String? = null,
        val tags: List<String>? = null,
        val parameters: List<String>? = null,
        val requestSchema: String? = null,
        val responseSchemas: Map<String, String>? = null,

        // Component-specific fields
        val name: String? = null,
        val componentType: String? = null,
        val properties: List<String>? = null,
        val required: List<String>? = null,

        // Related schemas (for path results)
        val relatedSchemas: List<ComponentInfo>? = null,

        // Complete OpenAPI spec in YAML format
        val openApiSpec: String? = null
)

/** Component schema information */
data class ComponentInfo(
        val name: String,
        val description: String?,
        val properties: List<String>,
        val required: List<String>
)
