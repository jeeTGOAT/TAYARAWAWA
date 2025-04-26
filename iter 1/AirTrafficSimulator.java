import javax.swing.*;
import java.awt.*;

public class AirTrafficSimulator {
    private JFrame frame;
    private MenuPage menuPage;
    private static final String TITLE = "Air Traffic Simulator";
    
    public AirTrafficSimulator() {
        initializeUI();
    }
    
    private void initializeUI() {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create and add menu page with frame reference
        menuPage = new MenuPage(frame);
        frame.add(menuPage);
        
        // Set window properties
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
    }
    
    public void start() {
        // Show the frame on the EDT
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
        });
    }
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        AirTrafficSimulator simulator = new AirTrafficSimulator();
        simulator.start();
    }
} 