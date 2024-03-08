package com.cedrickflocon.hanabi.game

sealed interface Action {
    sealed interface Hint : Action {
        val player: Int
        val indexes: List<Int>
    }

    data class HintColor(override val player: Int, val color: Color, override val indexes: List<Int>) : Hint {
        override fun toString(): String {
            return """Player$player=>$color $indexes"""
        }
    }

    data class HintValue(override val player: Int, val value: Int, override val indexes: List<Int>) : Hint {
        override fun toString(): String {
            return """Player$player=>$value $indexes"""
        }
    }

    data class Discard(val card: Card) : Action
    data class Play(val card: Card) : Action
}
