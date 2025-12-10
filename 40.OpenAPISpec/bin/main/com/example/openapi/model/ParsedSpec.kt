package com.example.openapi.model

/** Parsed OpenAPI specification containing paths and components */
data class ParsedSpec(
        val paths: List<PathDocument>,
        val components: List<ComponentDocument>,
        val info: SpecInfo
)

/** API path operation document */
data class PathDocument(
        val method: String,
        val path: String,
        val operationId: String?,
        val summary: String?,
        val description: String?,
        val tags: List<String>,
        val parameters: List<ParameterInfo>,
        val requestSchema: String?,
        val responseSchemas: Map<String, String>, // status code -> schema name
        val content: String // Human-readable description
)

/** Component schema document */
data class ComponentDocument(
        val componentType: String, // "schema", "parameter", "response"
        val name: String,
        val description: String?,
        val properties: List<PropertyInfo>,
        val required: List<String>,
        val content: String // Human-readable description
)

/** Parameter information */
data class ParameterInfo(
        val name: String,
        val location: String, // "query", "path", "header", "cookie"
        val type: String,
        val required: Boolean,
        val description: String?
)

/** Property information for schemas */
data class PropertyInfo(
        val name: String,
        val type: String,
        val format: String?,
        val description: String?,
        val required: Boolean
)

/** Specification metadata */
data class SpecInfo(val title: String, val version: String, val description: String?)
