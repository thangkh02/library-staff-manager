package cnpm.edu.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/librarymgr?useUnicode=true&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "21092004";

    public static Connection getConnection() throws SQLException {
        try {
            // Tải driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        }

        String connectionUrl = URL +
                "&allowPublicKeyRetrieval=true" +
                "&useSSL=false" +
                "&serverTimezone=UTC";
        Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
        return connection;
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
        }
    }

    // Phương thức kiểm tra kết nối
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection(conn);
        }
    }

    public static boolean testTableAccess(String tableName) {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement("DESCRIBE " + tableName);
            ResultSet rs = stmt.executeQuery();
            boolean hasRows = rs.next();
            rs.close();
            stmt.close();
            return hasRows;
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra bảng " + tableName + ": " + e.getMessage());
            return false;
        } finally {
            closeConnection(connection);
        }
    }
}