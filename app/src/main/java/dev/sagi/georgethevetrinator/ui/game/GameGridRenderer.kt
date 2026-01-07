package dev.sagi.georgethevetrinator.ui.game

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import dev.sagi.georgethevetrinator.R
import dev.sagi.georgethevetrinator.model.entities.Entity
import dev.sagi.georgethevetrinator.model.enums.EntityType
import dev.sagi.georgethevetrinator.model.entities.Player
import dev.sagi.georgethevetrinator.services.SignalImageLoader

class GameGridRenderer(
    private val context: Context,
    private val gridLayout: GridLayout,
    private val rows: Int,
    private val cols: Int
) {
//    ======================================== ATTRIBUTES ========================================
    // === LINKED VIEWS (DYNAMIC) ===
    private lateinit var playerView: AppCompatImageView

    // MATRIX OF EMPTY CELLS (the "game board"). Initialize on declaration, for having it as val
    private val cells: Array<Array<FrameLayout>> = Array(rows) { row ->
        Array(cols) { col ->
            val cell = FrameLayout(context)
            // Create layout parameters (for equal sizing)
            cell.layoutParams = createCellParams(row, col)
            // Add this cell:
            gridLayout.addView(cell)
            cell // return to array
        }
    }

    // === RECYCLED OBSTACLES VIEW POOLS ===
    private val obstaclePool = mutableListOf<AppCompatImageView>()
    private val usedObstacles = mutableListOf<AppCompatImageView>()


//    ======================================== FUNCTIONS ========================================
    // === INITIALIZATION ===
    init {
        gridLayout.rowCount = rows
        gridLayout.columnCount = cols
        initPlayer()
    }
    private fun initPlayer() {
        playerView = AppCompatImageView(context)
        // Set the image:
        SignalImageLoader.getInstance().loadImage(R.drawable.img_player_george, playerView)
        // Set width and height to match the matrix's cell:
        playerView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        playerView.scaleType = ImageView.ScaleType.FIT_END
    }

    // === DRAW FUNCTIONS ===
    fun renderPlayer(player: Player) {
        // Remove player from old parent (if it has one)
        (playerView.parent as? ViewGroup)?.removeView(playerView)

        // Add player to new cell
        val playerCell = cells[player.row][player.col]
        playerCell.addView(playerView)
    }


    fun renderObstacles(entities: List<Entity>) {
        // 1. Recycle, then clear grid
        recycleObstacleViews()
        for (obs in entities) {
            renderObstacle(obs)
        }
    }

    private fun renderObstacle(entity: Entity) {
        val obstacleView = getObstacleView(entity.type)

        // Insert to parent cell and add view:
        val cell = cells[entity.row][entity.col]
        cell.addView(obstacleView)
    }

    // === REFRESH OBSTACLES VIEWS ===
    private fun getObstacleView(type: EntityType): AppCompatImageView {
        val view: AppCompatImageView
        if (obstaclePool.isNotEmpty()) {
            view = obstaclePool.removeAt(0) // Recycle old view
        } else {
            view = AppCompatImageView(context) // Create new one only if needed
        }

        // Set the drawable resource:
        SignalImageLoader.getInstance().loadImage(type.drawableResourceId, view)

        usedObstacles.add(view) // Track it for recycling it in next frame
        return view
    }

    private fun recycleObstacleViews() {
        // Add all used views to pool AND remove them from the grid
        for (view in usedObstacles) {
            (view.parent as? ViewGroup)?.removeView(view) // Detach from grid
            obstaclePool.add(view) // Add to pool
        }
        // Clear usedObstacles:
        usedObstacles.clear()
    }

    // === HELPER FUNCTIONS ===
    private fun createCellParams(row: Int, col: Int): GridLayout.LayoutParams {
        val parameters = GridLayout.LayoutParams()
        parameters.width = 0
        parameters.height = 0
        parameters.rowSpec = GridLayout.spec(row, 1.0f)
        parameters.columnSpec = GridLayout.spec(col, 1.0f)
        return parameters
    }

    // === RELEASE FROM MEMORY (extra safety layer, even though this context is owned by GameActivity)
    fun release() {
        // 1. Clear the view pools
        obstaclePool.clear()
        usedObstacles.clear()

        // 2. Remove all views from the grid
        gridLayout.removeAllViews()
    }
}
