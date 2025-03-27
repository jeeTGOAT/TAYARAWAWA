import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class CustomUIManager {
    private static final Color PRIMARY_COLOR = new Color(0, 122, 204);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

    public static void setupLookAndFeel() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    public static JPanel createStyledPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image img = ImageIO.read(new File("src/resources/images/avion-800_1.png"));
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    public static JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(REGULAR_FONT);
        table.getTableHeader().setFont(TITLE_FONT);
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(SECONDARY_COLOR);
        return table;
    }

    public static void styleDialog(JDialog dialog) {
        dialog.getRootPane().setBackground(SECONDARY_COLOR);
        for (Component comp : dialog.getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                stylePanel((JPanel) comp);
            }
        }
    }

    private static void stylePanel(JPanel panel) {
        panel.setBackground(SECONDARY_COLOR);
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setFont(REGULAR_FONT);
            } else if (comp instanceof JTextField) {
                comp.setFont(REGULAR_FONT);
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(Color.WHITE);
                button.setFont(REGULAR_FONT);
                button.setFocusPainted(false);
            }
        }
    }
} 