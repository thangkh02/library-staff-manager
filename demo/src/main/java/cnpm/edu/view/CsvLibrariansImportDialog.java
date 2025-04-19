package cnpm.edu.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import cnpm.edu.model.LibrarianModel;
import cnpm.edu.model.Role;
import cnpm.edu.utils.CSVData;

/**
 * Dialog hiển thị và xác nhận dữ liệu từ file CSV
 */
public class CsvLibrariansImportDialog extends JDialog {
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JButton addButton, cancelButton;
    private boolean confirmed = false;
    private List<CSVData> csvDataList;
    private LibrarianModel model;
    private List<Role> availableRoles;
    private List<String> errors;

    /**
     * Khởi tạo dialog để hiển thị và xác nhận dữ liệu từ CSV
     * 
     * @param parent      JFrame cha
     * @param csvDataList Danh sách dữ liệu từ file CSV
     * @param model       LibrarianModel để tương tác với CSDL
     * @param roles       Danh sách vai trò có sẵn trong hệ thống
     */
    public CsvLibrariansImportDialog(JFrame parent, List<CSVData> csvDataList, LibrarianModel model, List<Role> roles) {
        super(parent, "Xác nhận thêm thủ thư từ file CSV", true);
        this.csvDataList = csvDataList;
        this.model = model;
        this.availableRoles = roles;
        this.errors = new ArrayList<>();

        initComponents();
        loadData();
    }

    /**
     * Khởi tạo các thành phần giao diện
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(900, 500));

        // Panel tiêu đề
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185));
        JLabel titleLabel = new JLabel("Xác nhận thêm thủ thư từ file CSV");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo bảng dữ liệu
        String[] columns = {
                "STT", "Họ tên", "Lương", "Ngày sinh", "Giới tính",
                "Email", "Số điện thoại", "Địa chỉ", "Vai trò", "Trạng thái"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dataTable = new JTable(tableModel);
        dataTable.setRowHeight(30);
        dataTable.getTableHeader().setReorderingAllowed(false);
        dataTable.getColumnModel().getColumn(0).setPreferredWidth(40); // STT
        dataTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Họ tên
        dataTable.getColumnModel().getColumn(2).setPreferredWidth(80); // Lương
        dataTable.getColumnModel().getColumn(3).setPreferredWidth(90); // Ngày sinh
        dataTable.getColumnModel().getColumn(4).setPreferredWidth(70); // Giới tính
        dataTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Email
        dataTable.getColumnModel().getColumn(6).setPreferredWidth(110); // Số điện thoại
        dataTable.getColumnModel().getColumn(7).setPreferredWidth(140); // Địa chỉ
        dataTable.getColumnModel().getColumn(8).setPreferredWidth(110); // Vai trò
        dataTable.getColumnModel().getColumn(9).setPreferredWidth(160); // Trạng thái

        // Panel chứa bảng dữ liệu
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel chứa buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        addButton = createStyledButton("Thêm", new Color(46, 204, 113));
        cancelButton = createStyledButton("Hủy", new Color(231, 76, 60));

        addButton.addActionListener(e -> {
            if (validateImport()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Thêm các thành phần vào dialog
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * Tải dữ liệu từ danh sách CSV vào bảng
     */
    private void loadData() {
        tableModel.setRowCount(0);

        for (int i = 0; i < csvDataList.size(); i++) {
            CSVData data = csvDataList.get(i);

            // Kiểm tra và hiển thị trạng thái hợp lệ của từng bản ghi
            validateRecord(data);
            String status = data.getError().isEmpty() ? "Hợp lệ" : data.getError();

            tableModel.addRow(new Object[] {
                    i + 1,
                    data.getFullName(),
                    formatCurrency(data.getSalary()),
                    data.getBirthDate(),
                    data.getGender(),
                    data.getEmail(),
                    data.getPhoneNumber(),
                    data.getAddress(),
                    data.getRole(),
                    status
            });
        }
    }

