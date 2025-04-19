package cnpm.edu.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cnpm.edu.config.DbConfig;

public class LibrarianModel {
    private List<Librarian> librarians = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();

    public void initializeData() {
        checkTableStructure();
        getLibrarians();
        getRoles();
        getPermissions();
    }

    public List<Librarian> getLibrarians() {
        librarians.clear();
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
            roleStmt.close();
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

    public List<Role> getRoles() {
        roles.clear();
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

    public List<Permission> getPermissions() {
        permissions.clear();
        Connection connection = null;

        try {
            connection = DbConfig.getConnection();
            String query = "SELECT * FROM permission";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String namePermission;
                try {
                    namePermission = rs.getString("name_permission");
                } catch (SQLException e) {
                    try {
                        namePermission = rs.getString("namePermission");
                    } catch (SQLException e2) {
                        namePermission = "Unknown";
                    }
                }

                String description = "";
                try {
                    description = rs.getString("description");
                } catch (SQLException e) {
                }

                permissions.add(new Permission(id, namePermission, description));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách quyền: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbConfig.closeConnection(connection);
        }

        return permissions;
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
            getLibrarians();

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

            String deleteRolesQuery = "DELETE FROM Librarian_Role WHERE librarian_id = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteRolesQuery);
            deleteStmt.setInt(1, id);
            deleteStmt.executeUpdate();
            deleteStmt.close();

            if (roles != null && !roles.isEmpty()) {
                String insertRoleQuery = "INSERT INTO Librarian_Role (librarian_id, role_id) VALUES (?, ?)";
                PreparedStatement roleStmt = connection.prepareStatement(insertRoleQuery);

                for (Role role : roles) {
                    roleStmt.setInt(1, id);
                    roleStmt.setInt(2, role.getId());
                    roleStmt.addBatch();
                }

                roleStmt.executeBatch();
                roleStmt.close();
            }

            connection.commit();
            getLibrarians();

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

    public boolean removeLibrarianRole(int librarianId, int roleId) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            String query = "DELETE FROM librarian_role WHERE librarian_id = ? AND role_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, librarianId);
            stmt.setInt(2, roleId);

            int result = stmt.executeUpdate();
            stmt.close();

            if (result > 0) {
                getLibrarians();
            }

            return result > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi gỡ vai trò của thủ thư: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
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
            getLibrarians();

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

    public void checkTableStructure() {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "librarian_role", null);

            if (tables.next()) {
                ResultSet columns = metaData.getColumns(null, null, "librarian_role", null);
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    String nullable = columns.getString("IS_NULLABLE");
                    System.out.println("Cột: " + columnName + ", Kiểu: " + columnType + ", Nullable: " + nullable);
                }
                columns.close();
            }
            tables.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra cấu trúc bảng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbConfig.closeConnection(connection);
        }
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

            PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
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

            if (newRoleId > 0) {
                getRoles(); // Cập nhật danh sách vai trò
                return newRoleId;
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm vai trò: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
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

    public boolean updateRolePermissions(int roleId, List<Integer> permissionIds) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            connection.setAutoCommit(false);

            // Xóa tất cả quyền hiện tại của vai trò
            String deleteQuery = "DELETE FROM permission_role WHERE Roleid = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, roleId);
            deleteStmt.executeUpdate();
            deleteStmt.close();

            // Thêm các quyền mới
            if (permissionIds != null && !permissionIds.isEmpty()) {
                String insertQuery = "INSERT INTO permission_role (Permissionid, Roleid) VALUES (?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);

                for (Integer permissionId : permissionIds) {
                    insertStmt.setInt(1, permissionId);
                    insertStmt.setInt(2, roleId);
                    insertStmt.addBatch();
                }

                insertStmt.executeBatch();
                insertStmt.close();
            }

            connection.commit();
            System.out.println("DEBUG: Đã cập nhật " + (permissionIds != null ? permissionIds.size() : 0) +
                    " quyền cho vai trò " + roleId);
            return true;
        } catch (SQLException e) {
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Lỗi khi cập nhật quyền cho vai trò: " + e.getMessage());
            e.printStackTrace();
            return false;
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

    public List<Permission> getPermissionsForRole(Role role) {
        if (role == null)
            return new ArrayList<>();

        List<Permission> permissions = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            // Sửa lại tên bảng và tên cột đúng với schema của bạn
            String query = "SELECT DISTINCT p.* FROM permission p " +
                    "INNER JOIN permission_role pr ON p.id = pr.Permissionid " +
                    "WHERE pr.Roleid = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, role.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("DEBUG: Đang tìm quyền cho vai trò ID = " + role.getId());

            while (rs.next()) {
                int id = rs.getInt("id");
                String namePermission = rs.getString("namePermission");
                String description = rs.getString("description");

                System.out.println("DEBUG: Tìm thấy quyền " + id + ": " + namePermission);
                permissions.add(new Permission(id, namePermission, description));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách quyền của vai trò: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbConfig.closeConnection(connection);
        }
        return permissions;
    }

    public LibrarianRole getLibrarianRoleByIds(int librarianId, int roleId) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            String query = "SELECT * FROM librarian_role WHERE librarian_id = ? AND role_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, librarianId);
            stmt.setInt(2, roleId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LocalDateTime assignmentDate = null;
                java.sql.Date date = rs.getDate("assignmentDate");
                if (date != null) {
                    assignmentDate = date.toLocalDate().atStartOfDay();
                }

                Librarian librarian = librarians.stream()
                        .filter(l -> l.getId() == librarianId)
                        .findFirst()
                        .orElse(null);
                Role role = roles.stream()
                        .filter(r -> r.getId() == roleId)
                        .findFirst()
                        .orElse(null);
                if (librarian != null && role != null) {
                    // Đã sửa: không truyền tham số id (không còn tồn tại)
                    return new LibrarianRole(librarianId, roleId, assignmentDate, librarian, role);
                }
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin librarian_role: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DbConfig.closeConnection(connection);
        }
        return null;
    }

    public boolean isEmailExists(String email, int excludeId) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            String query = "SELECT COUNT(*) FROM librarian WHERE email = ? AND id != ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra email tồn tại: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbConfig.closeConnection(connection);
        }
    }

    public boolean isPhoneExists(String phone, int excludeId) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            String query = "SELECT COUNT(*) FROM librarian WHERE phoneNumber = ? AND id != ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, phone);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra số điện thoại tồn tại: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbConfig.closeConnection(connection);
        }
    }

    public boolean isLibrarianExists(String fullName, LocalDate birthDate, String gender, int excludeId) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            String query = "SELECT COUNT(*) FROM librarian WHERE fullName = ? AND birthDate = ? AND gender = ? AND id != ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, fullName);
            stmt.setDate(2, java.sql.Date.valueOf(birthDate));
            stmt.setString(3, gender);
            stmt.setInt(4, excludeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra thủ thư tồn tại: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbConfig.closeConnection(connection);
        }
    }

    public boolean assignRoleToLibrarian(Librarian librarian, Role role) {

        if (librarian.getRoles().stream().anyMatch(r -> r.getId() == role.getId())) {
            System.out.println("Thủ thư " + librarian.getFullName() + " đã có vai trò " + role.getNameRole());
            return true;
        }

        Connection connection = null;
        try {
            connection = DbConfig.getConnection();

            // Thêm ngày đảm nhiệm
            String query = "INSERT INTO librarian_role (librarian_id, role_id, assignmentDate) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, librarian.getId());
            stmt.setInt(2, role.getId());
            // Sử dụng ngày hiện tại khi thêm vào database
            java.sql.Timestamp currentTimestamp = java.sql.Timestamp.valueOf(LocalDateTime.now());
            stmt.setTimestamp(3, currentTimestamp);

            int result = stmt.executeUpdate();
            stmt.close();

            if (result > 0) {
                getLibrarians();
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi khi gán vai trò cho thủ thư: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DbConfig.closeConnection(connection);
        }
    }

    public List<Librarian> filter(String searchText, String gender, String ageRange, String salaryRange) {
        List<Librarian> result = new ArrayList<>(librarians);

        // Phần tìm kiếm theo tên
        if (searchText != null && !searchText.trim().isEmpty()) {
            String search = searchText.toLowerCase();
            result = result.stream()
                    .filter(l -> l.getFullName().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }

        if (gender != null && !gender.equals("Tất cả")) {
            result = result.stream()
                    .filter(l -> l.getGender().equals(gender))
                    .collect(Collectors.toList());
        }

        // lọc theo tuổi
        if (ageRange != null && !ageRange.equals("Tất cả")) {
            int minAge = 0;
            int maxAge = Integer.MAX_VALUE;

            // các khoảng tuổi
            if (ageRange.equals("18-25")) {
                minAge = 18;
                maxAge = 25;
            } else if (ageRange.equals("26-40")) {
                minAge = 26;
                maxAge = 40;
            } else if (ageRange.equals("41-50")) {
                minAge = 41;
                maxAge = 50;
            } else if (ageRange.equals(">50")) {
                minAge = 51;
                maxAge = Integer.MAX_VALUE;
            }

            final int finalMinAge = minAge;
            final int finalMaxAge = maxAge;
            result = result.stream()
                    .filter(l -> {
                        int age = l.getAge();
                        return age >= finalMinAge && age <= finalMaxAge;
                    })
                    .collect(Collectors.toList());
        }

        // lọc theo lương
        if (salaryRange != null && !salaryRange.equals("Tất cả")) {
            double minSalary = 0;
            double maxSalary = Double.MAX_VALUE;

            // các khoảng lương
            if (salaryRange.equals("5-7 triệu")) {
                minSalary = 5000000;
                maxSalary = 7000000;
            } else if (salaryRange.equals("8-10 triệu")) {
                minSalary = 8000000;
                maxSalary = 10000000;
            } else if (salaryRange.equals("11-15 triệu")) {
                minSalary = 11000000;
                maxSalary = 15000000;
            } else if (salaryRange.equals("15 triệu trở lên")) {
                minSalary = 15000000;
                maxSalary = Double.MAX_VALUE;
            }

            final double finalMinSalary = minSalary;
            final double finalMaxSalary = maxSalary;
            result = result.stream()
                    .filter(l -> l.getSalary() >= finalMinSalary && l.getSalary() <= finalMaxSalary)
                    .collect(Collectors.toList());
        }

        return result;
    }

    public boolean isRoleNameExists(String roleName, int excludeId) {
        Connection connection = null;
        try {
            connection = DbConfig.getConnection();
            String query = "SELECT COUNT(*) FROM role WHERE nameRole = ? AND id != ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, roleName);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra tên vai trò tồn tại: " + e.getMessage());
            e.printStackTrace();
            return false;
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

            if (result > 0) {
                getRoles();
                return true;
            }
            return false;
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
            connection.setAutoCommit(false);

            // Xóa các liên kết với quyền
            String deletePermissionsQuery = "DELETE FROM permission_role WHERE Roleid = ?";
            PreparedStatement deletePermissionsStmt = connection.prepareStatement(deletePermissionsQuery);
            deletePermissionsStmt.setInt(1, roleId);
            deletePermissionsStmt.executeUpdate();
            deletePermissionsStmt.close();

            // Xóa các liên kết với thủ thư - GIỮ NGUYÊN VÌ ĐÃ ĐÚNG
            String deleteLibrarianRolesQuery = "DELETE FROM librarian_role WHERE role_id = ?";
            PreparedStatement deleteLibrarianRolesStmt = connection.prepareStatement(deleteLibrarianRolesQuery);
            deleteLibrarianRolesStmt.setInt(1, roleId);
            deleteLibrarianRolesStmt.executeUpdate();
            deleteLibrarianRolesStmt.close();

            // Xóa vai trò
            String deleteRoleQuery = "DELETE FROM role WHERE id = ?";
            PreparedStatement deleteRoleStmt = connection.prepareStatement(deleteRoleQuery);
            deleteRoleStmt.setInt(1, roleId);
            int result = deleteRoleStmt.executeUpdate();
            deleteRoleStmt.close();

            connection.commit();

            if (result > 0) {
                getRoles();
                return true;
            }
            return false;
        } catch (SQLException e) {
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            System.err.println("Lỗi khi xóa vai trò: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
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
}
