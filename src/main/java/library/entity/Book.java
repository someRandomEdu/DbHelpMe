package library.entity;

import jakarta.persistence.*;
import library.helper.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "books")
public final class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    private String author;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "cover")
    private String cover;

    @Column(name = "read_link")
    private String readLink;

    public Book() {

    }

    public Book(Integer id, String title, String author, String publisher, String description, Integer quantity, String cover, String readLink) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.quantity = quantity;
        this.cover = cover;
        this.readLink = readLink;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getReadLink() {
        return readLink;
    }

    public void setReadLink(String readLink) {
        this.readLink = readLink;
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

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                authorNames.add(rs.getString("author_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return authorNames.toString();
    }

    public static List<String> getCategoriesList() {
        List<String> categories = new ArrayList<>();

        String query = "SELECT name FROM categories";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

    public String getCategoriesString() {
        String query = "SELECT c.name " +
                "FROM categories c " +
                "JOIN book_category bc ON c.id = bc.category_id " +
                "WHERE bc.book_id = ?";

        StringJoiner categoriesName = new StringJoiner(", ");

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categoriesName.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoriesName.toString();
    }
}
