package com.example.image.controller

import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import org.springframework.ai.image.ImageModel
import org.springframework.ai.image.ImagePrompt
import org.springframework.ai.openai.OpenAiImageOptions
import org.springframework.web.bind.annotation.*

/** Sample 01: OpenAI DALL-E Image Generation */
@RestController
@RequestMapping("/api/image")
class OpenAIImageController(private val imageModel: ImageModel) {

    data class ImageRequest(
            val prompt: String,
            val width: Int = 1024,
            val height: Int = 1024,
            val quality: String = "hd",
            val style: String = "vivid"
    )

    @PostMapping("/generate")
    fun generateImage(@RequestBody request: ImageRequest): Map<String, Any> {
        val options =
                OpenAiImageOptions.builder()
                        .withModel("dall-e-3")
                        .withWidth(request.width)
                        .withHeight(request.height)
                        .withQuality(request.quality)
                        .withStyle(request.style)
                        .build()

        val imagePrompt = ImagePrompt(request.prompt, options)
        val response = imageModel.call(imagePrompt)

        val imageGeneration = response.result
        val imageUrl = imageGeneration.output.url

        return mapOf(
                "provider" to "OpenAI DALL-E 3",
                "prompt" to request.prompt,
                "url" to imageUrl,
                "size" to "${request.width}x${request.height}",
                "quality" to request.quality,
                "style" to request.style
        )
    }

    @PostMapping("/generate-and-save")
    fun generateAndSave(@RequestBody request: ImageRequest): Map<String, Any> {
        val options =
                OpenAiImageOptions.builder()
                        .withModel("dall-e-3")
                        .withWidth(request.width)
                        .withHeight(request.height)
                        .build()

        val imagePrompt = ImagePrompt(request.prompt, options)
        val response = imageModel.call(imagePrompt)

        val imageUrl = response.result.output.url
        val fileName = "generated_${System.currentTimeMillis()}.png"
        val outputPath = Paths.get("images", fileName)

        // Create directory if not exists
        Files.createDirectories(outputPath.parent)

        // Download and save image
        URL(imageUrl).openStream().use { input -> Files.copy(input, outputPath) }

        return mapOf(
                "provider" to "OpenAI DALL-E 3",
                "prompt" to request.prompt,
                "url" to imageUrl,
                "savedPath" to outputPath.toString()
        )
    }

    @GetMapping("/info")
    fun getInfo(): Map<String, Any> {
        return mapOf(
                "provider" to "OpenAI",
                "model" to "DALL-E 3",
                "features" to
                        listOf(
                                "High quality image generation",
                                "Multiple sizes: 1024x1024, 1024x1792, 1792x1024",
                                "Quality: standard or hd",
                                "Style: vivid or natural"
                        ),
                "pricing" to mapOf("standard" to "$0.040 per image", "hd" to "$0.080 per image")
        )
    }
}
