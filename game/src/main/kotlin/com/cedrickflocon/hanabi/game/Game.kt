package com.cedrickflocon.hanabi.game

class Game(
    private val playerNumber: Int, private val deck: Deck
) {

    companion object {
        private const val HINT_MAX = 8
        private val CARD_DISTRIBUTION = mapOf(2 to 5, 3 to 5, 4 to 4, 5 to 4)
    }

    init {
        require(playerNumber in 2..5)
    }

    private val _board: Map<Color, MutableList<Card>> = Color.entries.associateWith { mutableListOf() }
    val board: Map<Color, List<Card>>
        get() = _board

    private val _discard: MutableList<Card> = mutableListOf()
    val discard: List<Card>
        get() = _discard

    private var lastTurn: Int = playerNumber + 1
    var turn: Int = 0
        private set

    val playerTurn: Int
        get() = turn % playerNumber

    val score: Int
        get() = _board.values.sumOf { it.size }

    var life: Int = 3
        private set

    var hint: Int = HINT_MAX
        private set

    private val _hintList = mutableListOf<Pair<Action.Hint, Int>>()
    val hintList: List<Pair<Action.Hint, Int>>
        get() = _hintList

    private val _playerCard = (0..<playerNumber).associateWith {
        (0..<CARD_DISTRIBUTION[playerNumber]!!).map { deck.pick() }.toMutableList()
    }
    val playerCard: Map<Int, List<Card>>
        get() = _playerCard

    fun play(action: Action): Int? {
        if (lastTurn == 0) {
            throw IllegalStateException("Game is finished")
        }

        when (action) {
            is Action.Discard -> discardAction(action)
            is Action.Play -> playAction(action)
            is Action.Hint -> hintAction(action)
        }

        if (deck.cardNumber == 0) {
            lastTurn--
            if (lastTurn == 0) {
                return score
            }
        }

        turn++
        return null
    }

    private fun discardAction(action: Action.Discard) {
        discard(action.card)
        if (hint < HINT_MAX) {
            hint++
        }
    }

    private fun playAction(action: Action.Play) {
        val lastValue = _board[action.card.color]!!.lastOrNull()?.value ?: 0
        if (lastValue + 1 == action.card.value) {
            _board[action.card.color]!!.add(retrieve(action.card))
            pickCard()
            if (action.card.value == 5) {
                hint++
            }
        } else {
            discard(action.card)
            life--
        }
    }

    private fun hintAction(action: Action.Hint) {
        if (hint == 0) {
            throw IllegalArgumentException("No more hint")
        }

        if (action.player == playerTurn || action.player !in (0..<playerNumber)) {
            throw IllegalArgumentException("Invalid hint for player ${action.player}")
        }

        val exhaustive = _playerCard[action.player]!!.filter {
            when (action) {
                is Action.HintColor -> it.color == action.color
                is Action.HintValue -> it.value == action.value
            }
        }.map { it.index }

        if (action.indexes.sorted() != exhaustive.map { it }.sorted()) {
            throw IllegalArgumentException("Not exhaustive hint ${action.indexes}")
        }

        hint--
        _hintList.add(action to turn)
    }

    private fun discard(card: Card) {
        _discard.add(retrieve(card))
        pickCard()
    }

    private fun retrieve(card: Card): Card {
        if (!_playerCard[playerTurn]!!.remove(card)) {
            throw IllegalArgumentException("Player $playerTurn do not own $card")
        }
        return card
    }

    private fun pickCard() {
        if (deck.cardNumber > 0) {
            _playerCard[playerTurn]!!.add(deck.pick())
        }
    }

    override fun toString(): String {
        return """
            |Score : $score | Turn : $turn | Player : $playerTurn  
            |Life : $life | Hint: $hint
            |${playerCard.map { "Player ${it.key} : ${it.value}" }.joinToString("\n")}
            |${board.map { "${it.key} : ${it.value}" }.joinToString("\n")}
            |Discard : ${discard.joinToString()}
            |Hints : ${hintList.joinToString { "[Turn:${it.second} | ${it.first}]" }}                          
            |------------------------------------------------------------
            """.trimMargin()
    }
}
