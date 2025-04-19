package cnpm.edu.DAO;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cnpm.edu.config.DbConfig;
import cnpm.edu.model.Role;

public class RoleDAO {

    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            String query = "SELECT * FROM Role";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nameRole = rs.getString("nameRole");
                String description = rs.getString("description");
                LocalDateTime createDate = null;

                try {
                    java.sql.Date date = rs.getDate("createDate");
                    if (date != null) {
                        createDate = date.toLocalDate().atStartOfDay();
                    } else {
                        createDate = LocalDateTime.now();
                    }
                } catch (SQLException e) {
                    createDate = LocalDateTime.now();
                }

                roles.add(new Role(id, nameRole, description, createDate));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách vai trò: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbConfig.closeConnection(connection);
        }

        return roles;
    }

    public int addRole(String name, String description) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet columns = meta.getColumns(null, null, "role", "createDate");
            boolean hasCreateDateColumn = columns.next();
            columns.close();

            String query = hasCreateDateColumn ? "INSERT INTO role (nameRole, description, createDate) VALUES (?, ?, ?)"
                    : "INSERT INTO role (nameRole, description) VALUES (?, ?)";

            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setString(2, description);

            if (hasCreateDateColumn) {
                stmt.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.now()));
            }

            int result = stmt.executeUpdate();

            // Lấy ID được tạo ra
            int newRoleId = -1;
            if (result > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newRoleId = generatedKeys.getInt(1);
                }
                generatedKeys.close();
            }
            stmt.close();
            return newRoleId;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm vai trò: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            DbConfig.closeConnection(connection);
        }
    }

    public boolean updateRole(int roleId, String roleName, String description) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            String query = "UPDATE role SET nameRole = ?, description = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, roleName);
            stmt.setString(2, description);
            stmt.setInt(3, roleId);

            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật vai trò: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbConfig.closeConnection(connection);
        }
    }

    public boolean deleteRole(int roleId) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            String query = "DELETE FROM role WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, roleId);

            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa vai trò: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbConfig.closeConnection(connection);
        }
    }
}