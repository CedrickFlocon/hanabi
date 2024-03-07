package com.cedrickflocon.hanabi.game

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CardTest {

    @Test
    fun validCard() {
        val color = mockk<Color>()
        val card = Card(3, color, 38)

        assertThat(card.value).isEqualTo(3)
        assertThat(card.color).isEqualTo(color)
        assertThat(card.index).isEqualTo(38)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun validValue(value: Int) {
        assertDoesNotThrow { Card(value, mockk(), 38) }
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 0, 6, 12])
    fun invalidValue(value: Int) {
        assertThrows<IllegalArgumentException> { Card(value, mockk(), 38) }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5, 48, 49])
    fun validIndex(index: Int) {
        assertDoesNotThrow { Card(3, mockk(), index) }
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 50])
    fun invalidIndex(index: Int) {
        assertThrows<IllegalArgumentException> { Card(3, mockk(), index) }
    }
}
