package com.example.resumeanalyzer.model

/**
 * 경력 정보
 */
data class WorkExperience(
    val company: String,
    val position: String,
    val startDate: String,
    val endDate: String?,
    val description: String?
)

/**
 * 학력 정보
 */
data class Education(
    val school: String,
    val degree: String,
    val major: String,
    val graduationYear: Int?
)

/**
 * 스킬 정보
 */
data class Skill(
    val name: String,
    val category: String,
    val proficiency: String?
)

/**
 * 전체 이력서 정보
 */
data class ResumeInfo(
    val name: String,
    val email: String?,
    val phone: String?,
    val address: String?,
    val summary: String?,
    val workExperiences: List<WorkExperience> = emptyList(),
    val educations: List<Education> = emptyList(),
    val skills: List<Skill> = emptyList()
)

/**
 * 간단한 이력서 정보 (기본 정보만)
 */
data class BasicResumeInfo(
    val name: String,
    val email: String?,
    val phone: String?,
    val yearsOfExperience: Int?,
    val currentPosition: String?
)
