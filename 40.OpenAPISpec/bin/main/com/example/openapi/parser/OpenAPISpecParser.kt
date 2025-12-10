package com.example.openapi.parser

import com.example.openapi.model.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.parser.OpenAPIV3Parser
import org.springframework.stereotype.Component

/**
 * OpenAPI Specification Parser
 *
 * Parses OpenAPI YAML/JSON files and extracts paths and components into structured documents for
 * VectorDB storage.
 */
@Component
class OpenAPISpecParser {

    private val parser = OpenAPIV3Parser()

    /** Parse OpenAPI specification from YAML/JSON content */
    fun parseSpec(yamlContent: String): ParsedSpec {
        val parseResult = parser.readContents(yamlContent)
        val openAPI =
                parseResult.openAPI
                        ?: throw IllegalArgumentException(
                                "Failed to parse OpenAPI spec: ${parseResult.messages}"
                        )

        val paths = extractPaths(openAPI)
        val components = extractComponents(openAPI)
        val info = extractInfo(openAPI)

        return ParsedSpec(paths, components, info)
    }

    /** Extract all path operations from OpenAPI spec */
    private fun extractPaths(openAPI: OpenAPI): List<PathDocument> {
        val paths = mutableListOf<PathDocument>()

        openAPI.paths?.forEach { (pathString, pathItem) ->
            pathItem.readOperationsMap()?.forEach { (httpMethod, operation) ->
                paths.add(createPathDocument(pathString, httpMethod.name, operation, pathItem))
            }
        }

        return paths
    }

    /** Create PathDocument from operation */
    private fun createPathDocument(
            path: String,
            method: String,
            operation: Operation,
            pathItem: PathItem
    ): PathDocument {
        val parameters = extractParameters(operation, pathItem)
        val responseSchemas = extractResponseSchemas(operation)
        val requestSchema = extractRequestSchema(operation)

        val content =
                buildPathContent(
                        method,
                        path,
                        operation,
                        parameters,
                        requestSchema,
                        responseSchemas
                )

        return PathDocument(
                method = method,
                path = path,
                operationId = operation.operationId,
                summary = operation.summary,
                description = operation.description,
                tags = operation.tags ?: emptyList(),
                parameters = parameters,
                requestSchema = requestSchema,
                responseSchemas = responseSchemas,
                content = content
        )
    }

    /** Extract parameters from operation */
    private fun extractParameters(operation: Operation, pathItem: PathItem): List<ParameterInfo> {
        val params = mutableListOf<ParameterInfo>()

        // Operation-level parameters
        operation.parameters?.forEach { param ->
            params.add(
                    ParameterInfo(
                            name = param.name,
                            location = param.`in`,
                            type = param.schema?.type ?: "string",
                            required = param.required ?: false,
                            description = param.description
                    )
            )
        }

        // Path-level parameters
        pathItem.parameters?.forEach { param ->
            if (params.none { it.name == param.name }) {
                params.add(
                        ParameterInfo(
                                name = param.name,
                                location = param.`in`,
                                type = param.schema?.type ?: "string",
                                required = param.required ?: false,
                                description = param.description
                        )
                )
            }
        }

        return params
    }

    /** Extract response schemas from operation */
    private fun extractResponseSchemas(operation: Operation): Map<String, String> {
        val schemas = mutableMapOf<String, String>()

        operation.responses?.forEach { (statusCode, response) ->
            response.content?.forEach { (_, mediaType) ->
                val schemaRef = mediaType.schema?.`$ref`
                if (schemaRef != null) {
                    schemas[statusCode] = extractSchemaName(schemaRef)
                } else if (mediaType.schema is ArraySchema) {
                    val arraySchema = mediaType.schema as ArraySchema
                    val itemRef = arraySchema.items?.`$ref`
                    if (itemRef != null) {
                        schemas[statusCode] = extractSchemaName(itemRef) + "[]"
                    }
                }
            }
        }

        return schemas
    }