    /**
     * Kiểm tra tính hợp lệ của một bản ghi CSV
     * 
     * @param data CSVData cần kiểm tra
     */
    private void validateRecord(CSVData data) {
        data.setError("");

        // Kiểm tra họ tên
        if (data.getFullName() == null || data.getFullName().trim().isEmpty()) {
            data.setError("Họ tên không được để trống");
            return;
        }

        String nameRegex = "^[\\p{L}\\s]+$";
        if (!data.getFullName().matches(nameRegex)) {
            data.setError("Họ tên không hợp lệ");
            return;
        }

        // Kiểm tra lương
        if (data.getSalary() <= 0) {
            data.setError("Lương phải lớn hơn 0");
            return;
        }

        // Kiểm tra ngày sinh
        if (data.getBirthDate() == null || data.getBirthDate().trim().isEmpty()) {
            data.setError("Ngày sinh không được để trống");
            return;
        }

        try {
            LocalDate birthDate = LocalDate.parse(data.getBirthDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            int age = calculateAge(birthDate);
            if (age < 18) {
                data.setError("Tuổi phải từ 18 trở lên");
                return;
            }
            if (age > 65) {
                data.setError("Tuổi không được quá 65");
                return;
            }
        } catch (DateTimeParseException e) {
            data.setError("Định dạng ngày sinh không hợp lệ (dd/MM/yyyy)");
            return;
        }

        // Kiểm tra giới tính
        if (data.getGender() == null || data.getGender().trim().isEmpty()) {
            data.setError("Giới tính không được để trống");
            return;
        }

        if (!data.getGender().equals("Nam") && !data.getGender().equals("Nữ")) {
            data.setError("Giới tính phải là 'Nam' hoặc 'Nữ'");
            return;
        }

        // Kiểm tra email
        if (data.getEmail() == null || data.getEmail().trim().isEmpty()) {
            data.setError("Email không được để trống");
            return;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!data.getEmail().matches(emailRegex)) {
            data.setError("Email không hợp lệ");
            return;
        }

        // Kiểm tra số điện thoại
        if (data.getPhoneNumber() == null || data.getPhoneNumber().trim().isEmpty()) {
            data.setError("Số điện thoại không được để trống");
            return;
        }

        String phoneRegex = "^0[0-9]{9}$";
        if (!data.getPhoneNumber().matches(phoneRegex)) {
            data.setError("Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số");
            return;
        }

        // Kiểm tra địa chỉ
        if (data.getAddress() == null || data.getAddress().trim().isEmpty()) {
            data.setError("Địa chỉ không được để trống");
            return;
        }

        // Kiểm tra vai trò (nếu có)
        if (data.getRole() != null && !data.getRole().isEmpty()) {
            boolean roleExists = false;
            for (Role role : availableRoles) {
                if (role.getNameRole().equals(data.getRole())) {
                    roleExists = true;
                    break;
                }
            }

            if (!roleExists) {
                data.setError("Vai trò không tồn tại trong hệ thống");
                return;
            }
        }

        // Kiểm tra trùng lặp email trong CSDL
        if (model.isEmailExists(data.getEmail(), -1)) {
            data.setError("Email đã tồn tại trong hệ thống");
            return;
        }

        // Kiểm tra trùng lặp số điện thoại trong CSDL
        if (model.isPhoneExists(data.getPhoneNumber(), -1)) {
            data.setError("Số điện thoại đã tồn tại trong hệ thống");
            return;
        }

        // Kiểm tra trùng lặp thông tin thủ thư trong CSDL
        try {
            LocalDate birthDate = LocalDate.parse(data.getBirthDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            if (model.isLibrarianExists(data.getFullName(), birthDate, data.getGender(), -1)) {
                data.setError("Thủ thư có thông tin tương tự đã tồn tại");
                return;
            }
        } catch (Exception e) {
            // Lỗi đã được xử lý ở trên
        }

        // Kiểm tra trùng lặp trong chính file CSV
        for (CSVData otherData : csvDataList) {
            if (otherData != data && otherData.getEmail().equals(data.getEmail())) {
                data.setError("Email trùng lặp trong file CSV");
                return;
            }

            if (otherData != data && otherData.getPhoneNumber().equals(data.getPhoneNumber())) {
                data.setError("Số điện thoại trùng lặp trong file CSV");
                return;
            }
        }
    }

    /**
     * Kiểm tra xem có thể import dữ liệu không
     * 
     * @return true nếu có thể import, false nếu không
     */
    private boolean validateImport() {
        errors.clear();

        // Kiểm tra xem có bản ghi nào không
        if (csvDataList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có dữ liệu để import",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra từng bản ghi
        boolean hasErrors = false;
        for (CSVData data : csvDataList) {
            if (!data.getError().isEmpty()) {
                hasErrors = true;
                errors.add("Dòng " + (csvDataList.indexOf(data) + 1) + ": " + data.getError());
            }
        }

        if (hasErrors) {
            StringBuilder errorMessage = new StringBuilder("Một số bản ghi có lỗi và không thể import:\n");
            for (int i = 0; i < Math.min(5, errors.size()); i++) {
                errorMessage.append("- ").append(errors.get(i)).append("\n");
            }
            if (errors.size() > 5) {
                errorMessage.append("- ... và ").append(errors.size() - 5).append(" lỗi khác\n");
            }
            errorMessage.append("\nVui lòng sửa lỗi trong file CSV và thử lại.");

            JOptionPane.showMessageDialog(this,
                    errorMessage.toString(),
                    "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Tính toán tuổi từ ngày sinh
     * 
     * @param birthDate Ngày sinh
     * @return Tuổi
     */
    private int calculateAge(LocalDate birthDate) {
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    /**
     * Định dạng số tiền
     * 
     * @param amount Số tiền
     * @return Chuỗi đã định dạng
     */
    private String formatCurrency(double amount) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        formatter.setGroupingUsed(true);
        return formatter.format(amount);
    }

    /**
     * Tạo button với style
     * 
     * @param text    Nội dung button
     * @param bgColor Màu nền
     * @return Button đã tạo
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(100, 40));
        return button;
    }

    /**
     * Kiểm tra người dùng đã xác nhận import hay chưa
     * 
     * @return true nếu người dùng đã xác nhận, false nếu không
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Lấy danh sách dữ liệu CSV
     * 
     * @return Danh sách dữ liệu CSV
     */
    public List<CSVData> getCsvDataList() {
        return csvDataList;
    }

    /**
     * Lấy danh sách các lỗi
     * 
     * @return Danh sách lỗi
     */
    public List<String> getErrors() {
        return errors;
    }
}