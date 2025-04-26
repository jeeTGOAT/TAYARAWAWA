import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class Cloud {
    private double x, y;
    private double[] cloudParts;
    private int size;
    private float opacity;
    private static final Random random = new Random();
    private Color cloudColor;
    private double drift;
    
    public Cloud(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 30 + random.nextInt(50);
        this.opacity = 0.6f + random.nextFloat() * 0.3f;
        this.cloudParts = new double[8];
        this.drift = random.nextDouble() * 0.5;
        
        // Generate random cloud shape
        for (int i = 0; i < cloudParts.length; i++) {
            cloudParts[i] = 0.7 + random.nextDouble() * 0.6;
        }
        
        // Set cloud color based on height (lower clouds are darker)
        float brightness = 0.7f + (float)y / 2000;
        brightness = Math.min(1.0f, Math.max(0.7f, brightness));
        cloudColor = new Color(brightness, brightness, brightness, opacity);
    }
    
    public void update(double windSpeed, double windDirection) {
        // Move cloud based on wind
        double windRad = Math.toRadians(windDirection);
        x += Math.cos(windRad) * windSpeed * 0.1 * drift;
        y += Math.sin(windRad) * windSpeed * 0.1 * drift;
        
        // Slowly vary opacity for more dynamic look
        opacity += (random.nextFloat() - 0.5f) * 0.01f;
        opacity = Math.min(0.9f, Math.max(0.4f, opacity));
        cloudColor = new Color(cloudColor.getRed()/255f, 
                             cloudColor.getGreen()/255f, 
                             cloudColor.getBlue()/255f, 
                             opacity);
    }
    
    public void draw(Graphics2D g2d) {
        // Save original transform and composite
        Composite originalComposite = g2d.getComposite();
        AffineTransform originalTransform = g2d.getTransform();
        
        // Set cloud rendering properties
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        
        // Create cloud shape
        Path2D cloudShape = new Path2D.Double();
        double angle = 0;
        double angleStep = 2 * Math.PI / cloudParts.length;
        
        // Draw main cloud body
        for (int i = 0; i < cloudParts.length; i++) {
            double radius = size * cloudParts[i];
            double px = x + Math.cos(angle) * radius * 0.5;
            double py = y + Math.sin(angle) * radius * 0.3;
            
            if (i == 0) {
                cloudShape.moveTo(px, py);
            } else {
                cloudShape.curveTo(
                    x + Math.cos(angle - angleStep/2) * radius * 0.6,
                    y + Math.sin(angle - angleStep/2) * radius * 0.4,
                    x + Math.cos(angle) * radius * 0.6,
                    y + Math.sin(angle) * radius * 0.4,
                    px, py
                );
            }
            angle += angleStep;
        }
        cloudShape.closePath();
        
        // Draw cloud with gradient
        GradientPaint gradient = new GradientPaint(
            (float)x, (float)(y - size/2), new Color(1f, 1f, 1f, opacity),
            (float)x, (float)(y + size/2), new Color(0.8f, 0.8f, 0.8f, opacity * 0.7f)
        );
        g2d.setPaint(gradient);
        g2d.fill(cloudShape);
        
        // Add some detail clouds
        for (int i = 0; i < 3; i++) {
            double detailX = x + (random.nextDouble() - 0.5) * size;
            double detailY = y + (random.nextDouble() - 0.5) * size * 0.3;
            double detailSize = size * (0.4 + random.nextDouble() * 0.3);
            
            g2d.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 
                opacity * 0.7f
            ));
            g2d.fillOval(
                (int)(detailX - detailSize/2),
                (int)(detailY - detailSize/2),
                (int)detailSize,
                (int)(detailSize * 0.6)
            );
        }
        
        // Restore original graphics state
        g2d.setComposite(originalComposite);
        g2d.setTransform(originalTransform);
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
} 