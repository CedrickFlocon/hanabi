package com.cedrickflocon.hanabi.game

class Game {

    companion object {
        private const val HINT_MAX = 7
    }

    var turn: Int = 0
        private set

    var score: Int = 0
        private set

    var life: Int = 3
        private set

    var hint: Int = HINT_MAX
        private set

    override fun toString(): String {
        return """
        Turn : $turn | Score : $score
        Life : $life | Hint: $hint
        """.trimIndent()
    }
}
