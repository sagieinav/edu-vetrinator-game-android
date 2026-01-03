package com.example.georgethevetrinator.model.enums

import com.example.georgethevetrinator.R

enum class EntityType(val drawableResourceId: Int, val crashMsgResourceId: Int) {
    EIGHT_OF_CLUBS(
        R.drawable.obstacle_eight_clubs,
        R.string.obstacle_eight_crash_msg),
    POCKET_KINGS(
        R.drawable.obstacle_pocket_kings,
        R.string.obstacle_kings_crash_msg),
    NETA(
        R.drawable.obstacle_neta,
        R.string.obstacle_neta_crash_msg),
    ANNOYING_KID(
        R.drawable.obstacle_annoying_kid,
        R.string.obstacle_kid_crash_msg),
    CRASHED(
        R.drawable.obstacle_crashed,
        R.string.obstacle_fallback_crash_msg),

    COIN(
        R.drawable.reward_coin,
        R.string.reward_coin_collide_msg),

    COLLECTED(
        R.drawable.reward_coin_collected,
        R.string.reward_coin_collide_msg);


    // Singleton (companion) object to select random type:
    companion object {
        fun getRandomObstacle(): EntityType {
            // Randomly choose one of this enum class' entries. Filter 'CRASHED' out!
            return (entries.filter { it != CRASHED && it != COIN && it != EntityType.COLLECTED})
                .random()
        }
    }
}