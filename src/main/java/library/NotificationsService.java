package library;

import io.swagger.v3.oas.annotations.servers.Server;
import library.helper.DatabaseHelper;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class NotificationsService {
    public void saveNotification(int userId, String type, String message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseHelper.getConnection();

            String sql = "INSERT INTO notifications (user_id, type, message, status) VALUES (?, ?, ?, 'unread')";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId); // Gán userId
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, message); // Gán message

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Notification saved successfully.");
            } else {
                System.out.println("Failed to save notification.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
