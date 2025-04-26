import java.awt.*;
import java.awt.geom.*;

public class Aircraft {
    private String id;
    private double x, y;
    private double heading;
    private double speed;
    private double targetAltitude;
    private double currentAltitude;
    private static final int SIZE = 20;
    private Color color = Color.WHITE;
    
    public Aircraft(String id, double x, double y, double heading, double speed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.speed = speed;
        this.targetAltitude = 2000;
        this.currentAltitude = 2000;
    }
    
    public void update(double deltaTime, double windSpeed, double windDirection) {
        // Convert heading to radians
        double headingRad = Math.toRadians(heading);
        
        // Calculate wind effect
        double windRad = Math.toRadians(windDirection);
        double windX = windSpeed * Math.cos(windRad);
        double windY = windSpeed * Math.sin(windRad);
        
        // Update position based on heading and speed
        x += (speed * Math.cos(headingRad) + windX) * deltaTime;
        y -= (speed * Math.sin(headingRad) + windY) * deltaTime;
        
        // Adjust altitude
        if (Math.abs(currentAltitude - targetAltitude) > 1) {
            double altitudeChange = Math.signum(targetAltitude - currentAltitude) * 500 * deltaTime;
            currentAltitude += altitudeChange;
        }
    }
    
    public void draw(Graphics2D g2d, boolean isSelected) {
        // Save the current transform
        AffineTransform oldTransform = g2d.getTransform();
        
        // Translate to aircraft position and rotate
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(-heading));
        
        // Draw selection circle if selected
        if (isSelected) {
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.fillOval(-SIZE * 2, -SIZE * 2, SIZE * 4, SIZE * 4);
        }
        
        // Draw aircraft
        g2d.setColor(color);
        int[] xPoints = {SIZE, -SIZE, -SIZE/2, -SIZE, SIZE};
        int[] yPoints = {0, SIZE/2, 0, -SIZE/2, 0};
        g2d.fillPolygon(xPoints, yPoints, 5);
        
        // Draw ID and altitude
        g2d.setColor(Color.WHITE);
        g2d.rotate(Math.toRadians(heading)); // Rotate text back to normal
        g2d.drawString(id + " FL" + (int)(currentAltitude/100), 10, -10);
        
        // Restore the original transform
        g2d.setTransform(oldTransform);
    }
    
    // Getters and setters
    public double getX() { return x; }
    public double getY() { return y; }
    public String getId() { return id; }
    public double getHeading() { return heading; }
    public double getSpeed() { return speed; }
    public double getAltitude() { return currentAltitude; }
    
    public void setHeading(double heading) {
        this.heading = heading;
        if (this.heading < 0) this.heading += 360;
        if (this.heading >= 360) this.heading -= 360;
    }
    
    public void setSpeed(double speed) {
        this.speed = Math.max(0, Math.min(speed, 400));
    }
    
    public void setTargetAltitude(double altitude) {
        this.targetAltitude = Math.max(0, Math.min(altitude, 40000));
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
} 