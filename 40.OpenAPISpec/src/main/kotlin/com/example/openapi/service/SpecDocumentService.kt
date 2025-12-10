package com.example.openapi.service

import com.example.openapi.model.ComponentDocument
import com.example.openapi.model.ParsedSpec
import com.example.openapi.model.PathDocument
import jakarta.annotation.PostConstruct
import org.springframework.ai.document.Document
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.vectorstore.SimpleVectorStore
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

/**
 * Service for storing OpenAPI specifications in VectorDB
 *
 * Converts parsed paths and components into VectorDB documents with rich metadata for efficient
 * search and retrieval.
 */
@Service
class SpecDocumentService(private val embeddingModel: EmbeddingModel) {

    private lateinit var vectorStore: VectorStore
    private var originalYaml: String? = null // Store original YAML for extraction

    @PostConstruct
    fun init() {
        vectorStore = SimpleVectorStore.builder(embeddingModel).build()
    }

    /** Store entire parsed spec in VectorDB */
    fun storeSpec(parsedSpec: ParsedSpec, yamlContent: String? = null) {
        val pathDocuments = createPathDocuments(parsedSpec.paths)
        val componentDocuments = createComponentDocuments(parsedSpec.components)

        val allDocuments = pathDocuments + componentDocuments
        vectorStore.add(allDocuments)

        // Store original YAML for later extraction
        yamlContent?.let { originalYaml = it }
    }

    /** Create VectorDB documents from path operations */
    fun createPathDocuments(paths: List<PathDocument>): List<Document> {
        return paths.map { path ->
            val metadata =
                    mutableMapOf<String, Any>(
                            "type" to "path",
                            "method" to path.method,
                            "path" to path.path,
                            "tags" to path.tags.joinToString(",")
                    )

            // Add optional fields
            path.operationId?.let { metadata["operationId"] = it }
            path.summary?.let { metadata["summary"] = it }
            path.description?.let { metadata["description"] = it }

            // Add parameters
            if (path.parameters.isNotEmpty()) {
                metadata["parameters"] =
                        path.parameters.joinToString(";") { param ->
                            "${param.name}:${param.type}:${param.location}:${param.required}"
                        }
            }

            // Add request schema
            path.requestSchema?.let { metadata["requestSchema"] = it }

            // Add response schemas
            if (path.responseSchemas.isNotEmpty()) {
                metadata["responseSchemas"] =
                        path.responseSchemas.entries.joinToString(";") { "${it.key}:${it.value}" }
            }

            Document(path.content, metadata)
        }
    }

    /** Create VectorDB documents from component schemas */
    fun createComponentDocuments(components: List<ComponentDocument>): List<Document> {
        return components.map { component ->
            val metadata =
                    mutableMapOf<String, Any>(
                            "type" to "component",
                            "componentType" to component.componentType,
                            "name" to component.name
                    )

            // Add description
            component.description?.let { metadata["description"] = it }

            // Add properties
            if (component.properties.isNotEmpty()) {
                metadata["properties"] =
                        component.properties.joinToString(";") { prop ->
                            "${prop.name}:${prop.type}:${prop.format ?: ""}:${prop.required}"
                        }
            }

            // Add required fields
            if (component.required.isNotEmpty()) {
                metadata["required"] = component.required.joinToString(",")
            }

            Document(component.content, metadata)
        }
    }

    /** Get all documents from VectorDB */
    fun getAllDocuments(): List<Document> {
        // SimpleVectorStore doesn't have a direct method to get all documents
        // We'll use a broad search as a workaround
        return vectorStore.similaritySearch("API specification") ?: emptyList()
    }

    /** Get statistics about stored documents */
    fun getStats(): Map<String, Any> {
        val allDocs = getAllDocuments()
        val pathDocs = allDocs.filter { it.metadata["type"] == "path" }
        val componentDocs = allDocs.filter { it.metadata["type"] == "component" }

        return mapOf(
                "totalDocuments" to allDocs.size,
                "pathDocuments" to pathDocs.size,
                "componentDocuments" to componentDocs.size
        )
    }

    /** Clear all documents from VectorDB */
    fun clearAll() {
        vectorStore = SimpleVectorStore.builder(embeddingModel).build()
        originalYaml = null
    }

    /** Get the VectorStore instance */
    fun getVectorStore(): VectorStore = vectorStore

    /** Get original YAML content */
    fun getOriginalYaml(): String? = originalYaml
}
