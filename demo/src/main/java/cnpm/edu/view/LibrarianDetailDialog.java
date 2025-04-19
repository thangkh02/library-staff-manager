package cnpm.edu.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import cnpm.edu.model.Librarian;
import cnpm.edu.model.LibrarianModel;
import cnpm.edu.model.Permission;
import cnpm.edu.model.Role;

public class LibrarianDetailDialog extends JDialog {
    private Librarian librarian;
    private LibrarianModel model;
    private JLabel avatarLabel;
    private JTable rolesTable;
    private JTextArea permissionsArea;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LibrarianDetailDialog(JFrame parent, Librarian librarian, LibrarianModel model) {
        super(parent, "Thông tin chi tiết thủ thư", true);
        this.librarian = librarian;
        this.model = model;

        // Tăng kích thước từ 800x600 lên 1000x700
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15)); // Tăng padding

        initComponents();
    }

    private void initComponents() {
        // Panel tiêu đề
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("THÔNG TIN CHI TIẾT THỦ THƯ");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Tăng cỡ chữ
        headerPanel.add(headerLabel);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Panel thông tin cá nhân (trái)
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Thông tin cá nhân"));

        // Panel ảnh đại diện
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setBackground(Color.WHITE);
        avatarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(180, 180)); // Tăng từ 150x150
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);
        avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        updateAvatarImage();

        avatarPanel.add(avatarLabel, BorderLayout.CENTER);

        // Panel thông tin chi tiết
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addDetailField(detailsPanel, gbc, "Họ tên:", librarian.getFullName(), 0);
        addDetailField(detailsPanel, gbc, "Mã thủ thư:", String.format("%03d", librarian.getId()), 1);
        addDetailField(detailsPanel, gbc, "Ngày sinh:", librarian.getBirthDate().format(dateFormatter), 2);
        addDetailField(detailsPanel, gbc, "Tuổi:", String.valueOf(librarian.getAge()) + " tuổi", 3);
        addDetailField(detailsPanel, gbc, "Giới tính:", librarian.getGender(), 4);
        addDetailField(detailsPanel, gbc, "Email:", librarian.getEmail(), 5);
        addDetailField(detailsPanel, gbc, "Số điện thoại:", librarian.getPhoneNumber(), 6);
        addDetailField(detailsPanel, gbc, "Địa chỉ:", librarian.getAddress(), 7);

        // Format lương
        String formattedSalary = String.format("%,d VNĐ", (long) librarian.getSalary());
        addDetailField(detailsPanel, gbc, "Lương:", formattedSalary, 8);

        infoPanel.add(avatarPanel, BorderLayout.WEST);
        infoPanel.add(detailsPanel, BorderLayout.CENTER);

        // Panel vai trò và quyền (phải)
        JPanel rolePermissionPanel = new JPanel(new BorderLayout(10, 10));
        rolePermissionPanel.setBackground(Color.WHITE);

        // Bảng vai trò
        JPanel rolesPanel = new JPanel(new BorderLayout(5, 5));
        rolesPanel.setBackground(Color.WHITE);
        rolesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Vai trò đảm nhiệm"));

        String[] roleColumns = { "Tên vai trò", "Mô tả", "Ngày được giao" };
        DefaultTableModel roleModel = new DefaultTableModel(roleColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rolesTable = new JTable(roleModel);
        rolesTable.setRowHeight(25);
        JScrollPane roleScrollPane = new JScrollPane(rolesTable);
        roleScrollPane.setPreferredSize(new Dimension(500, 180)); // Tăng kích thước

        fillRolesTable();

        rolesPanel.add(roleScrollPane, BorderLayout.CENTER);

        // Khu vực hiển thị quyền
        JPanel permissionsPanel = new JPanel(new BorderLayout(5, 5));
        permissionsPanel.setBackground(Color.WHITE);
        permissionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Quyền thông qua vai trò"));

        permissionsArea = new JTextArea();
        permissionsArea.setEditable(false);
        permissionsArea.setLineWrap(true);
        permissionsArea.setWrapStyleWord(true);
        permissionsArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane permissionScrollPane = new JScrollPane(permissionsArea);
        permissionScrollPane.setPreferredSize(new Dimension(500, 230)); // Tăng kích thước

        fillPermissionsArea();

        permissionsPanel.add(permissionScrollPane, BorderLayout.CENTER);

        rolePermissionPanel.add(rolesPanel, BorderLayout.NORTH);
        rolePermissionPanel.add(permissionsPanel, BorderLayout.CENTER);

        // Thêm panel vào panel chính
        mainPanel.add(infoPanel, BorderLayout.WEST);
        mainPanel.add(rolePermissionPanel, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(52, 152, 219));
        closeButton.setForeground(Color.WHITE);
        closeButton.setPreferredSize(new Dimension(100, 30));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(closeButton);

        // Thêm các panel vào dialog
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addDetailField(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(valueComponent, gbc);
    }

    private void updateAvatarImage() {
        try {
            String avatarPath = librarian.getAvatarUrl();
            File imageFile = avatarPath.startsWith("avatars/")
                    ? new File(System.getProperty("user.dir"), "src/main/resources/" + avatarPath)
                    : new File(avatarPath);

            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH); // Tăng từ 150x150
                avatarLabel.setIcon(new ImageIcon(img));
            } else {
                avatarLabel.setIcon(null);
                avatarLabel.setText("Không có ảnh");
            }
        } catch (Exception e) {
            avatarLabel.setIcon(null);
            avatarLabel.setText("Lỗi tải ảnh");
        }
    }

    private void fillRolesTable() {
        DefaultTableModel tableModel = (DefaultTableModel) rolesTable.getModel();
        tableModel.setRowCount(0);

        List<Role> roles = librarian.getRoles();
        for (Role role : roles) {
            tableModel.addRow(new Object[] {
                    role.getNameRole(),
                    role.getDescription(),
                    role.getCreateDate() != null ? role.getCreateDate().toLocalDate().format(dateFormatter) : "N/A"
            });
        }
    }

    private void fillPermissionsArea() {
        StringBuilder sb = new StringBuilder();
        List<Role> roles = librarian.getRoles();

        for (Role role : roles) {
            List<Permission> permissions = model.getPermissionsForRole(role);
            sb.append("- Vai trò: ").append(role.getNameRole()).append("\n");

            if (permissions.isEmpty()) {
                sb.append("   Không có quyền\n\n");
            } else {
                for (Permission permission : permissions) {
                    sb.append("   + ").append(permission.getNamePermission());

                    // Thêm mô tả nếu có
                    if (permission.getDescription() != null && !permission.getDescription().isEmpty()) {
                        sb.append(": ").append(permission.getDescription());
                    }

                    sb.append("\n");
                }
                sb.append("\n");
            }
        }

        if (roles.isEmpty()) {
            sb.append("Thủ thư này không có vai trò nào.");
        }

        permissionsArea.setText(sb.toString());
    }
}