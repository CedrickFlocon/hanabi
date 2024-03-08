package com.cedrickflocon.hanabi.game

class Game(
    val playerNumber: Int,
    private val deck: Deck
) {

    companion object {
        private const val HINT_MAX = 7
        private val CARD_DISTRIBUTION = mapOf(2 to 5, 3 to 5, 4 to 4, 5 to 4)
    }

    init {
        require(playerNumber in 2..5)
    }

    var turn: Int = 0
        private set

    var score: Int = 0
        private set

    var life: Int = 3
        private set

    var hint: Int = HINT_MAX
        private set

    private val _playerCard: Map<Int, MutableList<Card>> = (0..<playerNumber).associateWith {
        (0..<CARD_DISTRIBUTION[playerNumber]!!)
            .map { deck.pick() }
            .toMutableList()
    }
    val playerCard: Map<Int, List<Card>> = _playerCard

    override fun toString(): String {
        return """
            |Turn : $turn | Score : $score
            |Life : $life | Hint: $hint
            |${playerCard.map { "Player ${it.key} : ${it.value}" }.joinToString("\n")}
        """.trimMargin()
    }
}
