package com.example.georgethevetrinator.model.entities

data class Obstacle (
    var row: Int,
    var col: Int,
    var type: ObstacleType = ObstacleType.getRandom(),
) {
    val crashMsgResourceId: Int = type.crashMsgResourceId
    fun moveDown(maxRows: Int): Boolean {
        row++
        return row >= maxRows - 1
    }

    fun crash() {
        this.type = ObstacleType.CRASHED
    }
}