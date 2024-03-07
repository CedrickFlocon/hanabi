package com.cedrickflocon.hanabi.game

data class Card(val value: Int, val color: Color, val index: Int) {
    init {
        require(value in 1..5)
        require(index in 0..<50)
    }

    override fun toString(): String {
        return "$value $color"
    }
}
