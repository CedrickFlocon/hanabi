package com.cedrickflocon.hanabi.game

import com.google.common.truth.Truth.assertThat
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows

class GameTest : DescribeSpec({

    describe("invalid player number") {
        listOf(-1, 0, 1, 6).forEach {
            it("it should throw an exception") {
                assertThrows<IllegalArgumentException> { Game(it, mockk()) }
            }
        }
    }

    describe("valid player number") {
        listOf(2 to 5, 3 to 5, 4 to 4, 5 to 4).forEach { (playerNumber, cardNumber) ->
            val cards = (0..<playerNumber * cardNumber).map { mockk<Card>() }
            val deck = mockk<Deck> { every { pick() } returnsMany cards }
            val game = Game(playerNumber, deck)

            it("should initialize field for $playerNumber") {
                assertThat(game.turn).isEqualTo(0)
                assertThat(game.score).isEqualTo(0)
                assertThat(game.life).isEqualTo(3)
                assertThat(game.hint).isEqualTo(7)
                cards.chunked(cardNumber).forEachIndexed { index, cards ->
                    assertThat(game.playerCard[index]).isEqualTo(cards)
                }
                verify(exactly = playerNumber * cardNumber) { deck.pick() }
            }
        }
    }
})
