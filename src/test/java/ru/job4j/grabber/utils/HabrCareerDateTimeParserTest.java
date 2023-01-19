package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HabrCareerDateTimeParserTest {

    @Test
    void whenDataandTime() {
        String text = "2023-01-05T16:37:30+03:00";
        HabrCareerDateTimeParser hb = new HabrCareerDateTimeParser();
        LocalDateTime dat =  hb.parse(text);
        LocalDateTime expectedDat = LocalDateTime.of(2023, 1, 5, 16, 37, 30);
        assertThat(dat).isEqualTo(expectedDat);
    }
}