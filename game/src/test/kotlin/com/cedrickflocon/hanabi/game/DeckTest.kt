package com.cedrickflocon.hanabi.game

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeckTest {

    @Nested
    inner class `Describe a deck` {
        lateinit var deck: Deck

        @BeforeEach
        fun setUp() {
            deck = Deck(1)
        }

        @Test
        fun `it should have 50 card in the deck`() {
            assertThat(deck.cardNumber).isEqualTo(50)
            with(deck.allCard()) {
                assertThat(this).hasSize(50)
                assertThat(this).isEqualTo(this.sortedBy { it.index })
                Color.entries
                    .map { color -> this.filter { it.color == color } }
                    .forEach { allByColor ->
                        assertThat(allByColor).hasSize(10)
                        assertThat(allByColor.count { it.value == 1 }).isEqualTo(3)
                        assertThat(allByColor.count { it.value == 2 }).isEqualTo(2)
                        assertThat(allByColor.count { it.value == 3 }).isEqualTo(2)
                        assertThat(allByColor.count { it.value == 4 }).isEqualTo(2)
                        assertThat(allByColor.count { it.value == 5 }).isEqualTo(1)
                    }
            }
            assertThrows<NoSuchElementException> { deck.pick() }
        }

        @Nested
        inner class `Describe a deck with same seed` {
            lateinit var deck2: Deck

            @BeforeEach
            fun setUp() {
                deck2 = Deck(1)
            }

            @Test
            fun `it should have the same card order`() {
                assertThat(deck.allCard()).isEqualTo(deck2.allCard())
            }
        }

        @Nested
        inner class `Describe a deck with another seed` {
            lateinit var deck2: Deck

            @BeforeEach
            fun setUp() {
                deck2 = Deck(3)
            }

            @Test
            fun `it should have the same card order`() {
                assertThat(deck.allCard()).isNotEqualTo(deck2.allCard())
            }
        }
    }

    private fun Deck.allCard(): List<Card> {
        return (0 until 50).map { this.pick() }
    }
}
