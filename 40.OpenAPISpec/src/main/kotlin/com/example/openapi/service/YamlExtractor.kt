package com.example.openapi.service

import org.springframework.stereotype.Service
import org.yaml.snakeyaml.Yaml

/** Service for extracting YAML fragments from original OpenAPI spec */
@Service
class YamlExtractor {

    private val yaml = Yaml()

    /** Extract path fragment from original YAML */
    fun extractPathFragment(originalYaml: String, method: String, path: String): String? {
        try {
            val data = yaml.load<Map<String, Any>>(originalYaml)
            val paths = data["paths"] as? Map<String, Any> ?: return null
            val pathData = paths[path] as? Map<String, Any> ?: return null
            val operationData = pathData[method.lowercase()] as? Map<String, Any> ?: return null

            // Reconstruct YAML fragment
            val fragment = mutableMapOf<String, Any>()
            fragment["openapi"] = data["openapi"] ?: "3.0.0"
            fragment["info"] = data["info"] ?: mapOf("title" to "API", "version" to "1.0.0")
            fragment["paths"] = mapOf(path to mapOf(method.lowercase() to operationData))

            // Add components if referenced
            val components = data["components"] as? Map<String, Any>
            if (components != null) {
                val referencedSchemas = extractReferencedSchemas(operationData)
                if (referencedSchemas.isNotEmpty()) {
                    val schemas = components["schemas"] as? Map<String, Any>
                    if (schemas != null) {
                        val filteredSchemas = schemas.filterKeys { it in referencedSchemas }
                        if (filteredSchemas.isNotEmpty()) {
                            fragment["components"] = mapOf("schemas" to filteredSchemas)
                        }
                    }
                }
            }

            return yaml.dump(fragment)
        } catch (e: Exception) {
            return null
        }
    }

    /** Extract component fragment from original YAML */
    fun extractComponentFragment(originalYaml: String, componentName: String): String? {
        try {
            val data = yaml.load<Map<String, Any>>(originalYaml)
            val components = data["components"] as? Map<String, Any> ?: return null
            val schemas = components["schemas"] as? Map<String, Any> ?: return null
            val schema = schemas[componentName] ?: return null

            // Reconstruct YAML fragment
            val fragment = mutableMapOf<String, Any>()
            fragment["openapi"] = data["openapi"] ?: "3.0.0"
            fragment["info"] = data["info"] ?: mapOf("title" to "API", "version" to "1.0.0")
            fragment["components"] = mapOf("schemas" to mapOf(componentName to schema))

            return yaml.dump(fragment)
        } catch (e: Exception) {
            return null
        }
    }

    /** Extract referenced schema names from operation data */
    private fun extractReferencedSchemas(data: Any): Set<String> {
        val schemas = mutableSetOf<String>()

        when (data) {
            is Map<*, *> -> {
                data.forEach { (key, value) ->
                    if (key == "\$ref" && value is String) {
                        val schemaName = value.substringAfterLast("/")
                        schemas.add(schemaName)
                    } else {
                        schemas.addAll(extractReferencedSchemas(value ?: return@forEach))
                    }
                }
            }
            is List<*> -> {
                data.forEach { item ->
                    schemas.addAll(extractReferencedSchemas(item ?: return@forEach))
                }
            }
        }

        return schemas
    }
}