    /** Extract request schema from operation */
    private fun extractRequestSchema(operation: Operation): String? {
        val requestBody = operation.requestBody ?: return null

        requestBody.content?.forEach { (_, mediaType) ->
            val schemaRef = mediaType.schema?.`$ref`
            if (schemaRef != null) {
                return extractSchemaName(schemaRef)
            }
        }

        return null
    }

    /** Extract schema name from reference */
    private fun extractSchemaName(ref: String): String {
        return ref.substringAfterLast("/")
    }

    /** Build human-readable content for path */
    private fun buildPathContent(
            method: String,
            path: String,
            operation: Operation,
            parameters: List<ParameterInfo>,
            requestSchema: String?,
            responseSchemas: Map<String, String>
    ): String {
        val parts = mutableListOf<String>()

        // Method and path
        parts.add("$method $path")

        // Summary/Description
        if (operation.summary != null) {
            parts.add("- ${operation.summary}")
        } else if (operation.description != null) {
            parts.add("- ${operation.description}")
        }

        // Parameters
        if (parameters.isNotEmpty()) {
            val paramDesc =
                    parameters.joinToString(", ") {
                        "${it.name} (${it.location}, ${it.type}${if (it.required) ", required" else ""})"
                    }
            parts.add("Parameters: $paramDesc")
        }

        // Request body
        if (requestSchema != null) {
            parts.add("Request body: $requestSchema")
        }

        // Responses
        if (responseSchemas.isNotEmpty()) {
            val responseDesc =
                    responseSchemas.entries.joinToString(", ") { "${it.key} (${it.value})" }
            parts.add("Responses: $responseDesc")
        }

        return parts.joinToString(". ")
    }

    /** Extract all component schemas from OpenAPI spec */
    private fun extractComponents(openAPI: OpenAPI): List<ComponentDocument> {
        val components = mutableListOf<ComponentDocument>()

        openAPI.components?.schemas?.forEach { (name, schema) ->
            components.add(createComponentDocument(name, schema))
        }

        return components
    }

    /** Create ComponentDocument from schema */
    private fun createComponentDocument(name: String, schema: Schema<*>): ComponentDocument {
        val properties = extractProperties(schema)
        val required = schema.required ?: emptyList()
        val content = buildComponentContent(name, schema, properties, required)

        return ComponentDocument(
                componentType = "schema",
                name = name,
                description = schema.description,
                properties = properties,
                required = required,
                content = content
        )
    }

    /** Extract properties from schema */
    private fun extractProperties(schema: Schema<*>): List<PropertyInfo> {
        val properties = mutableListOf<PropertyInfo>()
        val required = schema.required ?: emptyList()

        schema.properties?.forEach { (propName, propSchema) ->
            properties.add(
                    PropertyInfo(
                            name = propName,
                            type = propSchema.type ?: "object",
                            format = propSchema.format,
                            description = propSchema.description,
                            required = required.contains(propName)
                    )
            )
        }

        return properties
    }

    /** Build human-readable content for component */
    private fun buildComponentContent(
            name: String,
            schema: Schema<*>,
            properties: List<PropertyInfo>,
            required: List<String>
    ): String {
        val parts = mutableListOf<String>()

        parts.add("$name schema")

        if (schema.description != null) {
            parts.add("- ${schema.description}")
        }

        if (properties.isNotEmpty()) {
            val propDesc =
                    properties.joinToString(", ") { prop ->
                        val requiredMark = if (prop.required) " (required)" else ""
                        val formatInfo = if (prop.format != null) ", ${prop.format}" else ""
                        "${prop.name}: ${prop.type}$formatInfo$requiredMark"
                    }
            parts.add("Properties: $propDesc")
        }

        return parts.joinToString(". ")
    }

    /** Extract spec info */
    private fun extractInfo(openAPI: OpenAPI): SpecInfo {
        return SpecInfo(
                title = openAPI.info?.title ?: "Unknown",
                version = openAPI.info?.version ?: "1.0.0",
                description = openAPI.info?.description
        )
    }
}
