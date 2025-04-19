package cnpm.edu.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import cnpm.edu.model.Librarian;
import cnpm.edu.model.Role;

public class AddLibrarianToRoleDialog extends JDialog {
    private JTable librarianTable;
    private DefaultTableModel tableModel;
    private boolean confirmed = false;
    private List<Librarian> librarians;
    private Role role;

    public AddLibrarianToRoleDialog(JFrame parent, List<Librarian> availableLibrarians, Role role) {
        super(parent, "Thêm thủ thư vào vai trò " + role.getNameRole(), true);
        this.librarians = new ArrayList<>(availableLibrarians);
        this.role = role;
        
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
      
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Chọn thủ thư để thêm vào vai trò " + role.getNameRole());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        
        // Bảng danh sách thủ thư
        String[] columns = {"Mã", "Họ tên", "Các vai trò hiện tại"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        librarianTable = new JTable(tableModel);
        librarianTable.setRowHeight(30);
        librarianTable.getTableHeader().setReorderingAllowed(false);
        librarianTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        librarianTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        librarianTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        librarianTable.getColumnModel().getColumn(2).setPreferredWidth(450);
        
        JScrollPane scrollPane = new JScrollPane(librarianTable);
        
        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton confirmButton = new JButton("Thêm thủ thư đã chọn");
        confirmButton.setBackground(new Color(46, 204, 113));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(150, 40));
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(100, 40));
        
        // Thêm action listeners
        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        // Thêm các thành phần vào dialog
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Điền dữ liệu vào bảng
        populateTable();
    }
    
    private void populateTable() {
        tableModel.setRowCount(0);
        
        for (Librarian librarian : librarians) {
            String id = String.format("%03d", librarian.getId());
            String name = librarian.getFullName();
            
            // Tạo danh sách tên các vai trò hiện tại của thủ thư
            String currentRoles = librarian.getRoles().stream()
                    .map(Role::getNameRole)
                    .collect(Collectors.joining(", "));
            
            tableModel.addRow(new Object[] {id, name, currentRoles});
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public List<Librarian> getSelectedLibrarians() {
        if (!confirmed) {
            return new ArrayList<>();
        }
        
        List<Librarian> selectedLibrarians = new ArrayList<>();
        int[] selectedRows = librarianTable.getSelectedRows();
        
        for (int row : selectedRows) {
            int id = Integer.parseInt((String) tableModel.getValueAt(row, 0));
            
            // Tìm thủ thư theo ID
            librarians.stream()
                    .filter(lib -> lib.getId() == id)
                    .findFirst()
                    .ifPresent(selectedLibrarians::add);
        }
        
        return selectedLibrarians;
    }
}