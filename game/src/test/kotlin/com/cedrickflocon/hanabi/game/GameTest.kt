package com.cedrickflocon.hanabi.game

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GameTest {

    @Nested
    inner class `Describe valid game` {
        lateinit var game: Game

        @BeforeEach
        fun setUp() {
            game = Game()
        }

        @Test
        fun `it should initialize field`() {
            assertThat(game.turn).isEqualTo(0)
            assertThat(game.score).isEqualTo(0)
            assertThat(game.life).isEqualTo(3)
            assertThat(game.hint).isEqualTo(7)
        }
    }
}
