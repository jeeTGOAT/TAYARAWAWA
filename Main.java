import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Système de Gestion Aérienne");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);
            
            MainMenu mainMenu = new MainMenu(frame);
            frame.setContentPane(mainMenu);
            frame.setVisible(true);
        });
    }
} 