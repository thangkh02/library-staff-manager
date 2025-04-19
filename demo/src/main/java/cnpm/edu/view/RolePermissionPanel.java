package cnpm.edu.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import cnpm.edu.controller.LibrarianController;
import cnpm.edu.model.Librarian;
import cnpm.edu.model.LibrarianRole;
import cnpm.edu.model.Permission;
import cnpm.edu.model.Role;

public class RolePermissionPanel extends JPanel {
    private JComboBox<String> roleCombo;
    private JLabel librarianCountLabel;
    private JPanel permissionsPanel;
    private Map<Integer, JCheckBox> permissionCheckboxes;
    private LibrarianTable librarianRoleTable;
    private DefaultTableModel librarianRoleTableModel;
    private JButton savePermissionsButton;

    private List<Role> roles;
    private List<Permission> permissions;
    private LibrarianController controller;

    public RolePermissionPanel() {
        setLayout(new BorderLayout(10, 10));

        // Top panel containing role selection and librarian count
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Chọn vai trò:"), gbc);

        gbc.gridx = 1;
        roleCombo = new JComboBox<>();
        roleCombo.setPreferredSize(new Dimension(250, 30));
        topPanel.add(roleCombo, gbc);

        // Librarian count
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        librarianCountLabel = new JLabel("Số thủ thư đảm nhiệm: 0");
        topPanel.add(librarianCountLabel, gbc);

        // Main split panel to divide permissions and librarians table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        // Permissions panel with checkboxes
        JPanel permissionsContainer = new JPanel(new BorderLayout());
        permissionsContainer.setBorder(BorderFactory.createTitledBorder("Quyền của vai trò"));

        permissionsPanel = new JPanel();
        permissionsPanel.setLayout(new BoxLayout(permissionsPanel, BoxLayout.Y_AXIS));
        permissionCheckboxes = new HashMap<>();

        JScrollPane permissionsScrollPane = new JScrollPane(permissionsPanel);
        permissionsContainer.add(permissionsScrollPane, BorderLayout.CENTER);

        // Button panel for permissions
        JPanel permissionsButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        savePermissionsButton = new JButton("Lưu thay đổi quyền");
        savePermissionsButton.setBackground(new Color(70, 130, 180));
        savePermissionsButton.setForeground(Color.WHITE);
        permissionsButtonPanel.add(savePermissionsButton);
        permissionsContainer.add(permissionsButtonPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(permissionsContainer);

        // Librarians with this role
        JPanel librarianRolePanel = new JPanel(new BorderLayout());
        librarianRolePanel.setBorder(BorderFactory.createTitledBorder("Thủ thư đảm nhiệm vai trò này"));

        String[] columns = { "Mã thủ thư", "Họ tên", "Ngày đảm nhiệm" };
        librarianRoleTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        librarianRoleTable = new LibrarianTable(librarianRoleTableModel);
        JScrollPane tableScrollPane = new JScrollPane(librarianRoleTable);
        librarianRolePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Thêm nút gỡ thủ thư
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Thêm nút "Thêm thủ thư"
        JButton addLibrarianButton = new JButton("Thêm thủ thư đảm nhiệm");
        addLibrarianButton.setBackground(new Color(46, 139, 87));
        addLibrarianButton.setForeground(Color.WHITE);
        addLibrarianButton.addActionListener(e -> {
            Role selectedRole = getSelectedRole();
            if (selectedRole != null) {
                openAddLibrarianDialog(selectedRole);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn một vai trò trước",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(addLibrarianButton);

        JButton removeLibrarianButton = new JButton("Gỡ khỏi vai trò");
        removeLibrarianButton.setBackground(new Color(220, 53, 69));
        removeLibrarianButton.setForeground(Color.WHITE);
        removeLibrarianButton.addActionListener(e -> {
            int selectedRow = librarianRoleTable.getSelectedRow();
            if (selectedRow >= 0) {
                String librarianId = (String) librarianRoleTableModel.getValueAt(selectedRow, 0);
                String librarianName = (String) librarianRoleTableModel.getValueAt(selectedRow, 1);
                Role selectedRole = getSelectedRole();

                if (selectedRole != null) {
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "Bạn có chắc chắn muốn gỡ thủ thư " + librarianName + " khỏi vai trò "
                                    + selectedRole.getNameRole() + " không?",
                            "Xác nhận gỡ thủ thư",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        // Chuyển đổi ID từ string (001) sang int (1)
                        int id = Integer.parseInt(librarianId.trim());
                        boolean success = controller.getModel().removeLibrarianRole(id, selectedRole.getId());

                        if (success) {
                            JOptionPane.showMessageDialog(this,
                                    "Đã gỡ thủ thư khỏi vai trò thành công",
                                    "Thành công",
                                    JOptionPane.INFORMATION_MESSAGE);
                            updateForSelectedRole(selectedRole.getNameRole());
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Không thể gỡ thủ thư khỏi vai trò",
                                    "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn một thủ thư để gỡ khỏi vai trò",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(removeLibrarianButton);
        librarianRolePanel.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setBottomComponent(librarianRolePanel);

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Initialize role combo box listener
        roleCombo.addActionListener(e -> {
            if (roleCombo.getSelectedItem() != null) {
                String roleName = (String) roleCombo.getSelectedItem();
                updateForSelectedRole(roleName);
            }
        });
    }

    /**
     * Thiết lập controller cho panel
     * 
     * @param controller Controller quản lý thủ thư
     */
    public void setController(LibrarianController controller) {
        this.controller = controller;
        if (controller != null) {
            this.roles = controller.getModel().getRoles();
            this.permissions = controller.getModel().getPermissions();
            initialize(this.roles, this.permissions);
        }
    }

    /**
     * Cập nhật danh sách vai trò trong combo box
     * 
     * @param roles Danh sách vai trò
     */
    public void updateRoleCombo(List<Role> roles) {
        this.roles = roles;
        roleCombo.removeAllItems();
        for (Role role : roles) {
            roleCombo.addItem(role.getNameRole());
        }

        if (roleCombo.getItemCount() > 0) {
            roleCombo.setSelectedIndex(0);
            updateForSelectedRole((String) roleCombo.getSelectedItem());
        }
    }

    // Initialize panel with data
    public void initialize(List<Role> roles, List<Permission> allPermissions) {
        this.roles = new ArrayList<>(roles);
        this.permissions = new ArrayList<>(allPermissions);

        // Populate role combo
        roleCombo.removeAllItems();
        for (Role role : roles) {
            roleCombo.addItem(role.getNameRole());
        }

        // Set first role as selected if available
        if (roleCombo.getItemCount() > 0) {
            roleCombo.setSelectedIndex(0);
        }
    }

    private void updateForSelectedRole(String roleName) {
        // Find the selected role
        Role selectedRole = roles.stream()
                .filter(r -> r.getNameRole().equals(roleName))
                .findFirst()
                .orElse(null);

        if (selectedRole == null)
            return;

        // Debug log
        System.out.println("DEBUG: Cập nhật cho vai trò: " + roleName + " (ID=" + selectedRole.getId() + ")");

        // Clear existing checkboxes
        permissionsPanel.removeAll();
        permissionCheckboxes.clear();

        // Get permissions for this role
        List<Permission> rolePermissions = getRolePermissions(selectedRole);
        System.out.println("DEBUG: Vai trò có " + rolePermissions.size() + " quyền");

        // Debug log
        for (Permission p : rolePermissions) {
            System.out.println("DEBUG: Quyền của vai trò: " + p.getId() + " - " + p.getNamePermission());
        }

        // Create checkboxes for all permissions
        for (Permission permission : permissions) {
            JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JCheckBox checkbox = new JCheckBox(permission.getNamePermission());
            // Kiểm tra xem quyền có thuộc về vai trò không
            boolean hasPermission = rolePermissions.stream()
                    .anyMatch(p -> p.getId() == permission.getId());
            checkbox.setSelected(hasPermission);

            JLabel descLabel = new JLabel(" - " + permission.getDescription());

            checkboxPanel.add(checkbox);
            checkboxPanel.add(descLabel);
            permissionsPanel.add(checkboxPanel);
            permissionCheckboxes.put(permission.getId(), checkbox);

            // Add some spacing
            permissionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        updateLibrarianCount(selectedRole);

        // Update librarians table
        updateLibrariansTable(selectedRole);

        // Set save button action
        if (savePermissionsButton.getActionListeners().length > 0) {
            // Remove existing action listeners to avoid duplicates
            for (ActionListener al : savePermissionsButton.getActionListeners()) {
                savePermissionsButton.removeActionListener(al);
            }
        }

        savePermissionsButton.addActionListener(e -> {
            List<Integer> selectedPermissions = getSelectedPermissionIds();

            // Thêm kiểm tra xem có quyền nào được chọn không
            if (selectedPermissions.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Cần chọn ít nhất một quyền cho mỗi vai trò",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);

                // Tự động tải lại dữ liệu từ database để cập nhật UI
                updateForSelectedRole(selectedRole.getNameRole());
                return;
            }

            System.out.println("DEBUG: Lưu " + selectedPermissions.size() + " quyền được chọn cho vai trò "
                    + selectedRole.getNameRole());

            if (controller != null) {
                boolean success = controller.getModel().updateRolePermissions(selectedRole.getId(),
                        selectedPermissions);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Đã cập nhật quyền cho vai trò " + selectedRole.getNameRole(),
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh the panel
                    updateForSelectedRole(selectedRole.getNameRole());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể cập nhật quyền cho vai trò " + selectedRole.getNameRole(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Refresh UI
        permissionsPanel.revalidate();
        permissionsPanel.repaint();
    }

    // Get permissions for a specific role
    private List<Permission> getRolePermissions(Role role) {
        if (controller != null) {
            return controller.getModel().getPermissionsForRole(role);
        }
        return new ArrayList<>();
    }

    private void updateLibrarianCount(Role role) {
        int count = 0;
        if (controller != null) {
            // Không truy vấn trực tiếp database mà sử dụng danh sách thủ thư đã được tải
            for (Librarian librarian : controller.getModel().getLibrarians()) {
                // Kiểm tra vai trò của thủ thư bằng cách duyệt qua danh sách vai trò
                if (librarian.getRoles().stream().anyMatch(r -> r.getId() == role.getId())) {
                    count++;
                }
            }
        }
        librarianCountLabel.setText("Số thủ thư đảm nhiệm: " + count);
    }

    private void updateLibrariansTable(Role role) {
        // Clear existing table data
        librarianRoleTableModel.setRowCount(0);

        if (controller == null)
            return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        try {
            for (Librarian librarian : controller.getModel().getLibrarians()) {
                // Kiểm tra nếu thủ thư có vai trò này
                if (librarian.getRoles().stream().anyMatch(r -> r.getId() == role.getId())) {
                    // Lấy thông tin librarianRole từ model - dữ liệu từ database
                    LibrarianRole librarianRole = controller.getModel().getLibrarianRoleByIds(librarian.getId(),
                            role.getId());

                    // Debug thông tin librarian và role
                    System.out.println("DEBUG: Hiển thị thông tin thủ thư " + librarian.getId() +
                            " (" + librarian.getFullName() + ") với vai trò " +
                            role.getId() + " (" + role.getNameRole() + ")");

                    String formattedId = String.format("%03d", librarian.getId());

                    // Debug thông tin ngày đảm nhiệm
                    String assignedDate;
                    if (librarianRole != null && librarianRole.getAssignedDate() != null) {
                        // Chỉ lấy phần ngày từ LocalDateTime
                        assignedDate = librarianRole.getAssignedDate().toLocalDate()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } else {
                        assignedDate = "N/A";
                    }

                    librarianRoleTableModel.addRow(new Object[] {
                            formattedId,
                            librarian.getFullName(),
                            assignedDate
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật bảng thủ thư: " + e.getMessage());
            e.printStackTrace();
        }

        // Làm mới bảng
        librarianRoleTable.repaint();
    }

    public void setRolePermissions(Role role, List<Permission> permissions) {
        if (role == null)
            return;

        SwingUtilities.invokeLater(() -> {
            // Select the role in the combo box
            roleCombo.setSelectedItem(role.getNameRole());

            // Update checkboxes
            for (Permission permission : permissions) {
                JCheckBox checkbox = permissionCheckboxes.get(permission.getId());
                if (checkbox != null) {
                    checkbox.setSelected(true);
                }
            }
        });
    }

    public void setLibrariansWithRole(Role role, List<LibrarianRole> librariansWithRole) {
        if (role == null)
            return;

        librarianRoleTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (LibrarianRole librarianRole : librariansWithRole) {
            Librarian librarian = librarianRole.getLibrarian();
            if (librarian != null) {
                String formattedId = String.format("%03d", librarian.getId());
                String formattedDate = librarianRole.getAssignedDate().format(formatter);

                librarianRoleTableModel.addRow(new Object[] {
                        formattedId,
                        librarian.getFullName(),
                        formattedDate
                });
            }
        }

        // Update librarian count
        librarianCountLabel.setText("Số thủ thư đảm nhiệm: " + librariansWithRole.size());
    }

    // Methods to get selected values and state
    public Role getSelectedRole() {
        String roleName = (String) roleCombo.getSelectedItem();
        if (roleName == null)
            return null;

        return roles.stream()
                .filter(r -> r.getNameRole().equals(roleName))
                .findFirst()
                .orElse(null);
    }

    public List<Integer> getSelectedPermissionIds() {
        List<Integer> selectedIds = new ArrayList<>();

        for (Map.Entry<Integer, JCheckBox> entry : permissionCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedIds.add(entry.getKey());
            }
        }

        return selectedIds;
    }

    // Set listeners
    public void setSavePermissionsListener(ActionListener listener) {
        savePermissionsButton.addActionListener(listener);
    }

    public void setRoleSelectionListener(ActionListener listener) {
        roleCombo.addActionListener(listener);
    }

    /**
     * Mở dialog thêm thủ thư cho vai trò
     * 
     * @param role Vai trò cần thêm thủ thư
     */
    private void openAddLibrarianDialog(Role role) {
        if (controller == null)
            return;

        // Lấy danh sách tất cả thủ thư
        List<Librarian> allLibrarians = controller.getModel().getLibrarians();

        // Lấy danh sách thủ thư không có vai trò này
        List<Librarian> availableLibrarians = allLibrarians.stream()
                .filter(librarian -> !librarian.getRoles().stream().anyMatch(r -> r.getId() == role.getId()))
                .collect(Collectors.toList());

        if (availableLibrarians.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có thủ thư nào khả dụng để thêm vào vai trò này",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JFrame parentFrame = null;

        // Kiểm tra kiểu Window trả về
        if (parentWindow instanceof JFrame) {
            parentFrame = (JFrame) parentWindow;
        } else if (parentWindow != null) {
            // Nếu không phải JFrame, tạo JFrame mới làm parent
            parentFrame = new JFrame();
            parentFrame.setLocationRelativeTo(parentWindow);
        } else {
            // Nếu không có parent window, tạo JFrame mới
            parentFrame = new JFrame();
        }

        AddLibrarianToRoleDialog dialog = new AddLibrarianToRoleDialog(
                parentFrame,
                availableLibrarians,
                role);

        dialog.setVisible(true);

        // Xử lý kết quả sau khi dialog đóng
        if (dialog.isConfirmed() && !dialog.getSelectedLibrarians().isEmpty()) {
            // Thêm từng thủ thư được chọn vào vai trò
            List<Librarian> selectedLibrarians = dialog.getSelectedLibrarians();
            int successCount = 0;

            for (Librarian librarian : selectedLibrarians) {
                boolean success = controller.getModel().assignRoleToLibrarian(librarian, role);
                if (success) {
                    successCount++;
                }
            }

            // Hiển thị thông báo và cập nhật giao diện
            if (successCount > 0) {
                JOptionPane.showMessageDialog(this,
                        "Đã thêm " + successCount + " thủ thư vào vai trò " + role.getNameRole(),
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);

                // Cập nhật lại giao diện
                updateForSelectedRole(role.getNameRole());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể thêm thủ thư vào vai trò",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
