package cnpm.edu.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font; // Thêm import thiếu
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox; // Thêm import thiếu
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import cnpm.edu.controller.LibrarianController;
import cnpm.edu.model.Librarian;
import cnpm.edu.model.Permission;
import cnpm.edu.model.Role;

public class LibrarianView extends JFrame {
    private LibrarianTable librarianTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, salaryField, birthDateField, avatarField, searchField;
    private JComboBox<String> genderCombo, roleCombo, sortCriteriaCombo, sortOrderCombo, filterGenderCombo,
            filterAgeCombo, filterSalaryCombo;
    private JComboBox<String> roleComboAssign, librarianComboAssign;
    private JButton addButton, updateButton, deleteButton, searchButton, sortButton, filterButton, assignRoleButton;
    private JTextArea permissionsTextArea;
    private LibrarianController controller;
    private JPanel contentPanel;
    private JButton resetButton;
    private JButton viewDetailsButton; // Thêm nút xem chi tiết

    // Thêm thành phần cho quản lý vai trò - GIỮ LẠI MỘT PHIÊN BẢN DUY NHẤT
    private DefaultTableModel roleTableModel;
    private JTable roleTable;
    private JButton addRoleButton, deleteRoleButton;

    // Thêm các thành phần cho tab phân quyền
    private RolePermissionPanel rolePermissionPanel;
    private JLabel tabLibrarianLabel;
    private JLabel tabRoleLabel;
    private JLabel tabPermissionLabel;
    private JPanel highlightBar;

