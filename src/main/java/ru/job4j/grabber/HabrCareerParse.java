package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private static String retrieveDescription(String link) throws IOException {
        Connection connDesc = Jsoup.connect(link);
        Document doc = connDesc.get();
        Element row = doc.select(".vacancy-description__text").first();
        return row.text();
    }

    public static void main(String[] args) throws IOException {
        for (int page = 1; page <= 1; page++) {
            Connection conn = Jsoup.connect(String.format("%s%s", PAGE_LINK, page));
            Document doc = conn.get();
            Elements rows = doc.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title")
                        .first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK
                        , linkElement.attr("href"));
                String description;
                try {
                    description = retrieveDescription(link);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Element titleData = row.select(".vacancy-card__date").first();
                Element td = titleData.child(0);
                String vacancyData = td.attr("datetime");
                System.out.printf("%s %s %s %s%n", vacancyData, vacancyName, link, description);
            });
        }
    }
}
