package dev.sagi.georgethevetrinator.model.entities

import dev.sagi.georgethevetrinator.model.enums.EntityType

data class Entity (
    var row: Int,
    var col: Int,
    var type: EntityType = EntityType.getRandomObstacle(),
) {
    val crashMsgResourceId: Int = type.crashMsgResourceId
    fun moveDown(maxRows: Int): Boolean {
        row++
        return row >= maxRows - 1
    }

    fun crash() {
        this.type = EntityType.CRASHED
    }

    fun collected() {
        this.type = EntityType.COLLECTED
    }
}