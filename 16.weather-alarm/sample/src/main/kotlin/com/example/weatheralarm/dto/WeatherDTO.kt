package com.example.weatheralarm.dto

data class WeatherRequest(
    val city: String
)

data class WeatherData(
    val city: String,
    val temperature: Double,
    val description: String,
    val humidity: Int,
    val windSpeed: Double = 0.0
)

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val message: String,
    val functionCalled: String? = null,
    val weatherData: WeatherData? = null
)

// Weather API Response DTOs
data class WeatherApiResponse(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Weather(
    val description: String
)

data class Wind(
    val speed: Double
)
