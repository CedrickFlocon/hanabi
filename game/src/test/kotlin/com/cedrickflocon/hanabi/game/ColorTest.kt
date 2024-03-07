package com.cedrickflocon.hanabi.game

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ColorTest {

    @Test
    fun colorNumber() {
        assertThat(Color.entries.size).isEqualTo(5)
    }
}
