package cnpm.edu;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import cnpm.edu.config.DbConfig;
import cnpm.edu.controller.LibrarianController;
import cnpm.edu.model.LibrarianModel;
import cnpm.edu.view.LibrarianView;

public class App {
    public static void main(String[] args) {
        // Thêm đoạn code này vào đầu phương thức main() trong App.java
        try {
            // Sử dụng FlatLaf Look and Feel nếu có thể
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            try {
                // Fallback sang Nimbus nếu không có FlatLaf
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e2) {
                try {
                    // Fallback sang cross-platform look and feel
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }

        // Kiểm tra kết nối đến database
        if (!DbConfig.testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "Không thể kết nối đến cơ sở dữ liệu MySQL.\n" +
                            "Vui lòng kiểm tra:\n" +
                            "1. MySQL server có đang chạy không\n" +
                            "2. CSDL QLTT đã được tạo chưa\n" +
                            "3. Username và password có đúng không",
                    "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            LibrarianModel model = new LibrarianModel();
            LibrarianView view = new LibrarianView();
            new LibrarianController(model, view);
            view.setVisible(true);
        });
    }
}