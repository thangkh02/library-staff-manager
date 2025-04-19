package cnpm.edu.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class TableHeader extends JLabel {

    public TableHeader(String text) {
        super(text);
        setOpaque(true);
        setBackground(new Color(135, 206, 250));
        setFont(new Font("Arial", Font.BOLD, 13));
        setForeground(Color.WHITE);
        setBorder(new EmptyBorder(10, 5, 10, 5));
        setHorizontalAlignment(JLabel.LEFT);
    }
}