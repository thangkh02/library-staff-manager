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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.toedter.calendar.JDateChooser;

import cnpm.edu.model.Librarian;
import cnpm.edu.model.LibrarianModel;
import cnpm.edu.model.Role;

public class LibrarianFormDialog extends JDialog {
    // Thay đổi kiểu của birthDateField từ JTextField thành JDateChooser
    private JTextField nameField, salaryField, avatarField, emailField, phoneField, addressField;
    private JDateChooser birthDateChooser; // Thay đổi này
    private JComboBox<String> genderCombo;
    private JList<String> roleList; // Thay thế roleCombo
    private JScrollPane roleScrollPane;
    private JButton saveButton, cancelButton, chooseImageButton;
    private boolean confirmed = false;
    private Librarian editingLibrarian;
    private JLabel avatarPreviewLabel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Thêm trường LibrarianModel và id của thủ thư đang sửa (nếu có)
    private LibrarianModel model;
    private int editingId = -1;

    // Cập nhật constructor để nhận model
    public LibrarianFormDialog(JFrame parent, List<Role> roles, LibrarianModel model) {
        super(parent, "Thêm thủ thư mới", true);
        this.model = model;
        initComponents(roles);
    }

    public LibrarianFormDialog(JFrame parent, Librarian librarian, List<Role> roles, LibrarianModel model) {
        super(parent, "Chỉnh sửa thông tin thủ thư", true);
        this.editingLibrarian = librarian;
        this.model = model;
        this.editingId = librarian.getId();
        initComponents(roles);
        populateFields(librarian);
    }

