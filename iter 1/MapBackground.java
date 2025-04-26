import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;

public class MapBackground {
    private int width, height;
    private Color waterColor = new Color(10, 20, 40);
    private Color landColor = new Color(20, 40, 20);
    private Color gridColor = new Color(0, 255, 0, 30);
    private BasicStroke gridStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
                                                    10.0f, new float[]{5.0f}, 0.0f);
    
    public MapBackground(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void draw(Graphics2D g2d) {
        // Draw base water
        g2d.setColor(waterColor);
        g2d.fillRect(0, 0, width, height);
        
        // Draw terrain features
        drawTerrain(g2d);
        
        // Draw grid overlay
        drawGrid(g2d);
        
        // Draw range rings
        drawRangeRings(g2d);
    }
    
    private void drawTerrain(Graphics2D g2d) {
        // Create some landmasses
        g2d.setColor(landColor);
        
        // Main continent
        Path2D continent = new Path2D.Double();
        continent.moveTo(width * 0.1, height * 0.3);
        continent.curveTo(width * 0.2, height * 0.4, 
                         width * 0.4, height * 0.35, 
                         width * 0.6, height * 0.45);
        continent.curveTo(width * 0.7, height * 0.5,
                         width * 0.8, height * 0.6,
                         width * 0.9, height * 0.55);
        continent.lineTo(width * 0.9, height);
        continent.lineTo(width * 0.1, height);
        continent.closePath();
        
        // Add some noise to make it look more natural
        g2d.fill(continent);
        
        // Add some islands
        for (int i = 0; i < 5; i++) {
            double x = width * (0.2 + Math.random() * 0.6);
            double y = height * (0.1 + Math.random() * 0.3);
            double size = 20 + Math.random() * 40;
            
            Path2D island = new Path2D.Double();
            island.moveTo(x, y);
            for (int j = 0; j < 8; j++) {
                double angle = j * Math.PI / 4;
                double radius = size * (0.8 + Math.random() * 0.4);
                island.lineTo(x + Math.cos(angle) * radius,
                            y + Math.sin(angle) * radius);
            }
            island.closePath();
            g2d.fill(island);
        }
    }
    
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(gridColor);
        g2d.setStroke(gridStroke);
        
        // Draw vertical lines
        for (int x = 0; x < width; x += 50) {
            g2d.drawLine(x, 0, x, height);
        }
        
        // Draw horizontal lines
        for (int y = 0; y < height; y += 50) {
            g2d.drawLine(0, y, width, y);
        }
    }
    
    private void drawRangeRings(Graphics2D g2d) {
        int centerX = width / 2;
        int centerY = height / 2;
        int maxRadius = Math.min(width, height) / 2;
        
        g2d.setColor(new Color(0, 255, 0, 20));
        g2d.setStroke(new BasicStroke(1.0f));
        
        // Draw range rings
        for (int i = 1; i <= 5; i++) {
            int radius = (maxRadius * i) / 5;
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            
            // Draw distance label
            String distance = (i * 50) + "NM";
            g2d.drawString(distance, centerX + radius - 30, centerY);
        }
    }
    
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
} 