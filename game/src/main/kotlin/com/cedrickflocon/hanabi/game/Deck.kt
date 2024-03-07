package com.cedrickflocon.hanabi.game

import java.util.*
import kotlin.random.Random

class Deck(seed: Int) {

    companion object {
        private val DISTRIBUTION = mapOf(1 to 3, 2 to 2, 3 to 2, 4 to 2, 5 to 1)
    }

    private val cards: LinkedList<Card>

    init {
        require(seed in 0..100)

        cards = LinkedList(Color.entries
            .flatMap { color -> DISTRIBUTION.flatMap { (value, repeat) -> (1..repeat).map { value to color } } }
            .shuffled(Random(seed))
            .mapIndexed { index, card -> Card(card.first, card.second, index) }
        )
    }

    val cardNumber: Int
        get() = cards.size

    fun pick(): Card {
        return cards.pop()
    }
}
