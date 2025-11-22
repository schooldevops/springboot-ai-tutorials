package com.example.productimagetag.controller

import com.example.productimagetag.dto.AnalysisResponse
import com.example.productimagetag.dto.ProductTags
import com.example.productimagetag.service.ImageAnalysisService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import kotlin.system.measureTimeMillis

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageAnalysisService: ImageAnalysisService
) {

    @PostMapping("/analyze", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun analyzeImage(@RequestParam("file") file: MultipartFile): AnalysisResponse {
        val imageBytes = file.bytes
        
        var productTags: ProductTags
        val processingTime = measureTimeMillis {
            productTags = imageAnalysisService.analyzeProductImage(imageBytes)
        }
        
        return AnalysisResponse(
            productTags = productTags,
            processingTime = processingTime
        )
    }
    
    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "Product Image Tag Generator"
        )
    }
}
