package library.entity;

import jakarta.persistence.*;
import library.helper.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "books")
public final class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String author;
    private String publisher;
    private String description;
    private Integer categoryId;
    private Integer current;

    public Book() {
    }

    public Book(Integer id, String title, String author, String publisher, String description, Integer categoryId, Integer current) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.categoryId = categoryId;
        this.current = current;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Book other && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Book[id = %d, title = %s, author = %s, publisher = %s, description = %s]",
                id, title, author, publisher, description);
    }

    // Thực hiện truy vấn để lấy tất cả tên tác giả của cuốn sách
    public String getAllAuthors() {
        String query = "SELECT a.author_name " +
                "FROM authors a " +
                "JOIN book_author ba ON a.author_id = ba.author_id " +
                "WHERE ba.book_id = ?";

        // Sử dụng StringJoiner để nối các tác giả
        StringJoiner authorNames = new StringJoiner(", ");

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);  // Sử dụng setInt thay vì setLong vì id là Integer
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                authorNames.add(rs.getString("author_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return authorNames.toString();
    }
}