    private void initComponents(List<Role> roles) {
        setLayout(new BorderLayout(10, 10));

        setPreferredSize(new Dimension(600, 700));
        setResizable(true);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185));
        JLabel titleLabel = new JLabel(getTitle());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Main Form Panel with Scroll
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields
        addField(formPanel, gbc, "Họ và tên:", nameField = new JTextField(20), 0);

        // Thêm trường lương sau trường họ tên trong phương thức initComponents
        gbc.gridx = 0;
        gbc.gridy = 1;
        JPanel salaryLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        salaryLabelPanel.setBackground(Color.WHITE);
        JLabel salaryLabel = new JLabel("Lương (VNĐ):");
        salaryLabelPanel.add(salaryLabel);
        JLabel salaryRequiredMark = new JLabel("*");
        salaryRequiredMark.setForeground(Color.RED);
        salaryLabelPanel.add(salaryRequiredMark);
        formPanel.add(salaryLabelPanel, gbc);

        gbc.gridx = 1;
        salaryField = new JTextField(20);
        formPanel.add(salaryField, gbc);

        // Thay đổi cách tạo và thêm trường ngày sinh
        gbc.gridx = 0;
        gbc.gridy = 2;
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        labelPanel.setBackground(Color.WHITE);
        JLabel birthDateLabel = new JLabel("Ngày sinh:");
        labelPanel.add(birthDateLabel);
        JLabel requiredMark = new JLabel("*");
        requiredMark.setForeground(Color.RED);
        labelPanel.add(requiredMark);
        formPanel.add(labelPanel, gbc);

        gbc.gridx = 1;
        birthDateChooser = new JDateChooser();
        birthDateChooser.setDateFormatString("dd/MM/yyyy");
        birthDateChooser.setPreferredSize(new Dimension(200, 25));

        // Thiết lập giới hạn ngày (chỉ cho phép chọn ngày trong quá khứ)
        Calendar today = Calendar.getInstance();
        birthDateChooser.setMaxSelectableDate(today.getTime());

        formPanel.add(birthDateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        genderCombo = new JComboBox<>(new String[] { "Nam", "Nữ" });
        genderCombo.setPreferredSize(new Dimension(200, 25));
        formPanel.add(genderCombo, gbc);

        // Avatar section
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Ảnh đại diện:"), gbc);
        gbc.gridx = 1;
        JPanel avatarPanel = new JPanel(new BorderLayout(5, 0));
        avatarField = new JTextField(15);
        avatarField.setEditable(false);
        avatarPanel.add(avatarField, BorderLayout.CENTER);
        chooseImageButton = createStyledButton("Chọn ảnh", new Color(52, 152, 219));
        chooseImageButton.addActionListener(e -> chooseImage());
        avatarPanel.add(chooseImageButton, BorderLayout.EAST);
        formPanel.add(avatarPanel, gbc);

        // Avatar preview
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        avatarPreviewLabel = new JLabel();
        avatarPreviewLabel.setHorizontalAlignment(JLabel.CENTER);
        avatarPreviewLabel.setPreferredSize(new Dimension(150, 150));
        avatarPreviewLabel.setBorder(BorderFactory.createLineBorder(new Color(149, 165, 166)));
        formPanel.add(avatarPreviewLabel, gbc);

        // Additional fields
        addField(formPanel, gbc, "Email:", emailField = new JTextField(20), 6);
        addField(formPanel, gbc, "Số điện thoại:", phoneField = new JTextField(20), 7);
        addField(formPanel, gbc, "Địa chỉ:", addressField = new JTextField(20), 8);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Vai trò (giữ Ctrl để chọn nhiều):"), gbc);
        gbc.gridx = 1;

        DefaultListModel<String> roleListModel = new DefaultListModel<>();
        
        for (Role role : roles) {
            roleListModel.addElement(role.getNameRole());
        }

        roleList = new JList<>(roleListModel);
        roleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        roleList.setVisibleRowCount(4); // Hiển thị 4 dòng
        roleScrollPane = new JScrollPane(roleList);
        roleScrollPane.setPreferredSize(new Dimension(200, 80));
        formPanel.add(roleScrollPane, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        saveButton = createStyledButton("Lưu", new Color(46, 204, 113));
        cancelButton = createStyledButton("Hủy", new Color(231, 76, 60));
        saveButton.addActionListener(e -> saveAction());
        cancelButton.addActionListener(e -> {
            // Đặt confirmed = false để biết người dùng đã hủy
            confirmed = false;
            dispose();
        });
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components to dialog
        add(titlePanel, BorderLayout.NORTH);
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        field.setPreferredSize(new Dimension(200, 25));
        panel.add(field, gbc);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row,
            boolean required) {
        gbc.gridx = 0;
        gbc.gridy = row;

        // Tạo panel chứa label và dấu sao cho trường bắt buộc
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        labelPanel.setBackground(Color.WHITE);
        JLabel fieldLabel = new JLabel(label + ":");
        labelPanel.add(fieldLabel);

        if (required) {
            JLabel requiredMark = new JLabel("*");
            requiredMark.setForeground(Color.RED);
            labelPanel.add(requiredMark);
        }

        panel.add(labelPanel, gbc);

        gbc.gridx = 1;
        field.setPreferredSize(new Dimension(200, 25));
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(80, 30));
        return button;
    }

    // Thay đổi phương thức populateFields()
    private void populateFields(Librarian librarian) {
        nameField.setText(librarian.getFullName());

        // Chuyển LocalDate thành Date cho JDateChooser
        Date birthDate = java.sql.Date.valueOf(librarian.getBirthDate());
        birthDateChooser.setDate(birthDate);

        genderCombo.setSelectedItem(librarian.getGender());
        avatarField.setText(librarian.getAvatarUrl() != null ? librarian.getAvatarUrl() : "");
        if (!avatarField.getText().isEmpty())
            updateAvatarPreview(avatarField.getText());
        emailField.setText(librarian.getEmail() != null ? librarian.getEmail() : "");
        phoneField.setText(librarian.getPhoneNumber() != null ? librarian.getPhoneNumber() : "");
        addressField.setText(librarian.getAddress() != null ? librarian.getAddress() : "");
        salaryField.setText(String.valueOf(librarian.getSalary()));

        // Chọn các vai trò của thủ thư trong roleList
        List<Role> libraryRoles = librarian.getRoles();
        int[] selectedIndices = new int[libraryRoles.size()];
        DefaultListModel<String> model = (DefaultListModel<String>) roleList.getModel();

        for (int i = 0; i < libraryRoles.size(); i++) {
            Role role = libraryRoles.get(i);
            for (int j = 0; j < model.getSize(); j++) {
                if (model.getElementAt(j).equals(role.getNameRole())) {
                    selectedIndices[i] = j;
                    break;
                }
            }
        }

        roleList.setSelectedIndices(selectedIndices);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            copyImageToProjectFolder(fileChooser.getSelectedFile());
        }
    }

    private void saveAction() {
        if (validateForm()) {
            confirmed = true;
            dispose();
        }
    }

    // Sửa phương thức validateForm() để kiểm tra trùng lặp email và số điện thoại
    private boolean validateForm() {
        // Kiểm tra tất cả các trường bắt buộc
        if (nameField.getText().trim().isEmpty() ||
                salaryField.getText().trim().isEmpty() ||
                birthDateChooser.getDate() == null ||
                avatarField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty() ||
                addressField.getText().trim().isEmpty()) {

            showError("Yêu cầu nhập đầy đủ thông tin");
            return false;
        }

        // Kiểm tra tên - chỉ chứa chữ cái và khoảng trắng (hỗ trợ tiếng Việt)
        String nameRegex = "^[\\p{L}\\s]+$";
        if (!nameField.getText().matches(nameRegex)) {
            showError("Họ tên không được chứa số hoặc ký tự đặc biệt");
            return false;
        }

        // Kiểm tra ngày sinh - tuổi từ 18-65
        LocalDate birthDate = convertToLocalDate(birthDateChooser.getDate());
        LocalDate now = LocalDate.now();
        Period period = Period.between(birthDate, now);
        int age = period.getYears();

        if (age < 18) {
            showError("Độ tuổi của thủ thư không hợp lệ (tối thiểu 18 tuổi)");
            return false;
        }

        if (age > 65) {
            showError("Độ tuổi của thủ thư không hợp lệ (tối đa 65 tuổi)");
            return false;
        }

        // Kiểm tra email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!emailField.getText().matches(emailRegex)) {
            showError("Định dạng email không hợp lệ");
            return false;
        }

        // Kiểm tra số điện thoại - bắt đầu bằng số 0 và có đúng 10 chữ số
        String phoneRegex = "^0[0-9]{9}$";
        if (!phoneField.getText().matches(phoneRegex)) {
            showError("Số điện thoại không hợp lệ (phải bắt đầu bằng số 0 và có đúng 10 chữ số)");
            return false;
        }

        // Kiểm tra lương
        try {
            double salary = Double.parseDouble(salaryField.getText().trim());
            if (salary < 0) {
                showError("Lương không được âm");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Lương phải là số");
            return false;
        }

        // Kiểm tra email trùng lặp
        if (model != null && model.isEmailExists(emailField.getText(), editingId)) {
            showError("Email này đã tồn tại trong hệ thống. Vui lòng sử dụng email khác.", emailField);
            return false;
        }

        // Kiểm tra số điện thoại trùng lặp
        if (model != null && model.isPhoneExists(phoneField.getText(), editingId)) {
            showError("Số điện thoại này đã tồn tại trong hệ thống. Vui lòng sử dụng số khác.", phoneField);
            return false;
        }

        // Kiểm tra thủ thư trùng lặp
        if (model != null && model.isLibrarianExists(
                nameField.getText(),
                convertToLocalDate(birthDateChooser.getDate()),
                getGender(),
                editingId)) {
            showError("Thủ thư có thông tin tương tự đã tồn tại trong hệ thống.", nameField);
            return false;
        }

        return true;
    }

    // Thêm phương thức showError() để truyền component cần focus
    private void showError(String message, JComponent componentToFocus) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
        if (componentToFocus != null) {
            componentToFocus.requestFocus();
        }
    }

    // Giữ phương thức showError() hiện tại để tương thích
    private void showError(String message) {
        showError(message, null);
    }

    private void updateAvatarPreview(String imagePath) {
        try {
            File imageFile = imagePath.startsWith("avatars/")
                    ? new File(System.getProperty("user.dir"), "src/main/resources/" + imagePath)
                    : new File(imagePath);

            if (!imageFile.exists()) {
                avatarPreviewLabel.setIcon(null);
                avatarPreviewLabel.setText("Không tìm thấy ảnh");
                return;
            }

            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            avatarPreviewLabel.setIcon(new ImageIcon(img));
            avatarPreviewLabel.setText("");
        } catch (Exception e) {
            avatarPreviewLabel.setIcon(null);
            avatarPreviewLabel.setText("Không thể tải ảnh");
        }
    }

    private void copyImageToProjectFolder(File sourceFile) {
        try {
            File avatarsDir = new File(System.getProperty("user.dir"), "src/main/resources/avatars");
            if (!avatarsDir.exists())
                avatarsDir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
            File destFile = new File(avatarsDir, fileName);

            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            avatarField.setText("avatars/" + fileName);
            updateAvatarPreview(destFile.getAbsolutePath());

            JOptionPane.showMessageDialog(this, "Tải ảnh thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showError("Không thể sao chép ảnh: " + ex.getMessage());
        }
    }

    // Getters
    public boolean isConfirmed() {
        return confirmed;
    }

    public String getNameField() {
        return nameField.getText();
    }

    public LocalDate getBirthDate() {
        if (birthDateChooser.getDate() == null) {
            return null;
        }
        return convertToLocalDate(birthDateChooser.getDate());
    }

    public String getGender() {
        return (String) genderCombo.getSelectedItem();
    }

    public String getAvatarField() {
        return avatarField.getText();
    }

    // Thay đổi phương thức getRole() để tương thích ngược
    public String getRole() {
        List<String> roles = getSelectedRoles();
        return roles.isEmpty() ? "Không có vai trò" : roles.get(0);
    }

    public String getEmailField() {
        return emailField.getText();
    }

    public String getPhoneField() {
        return phoneField.getText();
    }

    public String getAddressField() {
        return addressField.getText();
    }

    public Librarian getEditingLibrarian() {
        return editingLibrarian;
    }

    // Thêm phương thức getter cho trường lương
    public double getSalary() {
        try {
            return Double.parseDouble(salaryField.getText().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Thêm phương thức mới để chuyển đổi từ java.util.Date sang LocalDate
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    public List<String> getSelectedRoles() {
        List<String> selectedRoles = roleList.getSelectedValuesList();
        if (selectedRoles.isEmpty() ||
                (selectedRoles.size() == 1 && selectedRoles.get(0).equals("Không có vai trò"))) {
            return new ArrayList<>();
        }
        return selectedRoles;
    }
}