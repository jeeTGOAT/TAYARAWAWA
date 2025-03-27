import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BackgroundPanel extends JPanel {
    private BufferedImage backgroundImage;
    private float opacity = 0.8f;

    public BackgroundPanel(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw background image
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        // Add semi-transparent overlay for better text readability
        g2d.setColor(new Color(0, 0, 0, (int)(opacity * 127)));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.dispose();
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0, Math.min(1, opacity));
        repaint();
    }
} 