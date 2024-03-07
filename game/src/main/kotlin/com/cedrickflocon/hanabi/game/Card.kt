package com.cedrickflocon.hanabi.game

data class Card(
    val value: Int, val color: Color
) {
    init {
        require(value in 1..5)
    }

    override fun toString(): String {
        return "$value $color"
    }
}
