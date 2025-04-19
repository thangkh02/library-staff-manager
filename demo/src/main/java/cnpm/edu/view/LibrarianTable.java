package cnpm.edu.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import cnpm.edu.view.LibrarianTable.CustomTableRenderer;

public class LibrarianTable extends JTable {

    public LibrarianTable(TableModel model) {
        super(model);

      
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setGridColor(new Color(235, 235, 235));
        setRowHeight(38);
        getTableHeader().setReorderingAllowed(false);

        
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        setCellSelectionEnabled(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setSelectionBackground(Color.YELLOW);
        setSelectionForeground(Color.BLACK);

        // Thiết lập header bảng
        getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i,
                    int i1) {
                TableHeader header = new TableHeader(o.toString());

                return header;
            }
        });

        // Thêm sự kiện mouse để đảm bảo click vào cell sẽ chọn toàn bộ hàng
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row >= 0) {
                    setRowSelectionInterval(row, row);
                }
            }
        });

        setDefaultRenderer(Object.class, new CustomTableRenderer());

        if (getColumnCount() >= 8) {
            getColumnModel().getColumn(0).setPreferredWidth(60); // Mã
            getColumnModel().getColumn(1).setPreferredWidth(150); // Họ tên
            getColumnModel().getColumn(2).setPreferredWidth(100); // Lương
            getColumnModel().getColumn(3).setPreferredWidth(100); // Ngày sinh 
            getColumnModel().getColumn(4).setPreferredWidth(80); // Giới tính 
            getColumnModel().getColumn(5).setPreferredWidth(150); // Địa chỉ
            getColumnModel().getColumn(6).setPreferredWidth(110); // Số điện thoại
            getColumnModel().getColumn(7).setPreferredWidth(150); // Email
            setIntercellSpacing(new Dimension(10, 0));
        }
    }

    @Override
    public void changeSelection(int row, int column, boolean toggle, boolean extend) {
        super.changeSelection(row, column, toggle, extend);
        repaint();
    }

    class CustomTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {

            Component comp = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);

            if (row == table.getSelectedRow()) {
                comp.setBackground(new Color(235, 235, 235));
                comp.setFont(new Font("Arial", Font.PLAIN, 13));
                comp.setForeground(new Color(15, 89, 140));

            } else {
                comp.setBackground(new Color(235, 235, 235));
                comp.setBackground(Color.WHITE);
                comp.setForeground(new Color(80, 80, 80));
                setFont(new Font("Arial", Font.PLAIN, 13));
            }
            return comp;
        }
    }
}