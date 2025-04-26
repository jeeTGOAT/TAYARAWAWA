import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Great Plane Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            
            // Create and add the menu page
            MenuPage menuPage = new MenuPage(frame);
            frame.add(menuPage);
            
            frame.setVisible(true);
        });
    }
} 