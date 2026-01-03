package com.example.georgethevetrinator.model.entities

data class ScoreRecord(
    val playerName: String,
    val score: Int,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
