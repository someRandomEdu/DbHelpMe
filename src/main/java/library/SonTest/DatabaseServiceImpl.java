package library.SonTest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImpl implements DatabaseService {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int countDistinctRentedBooks(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT book_id) FROM rent_data WHERE account_id = ?",
                Integer.class,
                userId
        );
    }

    @Override
    public int countDistinctReturnedBooks(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT book_id) FROM return_data WHERE account_id = ?",
                Integer.class,
                userId
        );
    }

    @Override
    public int countWishlistBooks(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM wishlist WHERE user_id = ?",
                Integer.class,
                userId
        );
    }

    @Override
    public int countOntimeRentedBooks(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM rent_data WHERE account_id = ? AND borrow_to >= CURRENT_DATE",
                Integer.class,
                userId
        );
    }

    @Override
    public int countOverdueRentedBooks(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM rent_data WHERE account_id = ? AND borrow_to < CURRENT_DATE",
                Integer.class,
                userId
        );
    }

    @Override
    public int countUnreadNotifications(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND status = 'unread'",
                Integer.class,
                userId
        );
    }

    @Override
    public int countReadNotifications(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND status = 'read'",
                Integer.class,
                userId
        );
    }

    @Override
    public int countTotalFeedbacks(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM feedbacks WHERE user_id = ?",
                Integer.class,
                userId
        );
    }

    @Override
    public int countHandledFeedbacks(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM feedbacks WHERE user_id = ? AND status = 'Handled'",
                Integer.class,
                userId
        );
    }

    @Override
    public int countPendingFeedbacks(int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM feedbacks WHERE user_id = ? AND status = 'Pending'",
                Integer.class,
                userId
        );
    }
}

// Interface tương ứng
interface DatabaseService {
    int countDistinctRentedBooks(int userId);
    int countDistinctReturnedBooks(int userId);
    int countWishlistBooks(int userId);
    int countOntimeRentedBooks(int userId);
    int countOverdueRentedBooks(int userId);
    int countUnreadNotifications(int userId);
    int countReadNotifications(int userId);
    int countTotalFeedbacks(int userId);
    int countHandledFeedbacks(int userId);
    int countPendingFeedbacks(int userId);
}