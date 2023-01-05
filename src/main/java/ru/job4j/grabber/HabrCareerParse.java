package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        Connection conn = Jsoup.connect(PAGE_LINK);
        Document doc = conn.get();
        Elements rows = doc.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = titleElement.child(0);
            String vacancyName = titleElement.text();
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            Element titleData = row.select(".vacancy-card__date").first();
            Element td = titleData.child(0);
            String vacancyData =  td.attr("datetime").substring(0, 10);
            System.out.printf("%s %s %s%n", vacancyData,  vacancyName, link);
        });
    }
}
