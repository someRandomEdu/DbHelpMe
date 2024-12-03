package library.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        String query = "SELECT userFullName FROM accounts WHERE username like ?";

       DatabaseHelper.connectToDatabase();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "admin%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String userName = rs.getString("userFullName");
                System.out.println(userName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
