package com.cedrickflocon.hanabi.game

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CardTest {

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5])
    fun validNumber(value: Int) {
        val color = mockk<Color>()
        val card = Card(value, color)
        assertThat(card.value).isEqualTo(value)
        assertThat(card.color).isEqualTo(color)
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 0, 6, 12])
    fun invalidNumber(value: Int) {
        assertThrows<IllegalArgumentException> { Card(value, mockk()) }
    }
}
