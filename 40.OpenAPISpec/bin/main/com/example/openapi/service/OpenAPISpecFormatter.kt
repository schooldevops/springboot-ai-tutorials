package com.example.openapi.service

import com.example.openapi.model.ComponentInfo
import org.springframework.stereotype.Service

/** Service for generating OpenAPI spec YAML from search results */
@Service
class OpenAPISpecFormatter {

    /** Generate OpenAPI spec for a path operation */
    fun generatePathSpec(
            method: String,
            path: String,
            operationId: String?,
            summary: String?,
            description: String?,
            tags: List<String>?,
            parameters: List<String>?,
            requestSchema: String?,
            responseSchemas: Map<String, String>?,
            relatedSchemas: List<ComponentInfo>?
    ): String {
        val yaml = StringBuilder()

        yaml.appendLine("openapi: 3.0.0")
        yaml.appendLine("info:")
        yaml.appendLine("  title: API Specification")
        yaml.appendLine("  version: 1.0.0")
        yaml.appendLine("paths:")
        yaml.appendLine("  $path:")
        yaml.appendLine("    ${method.lowercase()}:")

        // Operation ID
        operationId?.let { yaml.appendLine("      operationId: $it") }

        // Summary
        summary?.let { yaml.appendLine("      summary: $it") }

        // Description
        description?.let { yaml.appendLine("      description: $it") }

        // Tags
        if (!tags.isNullOrEmpty()) {
            yaml.appendLine("      tags:")
            tags.forEach { tag -> yaml.appendLine("        - $tag") }
        }

        // Parameters
        if (!parameters.isNullOrEmpty()) {
            yaml.appendLine("      parameters:")
            parameters.forEach { param ->
                val parts = param.split(":")
                if (parts.size >= 4) {
                    val (name, type, location, required) = parts
                    yaml.appendLine("        - name: $name")
                    yaml.appendLine("          in: $location")
                    yaml.appendLine("          required: $required")
                    yaml.appendLine("          schema:")
                    yaml.appendLine("            type: $type")
                }
            }
        }

        // Request Body
        requestSchema?.let { schema ->
            yaml.appendLine("      requestBody:")
            yaml.appendLine("        content:")
            yaml.appendLine("          application/json:")
            yaml.appendLine("            schema:")
            yaml.appendLine("              \$ref: '#/components/schemas/$schema'")
        }

        // Responses
        if (!responseSchemas.isNullOrEmpty()) {
            yaml.appendLine("      responses:")
            responseSchemas.forEach { (statusCode, schema) ->
                yaml.appendLine("        '$statusCode':")
                yaml.appendLine("          description: Response")
                yaml.appendLine("          content:")
                yaml.appendLine("            application/json:")
                yaml.appendLine("              schema:")
                if (schema.endsWith("[]")) {
                    val schemaName = schema.removeSuffix("[]")
                    yaml.appendLine("                type: array")
                    yaml.appendLine("                items:")
                    yaml.appendLine("                  \$ref: '#/components/schemas/$schemaName'")
                } else {
                    yaml.appendLine("                \$ref: '#/components/schemas/$schema'")
                }
            }
        }

        // Components (Related Schemas)
        if (!relatedSchemas.isNullOrEmpty()) {
            yaml.appendLine("components:")
            yaml.appendLine("  schemas:")
            relatedSchemas.forEach { component ->
                yaml.appendLine("    ${component.name}:")
                yaml.appendLine("      type: object")

                component.description?.let { yaml.appendLine("      description: $it") }

                if (component.properties.isNotEmpty()) {
                    yaml.appendLine("      properties:")
                    component.properties.forEach { prop ->
                        val parts = prop.split(":")
                        if (parts.size >= 2) {
                            val propName = parts[0]
                            val propType = parts[1]
                            val propFormat = parts.getOrNull(2)?.takeIf { it.isNotBlank() }

                            yaml.appendLine("        $propName:")
                            yaml.appendLine("          type: $propType")
                            propFormat?.let { yaml.appendLine("          format: $it") }
                        }
                    }
                }

                if (component.required.isNotEmpty()) {
                    yaml.appendLine("      required:")
                    component.required.forEach { req -> yaml.appendLine("        - $req") }
                }
            }
        }

        return yaml.toString()
    }

    /** Generate OpenAPI spec for a component schema */
    fun generateComponentSpec(
            name: String,
            description: String?,
            properties: List<String>,
            required: List<String>
    ): String {
        val yaml = StringBuilder()

        yaml.appendLine("openapi: 3.0.0")
        yaml.appendLine("info:")
        yaml.appendLine("  title: API Specification")
        yaml.appendLine("  version: 1.0.0")
        yaml.appendLine("components:")
        yaml.appendLine("  schemas:")
        yaml.appendLine("    $name:")
        yaml.appendLine("      type: object")

        description?.let { yaml.appendLine("      description: $it") }

        if (properties.isNotEmpty()) {
            yaml.appendLine("      properties:")
            properties.forEach { prop ->
                val parts = prop.split(":")
                if (parts.size >= 2) {
                    val propName = parts[0]
                    val propType = parts[1]
                    val propFormat = parts.getOrNull(2)?.takeIf { it.isNotBlank() }

                    yaml.appendLine("        $propName:")
                    yaml.appendLine("          type: $propType")
                    propFormat?.let { yaml.appendLine("          format: $it") }
                }
            }
        }

        if (required.isNotEmpty()) {
            yaml.appendLine("      required:")
            required.forEach { req -> yaml.appendLine("        - $req") }
        }

        return yaml.toString()
    }
}
