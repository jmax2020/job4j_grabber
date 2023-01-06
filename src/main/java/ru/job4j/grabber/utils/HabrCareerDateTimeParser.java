package ru.job4j.grabber.utils;

import java.time.LocalDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        String substr = parse;
        if (parse.length() >= 19) {
            substr = parse.substring(0, 19);
        } else if (parse.length() >= 10) {
            substr = String.format("%sT%s", parse.substring(0, 10), "00:00:00");
        }
        return (parse.length() < 10) ? null : LocalDateTime.parse(substr);
    }
}
