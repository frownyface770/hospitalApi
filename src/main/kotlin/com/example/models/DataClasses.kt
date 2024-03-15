package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Name(val firstName:String, val lastName:String)

@Serializable
enum class Gender {
    MALE, FEMALE, OTHER, NONE
}