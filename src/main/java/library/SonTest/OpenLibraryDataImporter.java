package library.SonTest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import library.helper.DatabaseHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class OpenLibraryDataImporter {
    private static final int MAX_DESCRIPTION_LENGTH = 255;
    private static final String TRENDING_URL = "https://openlibrary.org/trending.json";

    public static void main(String[] args) {
        importTrendingBooks();
    }

    private static void importTrendingBooks() {
        Set<String> trendingWorkIds = fetchTrendingBookWorkIds();

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);

            for (String workId : trendingWorkIds) {
                try {
                    // Thay đổi để lấy toàn bộ dữ liệu sách từ JSON trending
                    JSONObject bookData = fetchBookData(workId);
                    if (bookData == null) continue;

                    // Thêm log để kiểm tra dữ liệu sách
                    System.out.println("Dữ liệu sách đang xử lý: " + bookData);

                    int bookId = insertBook(conn, bookData, workId);
                    if (bookId == -1) {
                        continue;
                    }

                    // Truyền toàn bộ bookData
                    insertBookAuthors(conn, bookId, bookData);
                    insertBookCategories(conn, bookId, bookData);
                } catch (Exception e) {
                    System.err.println("Lỗi khi nhập sách " + workId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> fetchTrendingBookWorkIds() {
        Set<String> workIds = new HashSet<>();
        try {
            URL url = new URL(TRENDING_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (connection.getResponseCode() == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray worksArray = jsonResponse.optJSONArray("works");

                if (worksArray != null) {
                    for (int i = 0; i < worksArray.length(); i++) {
                        JSONObject work = worksArray.getJSONObject(i);
                        String key = work.optString("key", "");
                        if (!key.isEmpty()) {
                            // Extract work ID from the key
                            String workId = key.replace("/works/", "");
                            workIds.add(workId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy sách trending: " + e.getMessage());
        }

        System.out.println("Tìm thấy " + workIds.size() + " sách trending");
        return workIds;
    }
    private static JSONObject fetchBookData(String workId) {
        try {
            URL url = new URL("https://openlibrary.org/works/" + workId + ".json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                return new JSONObject(response.toString());
            }
        } catch (IOException e) {
            System.out.println("Không thể fetch dữ liệu cho " + workId);
        }
        return null;
    }

    private static int insertBook(Connection conn, JSONObject bookData, String workId) throws SQLException {
        // Kiểm tra xem sách đã tồn tại chưa
        String title = bookData.optString("title", "Không có tiêu đề");
        String checkExistingSql = "SELECT id FROM books WHERE title = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkExistingSql)) {
            checkStmt.setString(1, title);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Nếu sách đã tồn tại, trả về ID của sách đó
                    System.out.println("Sách đã tồn tại: " + title);
                    return rs.getInt("id");
                }
            }
        }

        // Tiếp tục logic chèn sách như cũ nếu sách chưa tồn tại
        Random random = new Random();
        int quantity = random.nextInt(5) + 1;

        // Xử lý description
        String description = "";
        Object descriptionObj = bookData.opt("description");
        if (descriptionObj instanceof JSONObject) {
            description = ((JSONObject) descriptionObj).optString("value", "");
        } else if (descriptionObj instanceof String) {
            description = (String) descriptionObj;
        }

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            description = description.substring(0, MAX_DESCRIPTION_LENGTH);
        }

        // Xử lý cover
        String coverUrl = "";
        try {
            JSONObject covers = fetchBookCovers(workId);
            if (covers != null && covers.has("covers") && !covers.getJSONArray("covers").isEmpty()) {
                int coverId = covers.getJSONArray("covers").getInt(0);
                coverUrl = "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy cover: " + e.getMessage());
        }

        // Tạo read_link
        String readLink = "https://openlibrary.org/works/" + workId;

        String insertSql = "INSERT INTO books (title, description, quantity, cover, read_link) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setInt(3, quantity);
            pstmt.setString(4, coverUrl);
            pstmt.setString(5, readLink);

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    System.out.println("Đã chèn sách mới: " + title);
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi chèn sách: " + e.getMessage());
            System.err.println("Tiêu đề: " + title);
            System.err.println("Mô tả (đã cắt): " + description);
            System.err.println("Cover URL: " + coverUrl);
            System.err.println("Read Link: " + readLink);
            throw e;
        }
        return -1;
    }

    private static JSONObject fetchBookCovers(String workId) {
        try {
            URL url = new URL("https://openlibrary.org/works/" + workId + ".json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                JSONObject bookData = new JSONObject(response.toString());
                return bookData;
            }
        } catch (IOException e) {
            System.out.println("Không thể fetch cover cho " + workId);
        }
        return null;
    }

    private static void insertBookAuthors(Connection conn, int bookId, JSONObject bookData) throws SQLException {
        JSONArray authorsArray = bookData.optJSONArray("authors");
        if (authorsArray == null || authorsArray.isEmpty()) {
            System.out.println("Không tìm thấy tác giả trong dữ liệu sách");
            return;
        }

        for (int i = 0; i < authorsArray.length(); i++) {
            JSONObject authorObj = authorsArray.getJSONObject(i);
            JSONObject authorDetails = authorObj.optJSONObject("author");

            if (authorDetails == null) {
                System.out.println("Không thể lấy thông tin chi tiết tác giả");
                continue;
            }

            // Lấy key của tác giả để fetch thông tin chi tiết
            String authorKey = authorDetails.optString("key", "");
            if (authorKey.isEmpty()) {
                System.out.println("Không có key cho tác giả");
                continue;
            }

            // Fetch thông tin chi tiết tác giả
            JSONObject fullAuthorDetails = fetchAuthorDetails(authorKey);
            if (fullAuthorDetails == null) {
                System.out.println("Không thể fetch thông tin chi tiết tác giả: " + authorKey);
                continue;
            }

            // Lấy tên tác giả
            String authorName = fullAuthorDetails.optString("name", "Không rõ tên");
            System.out.println("Đang xử lý tác giả: " + authorName);

            try {
                // Chèn tác giả
                String insertAuthorSql = "INSERT IGNORE INTO authors (author_name) VALUES (?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertAuthorSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, authorName);
                    int affectedRows = pstmt.executeUpdate();
                    System.out.println("Số dòng ảnh hưởng khi chèn tác giả: " + affectedRows);

                    // Lấy ID tác giả
                    int authorId = -1;
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            authorId = generatedKeys.getInt(1);
                            System.out.println("ID tác giả được sinh: " + authorId);
                        }
                    }

                    // Nếu không lấy được ID từ khóa tự sinh, truy vấn lại
                    if (authorId == -1) {
                        String selectAuthorSql = "SELECT author_id FROM authors WHERE author_name = ?";
                        try (PreparedStatement selectStmt = conn.prepareStatement(selectAuthorSql)) {
                            selectStmt.setString(1, authorName);
                            try (ResultSet rs = selectStmt.executeQuery()) {
                                if (rs.next()) {
                                    authorId = rs.getInt("author_id");
                                    System.out.println("ID tác giả đã tồn tại: " + authorId);
                                }
                            }
                        }
                    }

                    // Chèn liên kết sách-tác giả
                    if (authorId != -1) {
                        String insertBookAuthorSql = "INSERT IGNORE INTO book_author (book_id, author_id) VALUES (?, ?)";
                        try (PreparedStatement bookAuthorStmt = conn.prepareStatement(insertBookAuthorSql)) {
                            bookAuthorStmt.setInt(1, bookId);
                            bookAuthorStmt.setInt(2, authorId);
                            int bookAuthorAffectedRows = bookAuthorStmt.executeUpdate();
                            System.out.println("Số dòng ảnh hưởng khi chèn liên kết sách-tác giả: " + bookAuthorAffectedRows);
                        }
                    } else {
                        System.err.println("Không thể lấy ID cho tác giả: " + authorName);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Lỗi khi xử lý tác giả " + authorName + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static JSONObject fetchAuthorDetails(String authorKey) {
        try {
            URL url = new URL("https://openlibrary.org" + authorKey + ".json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (connection.getResponseCode() == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                return new JSONObject(response.toString());
            }
        } catch (IOException e) {
            System.out.println("Không thể fetch thông tin tác giả " + authorKey + ": " + e.getMessage());
        }
        return null;
    }

    private static int getOrCreateAuthor(Connection conn, String authorName) throws SQLException {
        // Kiểm tra xem tác giả đã tồn tại chưa
        String checkSql = "SELECT author_id FROM authors WHERE author_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, authorName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("author_id");
                }
            }
        }

        // Nếu chưa tồn tại thì tạo mới
        String insertSql = "INSERT INTO authors (author_name) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, authorName);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    private static void insertBookAuthorLink(Connection conn, int bookId, int authorId) throws SQLException {
        String insertSql = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, authorId);
            pstmt.executeUpdate();
        }
    }

    private static void insertBookCategories(Connection conn, int bookId, JSONObject bookData) throws SQLException {
        // Mapping of subcategories to main categories
        Map<String, Set<String>> categoryMapping = new HashMap<>() {{
            // Arts
            put("Arts", Set.of(
                    "architecture", "art instruction", "art history", "dance", "design",
                    "fashion", "film", "graphic design", "music", "music theory",
                    "painting", "photography"
            ));

            // Animals
            put("Animals", Set.of(
                    "bears", "cats", "kittens", "dogs", "puppies"
            ));

            // Fiction
            put("Fiction", Set.of(
                    "fantasy", "historical fiction", "horror", "humor", "literature",
                    "magic", "mystery and detective stories", "plays", "poetry",
                    "romance", "science fiction", "short stories", "thriller", "young adult"
            ));

            // Science & Mathematics
            put("Science & Mathematics", Set.of(
                    "biology", "chemistry", "mathematics", "physics", "programming"
            ));

            // Business & Finance
            put("Business & Finance", Set.of(
                    "management", "entrepreneurship", "business economics",
                    "business success", "finance"
            ));

            // Children's
            put("Children's", Set.of(
                    "kids books", "stories in rhyme", "baby books",
                    "bedtime books", "picture books"
            ));

            // History
            put("History", Set.of(
                    "ancient civilization", "archaeology", "anthropology",
                    "world war ii", "social life and customs"
            ));

            // Health & Wellness
            put("Health & Wellness", Set.of(
                    "cooking", "cookbooks", "mental health", "exercise",
                    "nutrition", "self-help"
            ));

            // Biography
            put("Biography", Set.of(
                    "autobiographies", "history", "politics and government",
                    "world war ii", "women", "kings and rulers",
                    "composers", "artists"
            ));

            // Social Sciences
            put("Social Sciences", Set.of(
                    "anthropology", "religion", "political science", "psychology"
            ));

            // Places (this might need special handling)
            put("Geography", Set.of(
                    "brazil", "india", "indonesia", "united states"
            ));

            // Textbooks categories
            put("Textbooks", Set.of(
                    "history", "mathematics", "geography", "psychology",
                    "algebra", "education", "business & economics",
                    "science", "chemistry", "english language",
                    "physics", "computer science"
            ));
        }};

        Set<String> matchedCategories = new HashSet<>();

        // Get categories from subjects
        JSONArray subjectsArray = bookData.optJSONArray("subjects");
        if (subjectsArray != null) {
            for (int i = 0; i < subjectsArray.length(); i++) {
                String subject = subjectsArray.getString(i).toLowerCase().trim();

                // Check against each category mapping
                for (Map.Entry<String, Set<String>> categoryEntry : categoryMapping.entrySet()) {
                    for (String subcategory : categoryEntry.getValue()) {
                        // Check for exact match or contains
                        if (subject.equals(subcategory) || subject.contains(subcategory)) {
                            matchedCategories.add(categoryEntry.getKey());
                            break;  // Stop after finding a match for this category
                        }
                    }
                }
            }
        }

        // Insert each matched category and link to book
        for (String categoryName : matchedCategories) {
            try {
                int categoryId = getOrCreateCategory(conn, categoryName);

                if (categoryId != -1) {
                    insertBookCategoryLink(conn, bookId, categoryId);
                }
            } catch (SQLException e) {
                // Skip duplicate entry errors
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
    }

    private static int getOrCreateCategory(Connection conn, String categoryName) throws SQLException {
        // Kiểm tra xem category đã tồn tại chưa
        String checkSql = "SELECT id FROM categories WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, categoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        // Nếu chưa tồn tại thì tạo mới
        String insertSql = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, categoryName);
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1;
    }

    private static void insertBookCategoryLink(Connection conn, int bookId, int categoryId) throws SQLException {
        String insertSql = "INSERT IGNORE INTO book_category (book_id, category_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, categoryId);
            pstmt.executeUpdate();
        }
    }
}