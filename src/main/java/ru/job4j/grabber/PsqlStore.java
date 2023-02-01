package ru.job4j.grabber;

import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            this.cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cnn.prepareStatement(
                String.format("insert into post(name, text, link, created) %s %s",
                        "values (?, ?, ?, ?) on conflict (link)",
                        "do nothing"), Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                post.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Post getPost(ResultSet rs) throws SQLException {
        return new Post(rs.getInt("id"),
                rs.getString("name"),
                rs.getString("link"),
                rs.getString("text"),
                rs.getTimestamp("created").toLocalDateTime());
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (Statement st = cnn.createStatement()) {
            ResultSet res = st.executeQuery("select * from post");
            while (res.next()) {
               posts.add(getPost(res));
           }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post rsl = null;
        try (PreparedStatement ps = cnn.prepareStatement(
                "select * from post where id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rsl = getPost(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rsl;
    }

        @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        Properties cfg = new Properties();
        List<Post> list;
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("PsqlStore.properties")) {
            cfg.load(in);
            try (PsqlStore psql = new PsqlStore(cfg)) {
            HabrCareerParse hcp = new HabrCareerParse(new HabrCareerDateTimeParser());
            list = hcp.list("https://career.habr.com/vacancies/java_developer?page=");
            for (Post post : list) {
                psql.save(post);
            }
            Post p = psql.findById(2);
            System.out.println(p);
            System.out.println("**************************************************************");
            list = psql.getAll();
            list.stream().forEach(System.out::println);
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}