package com.poc.carddelivery.infrastructure.file;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FixedLengthDeliveryFileWriterTest {

    @Test
    void 길이보다_짧으면_공백_패딩() {
        assertThat(FixedLengthDeliveryFileWriter.padRight("NEW", 5)).isEqualTo("NEW  ");
    }

    @Test
    void 길이보다_길면_잘라낸다() {
        assertThat(FixedLengthDeliveryFileWriter.padRight("ABCDEFG", 3)).isEqualTo("ABC");
    }

    @Test
    void null은_전부_공백() {
        assertThat(FixedLengthDeliveryFileWriter.padRight(null, 4)).isEqualTo("    ");
    }
}
