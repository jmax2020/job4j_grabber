package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private final DateTimeParser dateTimeParser;
    private static final int MAXPAGE = 5;
    private static int id = 1;
    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) throws IOException {
        Connection connDesc = Jsoup.connect(link);
        Document doc = connDesc.get();
        Element row = doc.select(".vacancy-description__text").first();
        return row.text();
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();
        for (int page = 1; page <= MAXPAGE; page++) {
            Connection conn = Jsoup.connect(String.format("%s%s", link, page));
            Document doc = conn.get();
            Elements rows = doc.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title")
                        .first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String lnk = String.format("%s%s", SOURCE_LINK,
                        linkElement.attr("href"));
                String description;
                try {
                    description = retrieveDescription(lnk);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Element titleData = row.select(".vacancy-card__date").first();
                Element td = titleData.child(0);
                String vacancyData = td.attr("datetime");
                posts.add(new Post(id++, vacancyName, lnk, description, dateTimeParser.parse(vacancyData)));
            });
        }
        return posts;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse hcp = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> list = hcp.list(PAGE_LINK);
        list.forEach(System.out::println);
    }
}
