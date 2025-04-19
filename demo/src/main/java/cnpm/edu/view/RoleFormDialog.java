package cnpm.edu.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cnpm.edu.model.LibrarianModel;
import cnpm.edu.model.Permission;
import cnpm.edu.model.Role;

public class RoleFormDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JPanel permissionsPanel;
    private Map<Integer, JCheckBox> permissionCheckboxes;
    private JButton confirmButton, cancelButton;
    private boolean confirmed = false;
    private Role editingRole;
    private List<Permission> permissions;

    // Constructor cho thêm vai trò mới
    public RoleFormDialog(JFrame parent) {
        super(parent, "Thêm vai trò mới", true);
        this.permissions = ((LibrarianView) parent).getController().getModel().getPermissions();
        initComponents();
    }

    // Constructor cho sửa thông tin vai trò
    public RoleFormDialog(JFrame parent, Role role) {
        super(parent, "Chỉnh sửa vai trò", true);
        this.editingRole = role;
        this.permissions = ((LibrarianView) parent).getController().getModel().getPermissions();
        initComponents();
        populateFields(role);

        // Nếu đang chỉnh sửa, tự động chọn các quyền hiện có của vai trò
        if (role != null) {
            LibrarianModel model = ((LibrarianView) parent).getController().getModel();
            List<Permission> rolePermissions = model.getPermissionsForRole(role);

            for (Permission permission : rolePermissions) {
                JCheckBox checkbox = permissionCheckboxes.get(permission.getId());
                if (checkbox != null) {
                    checkbox.setSelected(true);
                }
            }
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(600, 500);
        setLocationRelativeTo(getParent());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel(getTitle());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        // Main Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Tên vai trò
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên vai trò:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Mô tả
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mô tả:"), gbc);

        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        // Quyền
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("Quyền:"), gbc);

        // Permission panel
        permissionsPanel = new JPanel();
        permissionsPanel.setLayout(new BoxLayout(permissionsPanel, BoxLayout.Y_AXIS));
        permissionCheckboxes = new HashMap<>();

        if (permissions != null) {
            for (Permission permission : permissions) {
                JCheckBox checkbox = new JCheckBox(
                        permission.getNamePermission() + " - " + permission.getDescription());
                permissionCheckboxes.put(permission.getId(), checkbox);
                permissionsPanel.add(checkbox);
            }
        }

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane permissionsScrollPane = new JScrollPane(permissionsPanel);
        permissionsScrollPane.setPreferredSize(new Dimension(550, 200));
        formPanel.add(permissionsScrollPane, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmButton = new JButton(editingRole == null ? "Thêm vai trò" : "Cập nhật");
        confirmButton.setBackground(new Color(46, 204, 113));
        confirmButton.setForeground(Color.WHITE);

        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);

        confirmButton.addActionListener(e -> {
            if (validateInputs()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // Add components to dialog
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên vai trò",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra nếu có ít nhất một quyền được chọn
        boolean hasSelectedPermission = false;
        for (JCheckBox checkbox : permissionCheckboxes.values()) {
            if (checkbox.isSelected()) {
                hasSelectedPermission = true;
                break;
            }
        }

        if (!hasSelectedPermission) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ít nhất một quyền cho vai trò",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void populateFields(Role role) {
        if (role != null) {
            nameField.setText(role.getNameRole());
            descriptionArea.setText(role.getDescription());
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getRoleName() {
        return nameField.getText().trim();
    }

    public String getRoleDescription() {
        return descriptionArea.getText().trim();
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
}