    public LibrarianView() {
        setTitle("Hệ thống quản lý thủ thư");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));

        // Tạo panel chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));

        // Tạo panel chứa các label tab (thay thế JTabbedPane)
        JPanel tabHeaderPanel = new JPanel();
        tabHeaderPanel.setBackground(new Color(255, 255, 255));
        tabHeaderPanel.setLayout(null);
        tabHeaderPanel.setPreferredSize(new Dimension(900, 40));

        // Tạo các label tab với khoảng cách phù hợp
        tabLibrarianLabel = new JLabel("QUẢN LÝ THỦ THƯ");
        tabLibrarianLabel.setFont(new Font("Arial", Font.BOLD, 13));
        tabLibrarianLabel.setBounds(20, 5, 140, 30);
        tabLibrarianLabel.setForeground(new Color(0, 0, 0, 140));
        tabHeaderPanel.add(tabLibrarianLabel);

        tabRoleLabel = new JLabel("VAI TRÒ");
        tabRoleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        tabRoleLabel.setBounds(170, 5, 100, 30);
        tabRoleLabel.setForeground(new Color(0, 0, 0, 60));
        tabHeaderPanel.add(tabRoleLabel);

        // Thêm label tab mới cho Chi tiết vai trò (Đổi tên từ Phân quyền)
        tabPermissionLabel = new JLabel("CHI TIẾT VAI TRÒ");
        tabPermissionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        tabPermissionLabel.setBounds(280, 5, 140, 30);
        tabPermissionLabel.setForeground(new Color(0, 0, 0, 60));
        tabHeaderPanel.add(tabPermissionLabel);

        // Thanh ngang chỉ tab đang chọn
        highlightBar = new JPanel();
        highlightBar.setBounds(15, 35, tabLibrarianLabel.getWidth(), 4);
        highlightBar.setBackground(new Color(135, 206, 250));
        tabHeaderPanel.add(highlightBar);

        // Thanh ngang dưới toàn bộ tab
        JPanel tabBottomLine = new JPanel();
        tabBottomLine.setBounds(0, 35, 900, 1);
        tabBottomLine.setBackground(new Color(220, 220, 220));
        tabHeaderPanel.add(tabBottomLine);

        // Tạo panel nội dung chứa các tab content với CardLayout
        contentPanel = new JPanel();
        contentPanel.setLayout(new CardLayout());

        // Tạo nội dung cho tab Quản lý thủ thư
        JPanel librarianPanel = createLibrarianPanel(); // Sử dụng phương thức mới

        // Tạo vai trò combo và các thành phần khác cho tính năng gán vai trò
        roleCombo = new JComboBox<>();
        roleComboAssign = new JComboBox<>();
        librarianComboAssign = new JComboBox<>();
        assignRoleButton = new JButton("Gán vai trò");
        assignRoleButton.setBackground(new Color(70, 130, 180));
        assignRoleButton.setForeground(Color.WHITE);
        permissionsTextArea = new JTextArea(10, 30);
        permissionsTextArea.setEditable(false);

        // Tab 2: Vai trò - sử dụng phương thức mới tạo panel quản lý vai trò
        JPanel rolePanel = initRolePanel(); // Sửa tên phương thức

        // Tab 3: Phân quyền (sử dụng RolePermissionPanel mới)
        rolePermissionPanel = new RolePermissionPanel();

        // Thêm các panel nội dung vào contentPanel
        contentPanel.add(librarianPanel, "librarian");
        contentPanel.add(rolePanel, "role");
        contentPanel.add(rolePermissionPanel, "permission");

        // Thêm sự kiện chuyển tab
        tabLibrarianLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel("librarian");
                updateTabSelectionState(tabLibrarianLabel, 15);
            }
        });

        tabRoleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel("role");
                updateTabSelectionState(tabRoleLabel, tabRoleLabel.getX() - 5);
            }
        });

        // Thêm sự kiện cho tab Phân quyền mới
        tabPermissionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel("permission");
                updateTabSelectionState(tabPermissionLabel, tabPermissionLabel.getX() - 5);
            }
        });

        mainPanel.add(tabHeaderPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        roleComboAssign.addActionListener(e -> {
            if (controller != null) {
                String selectedRoleName = (String) roleComboAssign.getSelectedItem();
                if (selectedRoleName != null) {
                    Role selectedRole = controller.getModel().getRoles().stream()
                            .filter(r -> r.getNameRole().equals(selectedRoleName))
                            .findFirst().orElse(null);
                    if (selectedRole != null) {
                        List<Permission> rolePermissions = controller.getModel().getPermissionsForRole(selectedRole);
                        StringBuilder permissionsText = new StringBuilder();
                        for (Permission p : rolePermissions) {
                            permissionsText.append("- ").append(p.getNamePermission()).append(": ")
                                    .append(p.getDescription()).append("\n");
                        }
                        permissionsTextArea.setText(permissionsText.toString());
                    }
                }
            }
        });
    }

    // Thêm phương thức để tạo panel quản lý thủ thư
    private JPanel createLibrarianPanel() {
        JPanel librarianPanel = new JPanel(new BorderLayout());
        librarianPanel.setBackground(new Color(240, 248, 255));

        String[] columns = { "Mã", "Họ tên", "Lương", "Ngày sinh", "Giới tính", "Địa chỉ", "Số điện thoại", "Email" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        librarianTable = new LibrarianTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(librarianTable);
        librarianPanel.add(tableScrollPane, BorderLayout.CENTER);
        librarianTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = librarianTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    // Đặt hàng được chọn
                    librarianTable.setRowSelectionInterval(row, row);

                    // Buộc bảng phải vẽ lại ngay lập tức
                    librarianTable.repaint();
                    try {
                        // Lấy ID dưới dạng String rồi parse sang int
                        String formattedId = (String) tableModel.getValueAt(row, 0);
                        int id = Integer.parseInt(formattedId);
                        String name = (String) tableModel.getValueAt(row, 1);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                librarianTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                librarianTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.setBorder(null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(20);
        salaryField = new JTextField(20);
        birthDateField = new JTextField(20);
        avatarField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        addButton = new JButton("Thêm thủ thư mới");
        addButton.setBackground(new Color(50, 205, 50));
        addButton.setForeground(Color.WHITE);
        inputPanel.add(addButton, gbc);

        // Nút Sửa
        gbc.gridy = 7;
        updateButton = new JButton("Sửa thủ thư");
        updateButton.setBackground(new Color(255, 165, 0));
        updateButton.setForeground(Color.WHITE);
        inputPanel.add(updateButton, gbc);

        // Nút Xóa
        gbc.gridy = 8;
        deleteButton = new JButton("Xóa thủ thư");
        deleteButton.setBackground(new Color(255, 69, 0));
        deleteButton.setForeground(Color.WHITE);
        inputPanel.add(deleteButton, gbc);

        gbc.gridy = 9;
        viewDetailsButton = createStyledButton("Xem chi tiết", new Color(155, 89, 182));
        inputPanel.add(viewDetailsButton, gbc);

        // Panel tìm kiếm, sắp xếp, lọc
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(245, 245, 245));
        controlPanel.setBorder(null);
        GridBagConstraints gbcControl = new GridBagConstraints();
        gbcControl.insets = new Insets(5, 5, 5, 5);
        gbcControl.fill = GridBagConstraints.HORIZONTAL;

        gbcControl.gridx = 0;
        gbcControl.gridy = 0;
        controlPanel.add(new JLabel("Tìm theo tên:"), gbcControl);
        gbcControl.gridx = 1;
        searchField = new JTextField(10);
        controlPanel.add(searchField, gbcControl);
        gbcControl.gridx = 2;
        searchButton = new JButton("Tìm Kiếm");
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        controlPanel.add(searchButton, gbcControl);

        gbcControl.gridx = 0;
        gbcControl.gridy = 1;
        controlPanel.add(new JLabel("Sắp xếp theo:"), gbcControl);
        gbcControl.gridx = 1;
        sortCriteriaCombo = new JComboBox<>(new String[] { "Mã", "Tên", "Tuổi", "Lương" });
        controlPanel.add(sortCriteriaCombo, gbcControl);
        gbcControl.gridx = 2;
        sortOrderCombo = new JComboBox<>(new String[] { "Tăng dần", "Giảm dần" });
        controlPanel.add(sortOrderCombo, gbcControl);
        gbcControl.gridx = 3;
        sortButton = new JButton("Sắp xếp");
        sortButton.setBackground(new Color(70, 130, 180));
        sortButton.setForeground(Color.WHITE);
        controlPanel.add(sortButton, gbcControl);

        gbcControl.gridx = 0;
        gbcControl.gridy = 2;
        controlPanel.add(new JLabel("Lọc giới tính:"), gbcControl);
        gbcControl.gridx = 1;
        filterGenderCombo = new JComboBox<>(new String[] { "Tất cả", "Nam", "Nữ" });
        controlPanel.add(filterGenderCombo, gbcControl);
        gbcControl.gridx = 2;
        controlPanel.add(new JLabel("Lọc tuổi:"), gbcControl);
        gbcControl.gridx = 3;
        filterAgeCombo = new JComboBox<>(new String[] { "Tất cả", "18-25", "26-40", "41-50" });
        controlPanel.add(filterAgeCombo, gbcControl);
        gbcControl.gridx = 4;
        controlPanel.add(new JLabel("Lọc lương:"), gbcControl);
        gbcControl.gridx = 5;
        filterSalaryCombo = new JComboBox<>(
                new String[] { "Tất cả", "5-7 triệu", "8-10 triệu", "11-15 triệu", "15 triệu trở lên" });
        controlPanel.add(filterSalaryCombo, gbcControl);
        gbcControl.gridx = 6;
        filterButton = new JButton("Lọc");
        filterButton.setBackground(new Color(70, 130, 180));
        filterButton.setForeground(Color.WHITE);
        controlPanel.add(filterButton, gbcControl);

        gbcControl.gridx = 7;
        resetButton = new JButton("Đặt lại");
        resetButton.setBackground(new Color(70, 130, 180));
        resetButton.setForeground(Color.WHITE);
        controlPanel.add(resetButton, gbcControl);

        librarianPanel.add(controlPanel, BorderLayout.NORTH);
        librarianPanel.add(inputPanel, BorderLayout.WEST);

        return librarianPanel;
    }

    // Phương thức helper để tạo button có style
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }
    public void setController(LibrarianController controller) {
        this.controller = controller;
        addButton.addActionListener(e -> controller.addLibrarian());
        updateButton.addActionListener(e -> controller.updateLibrarian());
        deleteButton.addActionListener(e -> controller.deleteLibrarian());
        viewDetailsButton.addActionListener(e -> controller.viewLibrarianDetails());
        searchButton.addActionListener(e -> controller.searchLibrarian());
        sortButton.addActionListener(e -> controller.sortLibrarians());
        filterButton.addActionListener(e -> controller.filterLibrarians());
        assignRoleButton.addActionListener(e -> controller.assignRole());
        resetButton.addActionListener(e -> controller.resetFilters());


        librarianTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    controller.viewLibrarianDetails();
                }
            }
        });

    
        addRoleButton.addActionListener(e -> controller.addRole());
        deleteRoleButton.addActionListener(e -> controller.deleteRole());


        rolePermissionPanel.setController(controller);
    }

    
    public void updateTable(List<Librarian> librarians) {
        tableModel.setRowCount(0);

        if (librarians == null || librarians.isEmpty()) {
            System.out.println("Cảnh báo: Danh sách thủ thư trống hoặc null");
            return;
        }

        // Tạo định dạng ngày
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Librarian librarian : librarians) {
            // Thay đổi từ "LM%03d" thành chỉ "%03d" - bỏ tiền tố LM
            String formattedId = String.format("%03d", librarian.getId());

            // Format ngày sinh thành dd/MM/yyyy
            String formattedBirthDate = librarian.getBirthDate().format(dateFormatter);

            // Lấy lương trực tiếp từ thủ thư và định dạng số
            String formattedSalary = formatNumberWithThousandSeparators(librarian.getSalary());

            tableModel.addRow(new Object[] {
                    formattedId, // Mã đã được định dạng mới: 001, 002, ...
                    librarian.getFullName(), // Họ tên
                    formattedSalary, // Lương định dạng 1.000.000
                    formattedBirthDate, // Ngày sinh đã định dạng
                    librarian.getGender(), // Giới tính
                    librarian.getAddress(), // Địa chỉ
                    librarian.getPhoneNumber(), // Số điện thoại
                    librarian.getEmail() // Email
            });
        }

        librarianTable.repaint();
    }

    // Thêm phương thức định dạng số có dấu phân cách hàng nghìn
    private String formatNumberWithThousandSeparators(double number) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        formatter.setGroupingUsed(true);
        return formatter.format(number);
    }

    public void updateRoleCombo(List<Role> roles) {
        if (roleCombo != null) {
            roleCombo.removeAllItems();
        }
        if (roleComboAssign != null) {
            roleComboAssign.removeAllItems();
        }

        List<Role> rolesCopy = new ArrayList<>(roles);

        if (roleCombo != null) {
            roleCombo.addItem("Không có vai trò");
        }

        // Sử dụng bản sao để lặp qua
        for (Role role : rolesCopy) {
            if (roleCombo != null) {
                roleCombo.addItem(role.getNameRole());
            }
            if (roleComboAssign != null) {
                roleComboAssign.addItem(role.getNameRole());
            }
        }

        // Cập nhật dropdown vai trò trong panel phân quyền
        if (rolePermissionPanel != null) {
            rolePermissionPanel.updateRoleCombo(rolesCopy);
        }
    }

    public void updateLibrarianCombo(List<Librarian> librarians) {
        librarianComboAssign.removeAllItems();
        for (Librarian librarian : librarians) {

            String formattedId = String.format("%03d", librarian.getId());
            librarianComboAssign.addItem(librarian.getFullName() + " (ID: " + formattedId + ")");
        }
    }

    public String getNameField() {
        return nameField.getText();
    }

    public String getSalaryField() {
        return salaryField.getText();
    }

    public String getBirthDateField() {
        return birthDateField.getText();
    }

    public String getGender() {
        return (String) genderCombo.getSelectedItem();
    }

    public String getAvatarField() {
        return avatarField.getText();
    }

    public String getRole() {
        return (String) roleCombo.getSelectedItem();
    }

    public int getSelectedLibrarianId() {
        int selectedRow = librarianTable.getSelectedRow();
        if (selectedRow >= 0) {
            String formattedId = (String) tableModel.getValueAt(selectedRow, 0);

            return Integer.parseInt(formattedId);
        }
        return -1;
    }

    public String getSearchField() {
        return searchField.getText();
    }

    public String getSortCriteria() {
        return (String) sortCriteriaCombo.getSelectedItem();
    }

    public boolean isSortAscending() {
        return sortOrderCombo.getSelectedItem().equals("Tăng dần");
    }

    public String getFilterGender() {
        return (String) filterGenderCombo.getSelectedItem();
    }

    public String getFilterAge() {
        return (String) filterAgeCombo.getSelectedItem();
    }

    public String getFilterSalary() {
        return (String) filterSalaryCombo.getSelectedItem();
    }

    public String getSelectedLibrarianForAssign() {
        return (String) librarianComboAssign.getSelectedItem();
    }

    public String getSelectedRoleForAssign() {
        return (String) roleComboAssign.getSelectedItem();
    }

    private void showPanel(String name) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);
    }

    // Cập nhật trạng thái tab được chọn
    private void updateTabSelectionState(JLabel selectedTab, int barX) {
        // Cập nhật màu sắc các tab
        tabLibrarianLabel.setForeground(new Color(0, 0, 0, 60));
        tabRoleLabel.setForeground(new Color(0, 0, 0, 60));
        tabPermissionLabel.setForeground(new Color(0, 0, 0, 60));

        // Đặt tab được chọn là đậm
        selectedTab.setForeground(new Color(0, 0, 0, 140));

        // Hiệu ứng di chuyển thanh highlight
        animateHighlightBar(highlightBar, highlightBar.getX(), barX, selectedTab.getWidth());
    }

    private void animateHighlightBar(JPanel bar, int startX, int endX, int width) {
        new Thread(() -> {
            try {
                int step = startX < endX ? 2 : -2;
                for (int i = startX; startX < endX ? i <= endX : i >= endX; i += step) {
                    bar.setBounds(i, bar.getY(), width, bar.getHeight());
                    repaint();
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Thêm phương thức để đặt lại tất cả các điều kiện
    public void resetFilters() {
        // Đặt về giá trị mặc định
        searchField.setText("");
        sortCriteriaCombo.setSelectedItem("Mã");
        sortOrderCombo.setSelectedItem("Tăng dần");
        filterGenderCombo.setSelectedItem("Tất cả");
        filterAgeCombo.setSelectedItem("Tất cả");
        filterSalaryCombo.setSelectedItem("Tất cả");
    }

    public int getSelectedRoleId() {
        int selectedRow = roleTable.getSelectedRow();
        if (selectedRow >= 0) {
            return Integer.parseInt(roleTableModel.getValueAt(selectedRow, 0).toString());
        }
        return -1;
    }

    // Thêm phương thức để cập nhật bảng vai trò
    public void updateRoleTable(List<Role> roles) {
        roleTableModel.setRowCount(0);

        // Tạo định dạng ngày (chỉ ngày, không có giờ)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Role role : roles) {
            // Đếm số thủ thư đảm nhiệm vai trò này
            int librarianCount = 0;
            if (controller != null) {
                librarianCount = (int) controller.getModel().getLibrarians().stream()
                        .filter(lib -> lib.getRoles().stream()
                                .anyMatch(r -> r.getId() == role.getId()))
                        .count();
            }

            // Format ngày tạo thành chuỗi (chỉ ngày)
            String formattedDate = role.getCreateDate() != null ? role.getCreateDate().format(formatter) : "N/A";

            roleTableModel.addRow(new Object[] {
                    role.getId(),
                    role.getNameRole(),
                    role.getDescription(),
                    librarianCount,
                    formattedDate
            });
        }
    }

    // Phương thức reset form vai trò
    public void resetRoleForm() {
        roleTable.clearSelection();
    }

    // Thêm phương thức khởi tạo mới cho tab vai trò
    private JPanel initRolePanel() {
        JPanel rolePanel = new JPanel(new BorderLayout(10, 10));
        rolePanel.setBackground(new Color(240, 248, 255));

        // Panel cho các nút chức năng
        JPanel roleControlPanel = new JPanel(new GridBagLayout());
        roleControlPanel.setBackground(new Color(245, 245, 245));
        roleControlPanel.setBorder(BorderFactory.createTitledBorder("Quản lý vai trò"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        // Chỉ tạo các nút chức năng
        addRoleButton = new JButton("Thêm vai trò");
        addRoleButton.setBackground(new Color(50, 205, 50));
        addRoleButton.setForeground(Color.WHITE);

        deleteRoleButton = new JButton("Xóa");
        deleteRoleButton.setBackground(new Color(255, 69, 0));
        deleteRoleButton.setForeground(Color.WHITE);

        // Thêm các nút vào panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        roleControlPanel.add(addRoleButton, gbc);

        gbc.gridy = 1;
        roleControlPanel.add(deleteRoleButton, gbc);

        // Thêm space để đẩy các nút lên trên
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        roleControlPanel.add(new JPanel(), gbc);

        // Panel cho bảng danh sách vai trò
        JPanel roleTablePanel = new JPanel(new BorderLayout());
        roleTablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách vai trò"));

        String[] roleColumns = { "Mã vai trò", "Tên vai trò", "Mô tả", "Số thủ thư đảm nhiệm", "Ngày tạo" };
        roleTableModel = new DefaultTableModel(roleColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roleTable = new JTable(roleTableModel);
        roleTable.setRowHeight(30);
        roleTable.getTableHeader().setReorderingAllowed(false);
        roleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane roleScrollPane = new JScrollPane(roleTable);
        roleTablePanel.add(roleScrollPane, BorderLayout.CENTER);

        // Thiết lập layout chính cho panel vai trò
        roleControlPanel.setPreferredSize(new Dimension(200, 300));

        rolePanel.add(roleControlPanel, BorderLayout.WEST);
        rolePanel.add(roleTablePanel, BorderLayout.CENTER);

        return rolePanel;
    }

    public LibrarianController getController() {
        return controller;
    }
}