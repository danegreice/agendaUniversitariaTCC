package com.example.projeto

data class Subject(
    val id: String? = null,
    val code: String? = null,
    val name: String? = null,
    val teacher: String? = null,
    val grade: ArrayList<String>? = null,
    val daysOfWeek: ArrayList<Boolean>? = null,
    val startTime: String? = null,
    val endTime: String? = null
) {}