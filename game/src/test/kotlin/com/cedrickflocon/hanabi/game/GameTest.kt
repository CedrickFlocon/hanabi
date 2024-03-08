package com.cedrickflocon.hanabi.game

import com.google.common.truth.Truth.assertThat
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.assertThrows

class GameTest : DescribeSpec({

    this.isolationMode = IsolationMode.InstancePerLeaf

    listOf(-1, 0, 1, 6).forEach {
        describe("invalid player number $it") {
            it("it should throw an exception") {
                assertThrows<IllegalArgumentException> { Game(it, mockk()) }
            }
        }
    }

    listOf(2 to 5, 3 to 5, 4 to 4, 5 to 4).forEach { (playerNumber, cardNumber) ->
        describe("valid player number $playerNumber") {
            val cards = (0..<50).map { mockk<Card>() }
            val deck = mockk<Deck> {
                every { pick() } returnsMany cards
                every { this@mockk.cardNumber } returns 30
            }
            val game = Game(playerNumber, deck)

            it("should initialize state") {
                assertThat(game.turn).isEqualTo(0)
                assertThat(game.playerTurn).isEqualTo(0)
                assertThat(game.score).isEqualTo(0)
                assertThat(game.life).isEqualTo(3)
                assertThat(game.hintNumber).isEqualTo(8)

                assertThat(game.playerCard).containsExactlyEntriesIn(cards.chunked(cardNumber).take(playerNumber)
                    .withIndex().associate { it.index to it.value.toMutableList() })

                assertThat(game.board.keys).containsExactly(*Color.entries.toTypedArray())
                assertThat(game.board.all { it.value.isEmpty() }).isTrue()

                assertThat(game.hintList).isEmpty()
                assertThat(game.discard).isEmpty()

                verify(exactly = playerNumber * cardNumber) { deck.pick() }
            }

            describe("discard") {
                describe("invalid card") {
                    it("should throw") {
                        assertThrows<IllegalArgumentException> { game.play(Action.Discard(cards[6])) }
                    }
                }

                describe("valid card") {
                    val endGameScore = game.play(Action.Discard(cards[2]))

                    it("should discard the card") {
                        assertThat(endGameScore).isNull()

                        assertThat(game.turn).isEqualTo(1)
                        assertThat(game.playerTurn).isEqualTo(1)
                        assertThat(game.score).isEqualTo(0)
                        assertThat(game.life).isEqualTo(3)
                        assertThat(game.hintNumber).isEqualTo(8)

                        assertThat(game.playerCard[0]).doesNotContain(cards[2])
                        assertThat(game.playerCard[0]).contains(cards[playerNumber * cardNumber])
                        game.playerCard.forEach {
                            assertThat(it.value).hasSize(cardNumber)
                        }

                        assertThat(game.board.keys).containsExactly(*Color.entries.toTypedArray())
                        assertThat(game.board.all { it.value.isEmpty() }).isTrue()

                        assertThat(game.hintList).isEmpty()
                        assertThat(game.discard).containsExactly(cards[2])

                        verify(exactly = playerNumber * cardNumber + 1) { deck.pick() }
                    }
                }

                describe("regain hint") {
                    game.playerCard[1]!!.forEachIndexed { index, card ->
                        every { card.color } returns mockk()
                    }
                    game.play(Action.HintColor(1, mockk(), emptyList()))

                    it("should have less hint") {
                        assertThat(game.hintNumber).isEqualTo(7)
                    }

                    describe("discard") {
                        game.play(Action.Discard(game.playerCard[game.playerTurn]!!.first()))
                        it("should have more hint") {
                            assertThat(game.hintNumber).isEqualTo(8)
                        }
                    }
                }
            }

            describe("play") {
                describe("invalid card") {
                    every { cards[2].color } returns Color.entries.random()
                    every { cards[2].value } returns 2
                    val endGameScore = game.play(Action.Play(cards[2]))

                    it("should discard the card") {
                        assertThat(endGameScore).isNull()

                        assertThat(game.turn).isEqualTo(1)
                        assertThat(game.playerTurn).isEqualTo(1)
                        assertThat(game.score).isEqualTo(0)
                        assertThat(game.life).isEqualTo(2)
                        assertThat(game.hintNumber).isEqualTo(8)

                        assertThat(game.playerCard[0]).doesNotContain(cards[2])
                        assertThat(game.playerCard[0]).contains(cards[playerNumber * cardNumber])

                        assertThat(game.board.keys).containsExactly(*Color.entries.toTypedArray())
                        assertThat(game.board.all { it.value.isEmpty() }).isTrue()

                        assertThat(game.hintList).isEmpty()
                        assertThat(game.discard).containsExactly(cards[2])

                        verify(exactly = playerNumber * cardNumber + 1) { deck.pick() }
                    }
                }

                describe("cant regain hint") {
                    game.playerCard[1]!!.forEachIndexed { index, card ->
                        every { card.color } returns mockk()
                    }
                    game.play(Action.HintColor(1, mockk(), emptyList()))

                    it("should have less hint") {
                        assertThat(game.hintNumber).isEqualTo(7)
                    }

                    describe("invalid card") {
                        game.playerCard[1]!![0].let {
                            every { it.color } returns Color.entries.random()
                            every { it.value } returns 2
                            game.play(Action.Play(it))
                        }

                        it("should only discard the card") {
                            assertThat(game.hintNumber).isEqualTo(7)
                        }
                    }
                }

                describe("valid card") {
                    every { cards[2].color } returns Color.entries.random()
                    every { cards[2].value } returns 1
                    val endGameScore = game.play(Action.Play(cards[2]))

                    it("should add card to the board") {
                        assertThat(endGameScore).isNull()

                        assertThat(game.turn).isEqualTo(1)
                        assertThat(game.playerTurn).isEqualTo(1)
                        assertThat(game.score).isEqualTo(1)
                        assertThat(game.life).isEqualTo(3)
                        assertThat(game.hintNumber).isEqualTo(8)

                        assertThat(game.playerCard[0]).doesNotContain(cards[2])
                        assertThat(game.playerCard[0]).contains(cards[playerNumber * cardNumber])

                        assertThat(game.board.keys).containsExactly(*Color.entries.toTypedArray())
                        assertThat(game.board[cards[2].color]).containsExactly(cards[2])
                        assertThat(game.board.filter { it.key != cards[2].color }.all { it.value.isEmpty() }).isTrue()

                        assertThat(game.hintList).isEmpty()
                        assertThat(game.discard).isEmpty()

                        verify(exactly = playerNumber * cardNumber + 1) { deck.pick() }
                    }
                }
            }

            describe("hint") {
                describe("invalid player") {
                    listOf(-1, 0, playerNumber).forEach {
                        val action = mockk<Action.Hint> { every { player } returns it }
                        it("should throw when player $it") {
                            assertThrows<IllegalArgumentException> { game.play(action) }
                        }
                    }
                }

                describe("no more hint") {
                    cards.forEach { every { it.color } returns mockk() }
                    repeat(8) {
                        game.play(Action.HintColor((it + 1) % playerNumber, mockk(), emptyList()))
                    }

                    describe("hint") {
                        it("should throw") {
                            assertThrows<IllegalArgumentException> { game.play(mockk<Action.Hint>()) }
                        }
                    }
                }

                describe("color") {
                    describe("invalid") {
                        describe("not exhaustive") {
                            val color = mockk<Color>()
                            game.playerCard[1]!!.forEachIndexed { index, value ->
                                every { value.color } returns color
                                every { value.index } returns index
                            }
                            val action = Action.HintColor(1, color, listOf(0, 1))

                            it("should throw") {
                                assertThrows<IllegalArgumentException> { game.play(action) }
                            }
                        }

                        describe("not in the hand") {
                            game.playerCard[1]!!.forEach {
                                every { it.color } returns mockk()
                            }

                            val action = Action.HintColor(1, mockk(), listOf(0, 1))

                            it("should throw") {
                                assertThrows<IllegalArgumentException> { game.play(action) }
                            }
                        }
                    }

                    describe("valid hint") {
                        describe("none") {
                            game.playerCard[1]!!.forEach {
                                every { it.color } returns mockk()
                            }

                            val action = Action.HintColor(1, mockk(), emptyList())
                            val endGameScore = game.play(action)

                            it("should update hint info") {
                                assertThat(endGameScore).isNull()

                                assertThat(game.turn).isEqualTo(1)
                                assertThat(game.playerTurn).isEqualTo(1)
                                assertThat(game.score).isEqualTo(0)
                                assertThat(game.life).isEqualTo(3)
                                assertThat(game.hintNumber).isEqualTo(7)

                                assertThat(game.playerCard).containsExactlyEntriesIn(cards.chunked(cardNumber)
                                    .take(playerNumber).withIndex().associate { it.index to it.value.toMutableList() })

                                assertThat(game.board.keys).containsExactly(*Color.entries.toTypedArray())
                                assertThat(game.board.all { it.value.isEmpty() }).isTrue()

                                assertThat(game.hintList).containsExactly(action to 0)
                                assertThat(game.discard).isEmpty()

                                verify(exactly = playerNumber * cardNumber) { deck.pick() }
                            }
                        }

                        describe("some") {
                            val color = mockk<Color>()
                            game.playerCard[1]!!.forEachIndexed { index, card ->
                                every { card.color } returns color
                                every { card.index } returns index + 1
                            }

                            val action = Action.HintColor(1, color, game.playerCard[1]!!.map { it.index })
                            val endGameScore = game.play(action)

                            it("should update hint info") {
                                assertThat(endGameScore).isNull()

                                assertThat(game.turn).isEqualTo(1)
                                assertThat(game.playerTurn).isEqualTo(1)
                                assertThat(game.score).isEqualTo(0)
                                assertThat(game.life).isEqualTo(3)
                                assertThat(game.hintNumber).isEqualTo(7)

                                assertThat(game.playerCard).containsExactlyEntriesIn(cards.chunked(cardNumber)
                                    .take(playerNumber).withIndex().associate { it.index to it.value.toMutableList() })

                                assertThat(game.board.keys).containsExactly(*Color.entries.toTypedArray())
                                assertThat(game.board.all { it.value.isEmpty() }).isTrue()

                                assertThat(game.hintList).containsExactly(action to 0)
                                assertThat(game.discard).isEmpty()

                                verify(exactly = playerNumber * cardNumber) { deck.pick() }
                            }
                        }
                    }
                }

                describe("value") {
                    describe("invalid") {
                        describe("not exhaustive") {
                            game.playerCard[1]!!.forEachIndexed { index, value ->
                                every { value.value } returns 3
                                every { value.index } returns index
                            }
                            val action = Action.HintValue(1, 3, listOf(0, 1))

                            it("should throw") {
                                assertThrows<IllegalArgumentException> { game.play(action) }
                            }
                        }

                        describe("not in the hand") {
                            game.playerCard[1]!!.forEach {
                                every { it.value } returns 3
                            }

                            val action = Action.HintValue(1, 2, listOf(0, 1))

                            it("should throw") {
                                assertThrows<IllegalArgumentException> { game.play(action) }
                            }
                        }
                    }

                    describe("valid") {
                        describe("none") {
                            game.playerCard[1]!!.forEach {
                                every { it.value } returns 1
                            }

                            val action = Action.HintValue(1, 2, emptyList())
                            val endGameScore = game.play(action)

                            it("should update hint info") {
                                assertThat(endGameScore).isNull()

                                assertThat(game.turn).isEqualTo(1)
                                assertThat(game.playerTurn).isEqualTo(1)
                                assertThat(game.score).isEqualTo(0)
                                assertThat(game.life).isEqualTo(3)
                                assertThat(game.hintNumber).isEqualTo(7)

                                assertThat(game.playerCard).containsExactlyEntriesIn(cards.chunked(cardNumber)
                                    .take(playerNumber).withIndex().associate { it.index to it.value.toMutableList() })

                                assertThat(game.board.keys).containsExactly(*Color.entries.toTypedArray())
                                assertThat(game.board.all { it.value.isEmpty() }).isTrue()

                                assertThat(game.hintList).containsExactly(action to 0)
                                assertThat(game.discard).isEmpty()

                                verify(exactly = playerNumber * cardNumber) { deck.pick() }
                            }
                        }

                        describe("some") {
                            game.playerCard[1]!!.forEachIndexed { index, card ->
                                every { card.value } returns 2
                                every { card.index } returns index + 1
                            }
                            val action = Action.HintValue(1, 2, game.playerCard[1]!!.map { it.index })
                            val endGameScore = game.play(action)

                            it("should update hint info") {
                                assertThat(endGameScore).isNull()

                                assertThat(game.turn).isEqualTo(1)
                                assertThat(game.playerTurn).isEqualTo(1)
                                assertThat(game.score).isEqualTo(0)
                                assertThat(game.life).isEqualTo(3)
                                assertThat(game.hintNumber).isEqualTo(7)

                                assertThat(game.playerCard).containsExactlyEntriesIn(cards.chunked(cardNumber)
                                    .take(playerNumber).withIndex().associate { it.index to it.value.toMutableList() })

                                assertThat(game.board.keys).containsExactly(*Color.entries.toTypedArray())
                                assertThat(game.board.all { it.value.isEmpty() }).isTrue()

                                assertThat(game.hintList).containsExactly(action to 0)
                                assertThat(game.discard).isEmpty()

                                verify(exactly = playerNumber * cardNumber) { deck.pick() }
                            }
                        }
                    }
                }
            }

            describe("end game") {
                every { deck.cardNumber } returns 0
                every { cards[2].color } returns Color.entries.random()
                every { cards[2].value } returns 1
                game.play(Action.Play(cards[2]))

                var endGame: Int?
                do {
                    endGame = game.play(Action.Discard(game.playerCard[game.playerTurn]!!.first()))
                } while (endGame == null)

                it("should finish the game") {
                    assertThat(endGame).isEqualTo(1)

                    assertThat(game.turn).isEqualTo(playerNumber)
                    assertThat(game.score).isEqualTo(1)
                    assertThat(game.life).isEqualTo(3)
                    assertThat(game.hintNumber).isEqualTo(8)

                    assertThat(game.playerCard[game.playerTurn]!!).hasSize(cardNumber - 2)
                    game.playerCard.filter { it.key != game.playerTurn }.forEach {
                        assertThat(it.value).hasSize(cardNumber - 1)
                    }
                }

                it("should throw when play again") {
                    assertThrows<IllegalStateException> { game.play(mockk()) }
                }
            }
        }
    }
})
