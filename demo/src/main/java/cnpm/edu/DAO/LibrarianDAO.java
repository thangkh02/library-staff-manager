package cnpm.edu.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import cnpm.edu.config.DbConfig;
import cnpm.edu.model.Librarian;
import cnpm.edu.model.Role;

public class LibrarianDAO {

    public List<Librarian> getAllLibrarians() {
        List<Librarian> librarians = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DbConfig.getConnection();
            String query = "SELECT * FROM librarian";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            java.util.Map<Integer, Librarian> librarianMap = new java.util.HashMap<>();

            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("fullName");
                double salary = rs.getDouble("salary");
                LocalDate birthDate = rs.getDate("birthDate").toLocalDate();
                String gender = rs.getString("gender");
                String avatarUrl = rs.getString("avatarUrl");
                String email = rs.getString("email");
                String phoneNumber = rs.getString("phoneNumber");
                String address = rs.getString("address");

                Librarian librarian = new Librarian(id, fullName, salary, birthDate, gender, avatarUrl,
                        new ArrayList<>(), email, phoneNumber, address);

                librarianMap.put(id, librarian);
                librarians.add(librarian);
            }

            String roleQuery = "SELECT lr.librarian_id, r.* FROM librarian_role lr " +
                    "JOIN role r ON lr.role_id = r.id";

            PreparedStatement roleStmt = connection.prepareStatement(roleQuery);
            ResultSet roleRs = roleStmt.executeQuery();

            while (roleRs.next()) {
                int librarianId = roleRs.getInt("librarian_id");
                int roleId = roleRs.getInt("id");
                String roleName = roleRs.getString("nameRole");
                String roleDesc = roleRs.getString("description");

                Role role = new Role(roleId, roleName, roleDesc);

                Librarian librarian = librarianMap.get(librarianId);
                if (librarian != null) {
                    librarian.addRole(role);
                }
            }

            roleRs.close();
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách thủ thư: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbConfig.closeConnection(connection);
        }

        return librarians;
    }

    public void addLibrarianWithRoles(String fullName, double salary, LocalDate birthDate,
                                       String gender, String avatarUrl, List<Role> roles,
                                       String email, String phoneNumber, String address) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            connection.setAutoCommit(false);

            int nextId = getNextAvailableId();

            String insertQuery = "INSERT INTO librarian (id, fullName, salary, birthDate, gender, " +
                    "avatarUrl, email, phoneNumber, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            stmt.setInt(1, nextId);
            stmt.setString(2, fullName);
            stmt.setDouble(3, salary);
            stmt.setObject(4, java.sql.Date.valueOf(birthDate));
            stmt.setString(5, gender);
            stmt.setString(6, avatarUrl);
            stmt.setString(7, email);
            stmt.setString(8, phoneNumber);
            stmt.setString(9, address);

            stmt.executeUpdate();
            stmt.close();

            if (roles != null && !roles.isEmpty()) {
                String roleInsert = "INSERT INTO librarian_role (librarian_id, role_id) VALUES (?, ?)";
                PreparedStatement roleStmt = connection.prepareStatement(roleInsert);

                for (Role role : roles) {
                    roleStmt.setInt(1, nextId);
                    roleStmt.setInt(2, role.getId());
                    roleStmt.addBatch();
                }

                roleStmt.executeBatch();
                roleStmt.close();
            }

            connection.commit();
            //getLibrarians(); // Cần xem xét việc cập nhật cache ở đây

        } catch (SQLException e) {
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Lỗi khi thêm thủ thư với nhiều vai trò: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DbConfig.closeConnection(connection);
        }
    }

    public void updateLibrarianWithRoles(int id, String fullName, double salary, LocalDate birthDate,
                                        String gender, String avatarUrl, List<Role> roles,
                                        String email, String phoneNumber, String address) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            connection.setAutoCommit(false);

            String updateQuery = "UPDATE librarian SET fullName = ?, salary = ?, birthDate = ?, gender = ?, " +
                    "avatarUrl = ?, email = ?, phoneNumber = ?, address = ? WHERE id = ?";

            PreparedStatement stmt = connection.prepareStatement(updateQuery);
            stmt.setString(1, fullName);
            stmt.setDouble(2, salary);
            stmt.setObject(3, java.sql.Date.valueOf(birthDate));
            stmt.setString(4, gender);
            stmt.setString(5, avatarUrl);
            stmt.setString(6, email);
            stmt.setString(7, phoneNumber);
            stmt.setString(8, address);
            stmt.setInt(9, id);

            int updatedRows = stmt.executeUpdate();
            stmt.close();

            if (updatedRows == 0) {
                connection.rollback();
                return;
            }

            String deleteRolesQuery = "DELETE FROM librarian_role WHERE librarian_id = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteRolesQuery);
            deleteStmt.setInt(1, id);
            deleteStmt.executeUpdate();
            deleteStmt.close();

            if (roles != null && !roles.isEmpty()) {
                String roleInsert = "INSERT INTO librarian_role (librarian_id, role_id) VALUES (?, ?)";
                PreparedStatement roleStmt = connection.prepareStatement(roleInsert);

                for (Role role : roles) {
                    roleStmt.setInt(1, id);
                    roleStmt.setInt(2, role.getId());
                    roleStmt.addBatch();
                }

                roleStmt.executeBatch();
                roleStmt.close();
            }

            connection.commit();
            //getLibrarians(); // Cần xem xét việc cập nhật cache ở đây

        } catch (SQLException e) {
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Lỗi khi cập nhật thủ thư với nhiều vai trò: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DbConfig.closeConnection(connection);
        }
    }

    public void deleteLibrarian(int id) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            connection.setAutoCommit(false);

            String deleteRolesQuery = "DELETE FROM librarian_role WHERE librarian_id = ?";
            PreparedStatement roleStmt = connection.prepareStatement(deleteRolesQuery);
            roleStmt.setInt(1, id);
            roleStmt.executeUpdate();
            roleStmt.close();

            String deleteLibrarianQuery = "DELETE FROM librarian WHERE id = ?";
            PreparedStatement librarianStmt = connection.prepareStatement(deleteLibrarianQuery);
            librarianStmt.setInt(1, id);
            librarianStmt.executeUpdate();
            librarianStmt.close();

            connection.commit();
           

        } catch (SQLException e) {
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Lỗi khi xóa thủ thư: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DbConfig.closeConnection(connection);
        }
    }

    private int getNextAvailableId() {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            String query = "SELECT MAX(id) + 1 AS next_id FROM librarian";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int nextId = rs.getInt("next_id");
                return nextId > 0 ? nextId : 1;
            }
            return 1;

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy ID tiếp theo: " + e.getMessage());
            e.printStackTrace();
            return 1;
        } finally {
            DbConfig.closeConnection(connection);
        }
    }
    
}