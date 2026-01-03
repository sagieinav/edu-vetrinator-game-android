package com.example.georgethevetrinator.logic

import com.example.georgethevetrinator.model.entities.GameControls
import com.example.georgethevetrinator.model.entities.GameDifficulty
import com.example.georgethevetrinator.model.entities.GameMode
import com.example.georgethevetrinator.model.entities.MoveDirection
import com.example.georgethevetrinator.model.entities.Entity
import com.example.georgethevetrinator.model.entities.EntityType
import com.example.georgethevetrinator.model.entities.Player
import com.example.georgethevetrinator.utilities.Constants
import kotlin.random.Random

class GameManager(
    private val maxHealth: Int = 3,
    val rows: Int, val cols: Int,
    val gameMode: GameMode,
    val gameDifficulty: GameDifficulty,
    val gameControls: GameControls) {
    //    ======================================== ATTRIBUTES ========================================
    private var currentFrame : Int = 0
    private var framesSinceLastSpawn: Int = 0
    private var obstacleSpawnProb = Constants.Game.BASE_OBSTACLE_PROB
    var score: Int = 0
    var speedMultiplier: Float = 1.0f // For boosting with tilt


    var currentHealth : Int = maxHealth
        private set

    private var gameResettingEntity: Entity ?= null // Potentially a 'CRASH' obstacle that reduces life to zero

    //    ======================================== GAME OBJECTS ========================================
    val player = Player(rows - 1, cols / 2, cols)
    val activeEntities = mutableListOf<Entity>()
    var crashMsgResourceId: Int = 0
        private set

    //    ======================================== EVENT CALLBACKS ========================================
    var onObstacleHit: (() -> Unit)? = null // type is Unit (void), Elvis for nullable, and set it to null for now
    var onCoinCollected: (() -> Unit)? = null
    var onGameOver: (() -> Unit)? = null



//    ======================================== FUNCTIONS ========================================


    // === GAME PROGRESS ===
    fun advanceGame() {
        currentFrame++
        framesSinceLastSpawn++
        // 1. Advance all active obstacles:
        advanceActiveObstacles()
        // 2. Check if there's a collision after obstacles have moved:
        gameResettingEntity?.let { crasher ->
            activeEntities.clear()
            activeEntities.add(crasher)
            // Reset the tracker, so we don't clear the game next frame:
            gameResettingEntity = null
        }
        // 3. (Potentially) spawn more entities:
        spawnEntity()

        // 4. Increment score based on odometer:
        score += (10 * speedMultiplier).toInt()
    }

    private fun spawnEntity() {
        // Try to spawn an Obstacle
        if (Random.nextDouble() < Constants.Game.BASE_OBSTACLE_PROB) {
            framesSinceLastSpawn = 0
            obstacleSpawnProb = Constants.Game.BASE_OBSTACLE_PROB
            spawnObstacle()
        }
        else {
            obstacleSpawnProb += 0.25
            // ONLY if no obstacle spawned, try to spawn a Coin
            if (Random.nextDouble() < Constants.Game.COIN_PROB) {
                spawnCoin()
            }
        }


    }
    // === OBSTACLES BEHAVIOR ===
    private fun spawnObstacle() {
        val randomCol = (0 until cols).random()
        activeEntities.add(Entity(0, randomCol))
    }

    private fun spawnCoin() {
        val randomCol = (0 until cols).random()
        activeEntities.add(Entity(0, randomCol, EntityType.COIN))
    }
    private fun advanceActiveObstacles() {
        val iterator = activeEntities.iterator()
        while (iterator.hasNext()) {
            val currentObstacle = iterator.next()
            if (currentObstacle.moveDown(rows)) { // Move to next line and check if reached EoL
                val shouldRemove = processEntityInteraction(currentObstacle)
                if (shouldRemove) iterator.remove()
            }
        }
    }

    private fun processEntityInteraction(entity: Entity) : Boolean {
        // Check if reached last row (end of life):
        if (entity.row == rows - 1) {
            // Check collision:
            if (entity.col == player.col) {
                handleCollision(entity)
                return false // Should not remove! (to show crash icon)
            }
            else {
                return true // Remove it
            }
        }
        // Safety layer cleanup (if it somehow skips the last row)
        else if (entity.row >= rows) return true

        // if somehow its not an EoL obstacle, do not remove:
        return false
    }

    private fun handleCollision(entity: Entity) {
        if (entity.type == EntityType.COIN) handleCoinCollected(entity)
        else handleObstacleHit(entity)
    }

    private fun handleCoinCollected(coin: Entity) {
        // 1. Increase score
        score += Constants.Game.COIN_BONUS;
        // 2. Set collision message
        crashMsgResourceId = coin.crashMsgResourceId
        // 3. Mark the coin as collected
        coin.collected()
        // 4. Finally, invoke the 'onCollected' event in GameActivity
        onCoinCollected?.invoke()
    }

    private fun handleObstacleHit(obstacle: Entity) {
        // ORDER OF OPERATIONS MATTERS HERE:
        // 1. Deduct health
        currentHealth--
        // 2. Set crash message
        crashMsgResourceId = obstacle.crashMsgResourceId
        // 3. Crash the obstacle
        obstacle.crash()
        // 4. Finally, invoke the 'onObstacleHit' event in GameActivity
        onObstacleHit?.invoke()
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

    // === PLAYER BEHAVIOR (MOVEMENT, COLLISION) ===
    fun movePlayer(direction: MoveDirection) {
        if (direction == MoveDirection.LEFT) player.moveLeft()
        else if (direction == MoveDirection.RIGHT) player.moveRight()
    }


    // === GAME RESETTING / RESTARTING ===
    private fun restartEndlessGame(crasher: Entity) {
        currentHealth = maxHealth
        gameResettingEntity = crasher
    }
    fun resetGame() {
        score = 0
        currentHealth = maxHealth
        activeEntities.clear()
        player.col = cols / 2
    }
}

