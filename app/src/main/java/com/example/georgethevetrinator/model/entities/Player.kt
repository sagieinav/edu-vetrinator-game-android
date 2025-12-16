package com.example.georgethevetrinator.model.entities

data class Player(
    var row: Int,
    var col: Int,
    val maxCols: Int
) {
    fun moveLeft() {
        if (col > 0) col--
    }

    fun moveRight() {
        if (col < maxCols - 1) col++
    }
}
