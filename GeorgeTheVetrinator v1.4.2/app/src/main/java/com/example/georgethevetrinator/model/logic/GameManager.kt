package com.example.georgethevetrinator.model.logic

import com.example.georgethevetrinator.model.entities.GameMode
import com.example.georgethevetrinator.model.entities.MoveDirection
import com.example.georgethevetrinator.model.entities.Obstacle
import com.example.georgethevetrinator.model.entities.Player

class GameManager(private val maxHealth: Int = 3, val rows: Int, val cols: Int, val gameMode: GameMode) {
//    ======================================== ATTRIBUTES ========================================
    private var currentFrame : Int = 0

    var currentHealth : Int = maxHealth
        private set

    private var gameResettingObstacle: Obstacle?= null // Potentially a 'CRASH' obstacle that reduces life to zero

//    ======================================== GAME OBJECTS ========================================
    val player = Player(rows - 1, cols / 2, cols)
    val activeObstacles = mutableListOf<Obstacle>()
    var crashMsgResourceId: Int = 0
        private set

//    ======================================== EVENT CALLBACKS ========================================
    var onCollision: (() -> Unit)? = null // type is Unit (void), Elvis for nullable, and set it to null for now
    var onGameOver: (() -> Unit)? = null



//    ======================================== FUNCTIONS ========================================


    // === GAME PROGRESS ===
    fun advanceGame() {
        // 1. Advance all active obstacles:
        advanceActiveObstacles()
        // 2. Check if there's a collision after obstacles have moved:
        gameResettingObstacle?.let { crasher ->
            activeObstacles.clear()
            activeObstacles.add(crasher)
            // Reset the tracker, so we don't clear the game next frame:
            gameResettingObstacle = null
        }
        // 3. Spawn more obstacles and frame++:
        if (currentFrame % 2 == 0) spawnObstacle()
        currentFrame++
    }

    // === OBSTACLES BEHAVIOR ===
    private fun spawnObstacle() {
        val randomCol = (0 until cols).random()
        activeObstacles.add(Obstacle(0, randomCol))
    }
    private fun advanceActiveObstacles() {
        val iterator = activeObstacles.iterator()
        while (iterator.hasNext()) {
            val currentObstacle = iterator.next()
            if (currentObstacle.moveDown(rows)) { // Move to next line and check if reached EoL
                val shouldRemove = processObstacleInteraction(currentObstacle)
                if (shouldRemove) iterator.remove()
            }
        }
    }

    private fun processObstacleInteraction(obstacle: Obstacle) : Boolean {
        // Check if reached last row (end of life):
        if (obstacle.row == rows - 1) {
            // Check collision:
            if (obstacle.col == player.col) {
                handleCollision(obstacle)
                return false // Should not remove! (to show crash icon)
            }
            else {
                return true // Remove it
            }
        }
        // Safety layer cleanup (if it somehow skips the last row)
        else if (obstacle.row >= rows) return true

        // if somehow its not an EoL obstacle, do not remove:
        return false
    }

    private fun handleCollision(obstacle: Obstacle) {
        // ORDER OF OPERATIONS MATTERS HERE:
        // 1. Deduct health
        currentHealth--
        // 2. Set crash message
        crashMsgResourceId = obstacle.crashMsgResourceId
        // 3. Crash the obstacle
        obstacle.crash()
//        // 4. Finally, invoke the 'onCollision' event in GameActivity
        onCollision?.invoke()

        // 5. Handle game over (normal mode) or restart game (endless mode)
        if (currentHealth <= 0) {
            if (gameMode == GameMode.ENDLESS) {
                restartEndlessGame(obstacle)
            }
            else {
                onGameOver?.invoke()
            }
        }
    }

    private fun restartEndlessGame(crasher: Obstacle) {
        currentHealth = maxHealth
        gameResettingObstacle = crasher
    }

    // === PLAYER BEHAVIOR (MOVEMENT, COLLISION) ===
    fun movePlayer(direction: MoveDirection) {
        if (direction == MoveDirection.LEFT) player.moveLeft()
        else if (direction == MoveDirection.RIGHT) player.moveRight()
    }
}