import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationPanel extends JPanel implements ActionListener, KeyListener {
    private static final Color SKY_COLOR = new Color(25, 25, 50); // Darker blue for better contrast
    private static final Color GROUND_COLOR = new Color(34, 139, 34);
    private static final int REFRESH_RATE = 30;
    private static final int CLOUD_COUNT = 10;
    private static final int STAR_COUNT = 100;
    private static final int RADAR_SWEEP_SPEED = 2; // degrees per frame
    private static final int RADAR_RINGS = 5;
    
    private List<Aircraft> aircraft;
    private List<Cloud> clouds;
    private List<Point> stars;
    private List<Airport> airports;
    private Timer simulationTimer;
    private Point mousePosition;
    private Aircraft selectedAircraft;
    private JPanel controlPanel;
    private JLabel scoreLabel;
    private JLabel statusLabel;
    private int score = 0;
    private Random random = new Random();
    private double radarAngle = 0;
    private long lastUpdateTime;
    
    // Game mechanics
    private double windSpeed = 0;
    private double windDirection = 0;
    private boolean isNight = false;
    private int weatherTimer = 0;
    
    public SimulationPanel() {
        setLayout(new BorderLayout());
        aircraft = new ArrayList<>();
        clouds = new ArrayList<>();
        stars = new ArrayList<>();
        airports = new ArrayList<>();
        
        // Initialize control panels
        createControlPanel();
        createStatusPanel();
        
        // Add mouse listeners
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();
                repaint();
            }
        });
        
        // Add keyboard listener
        addKeyListener(this);
        setFocusable(true);
        
        // Initialize game elements
        initializeGameElements();
        
        // Start simulation timer
        simulationTimer = new Timer(REFRESH_RATE, this);
        simulationTimer.start();
    }
    
    private void initializeGameElements() {
        // Create initial airports
        airports.add(new Airport("JFK", 100, getHeight() - 150));
        airports.add(new Airport("LAX", getWidth() - 100, getHeight() - 150));
        
        // Create stars (will be visible at night)
        for (int i = 0; i < STAR_COUNT; i++) {
            stars.add(new Point(random.nextInt(Math.max(1, getWidth())), 
                              random.nextInt(Math.max(1, (int)(getHeight() * 0.7)))));
        }
        
        // Create initial clouds
        for (int i = 0; i < CLOUD_COUNT; i++) {
            clouds.add(new Cloud(random.nextInt(Math.max(1, getWidth())), 
                               random.nextInt(Math.max(1, (int)(getHeight() * 0.7)))));
        }
        
        // Add some initial aircraft
        addTestAircraft();
    }
    
    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBackground(new Color(0, 0, 0, 150));
        
        JButton addAircraftBtn = createStyledButton("Add Aircraft");
        JButton toggleTimeBtn = createStyledButton("Toggle Day/Night");
        JButton weatherBtn = createStyledButton("Change Weather");
        JButton returnBtn = createStyledButton("Return to Menu");
        
        addAircraftBtn.addActionListener(e -> addRandomAircraft());
        toggleTimeBtn.addActionListener(e -> toggleDayNight());
        weatherBtn.addActionListener(e -> changeWeather());
        returnBtn.addActionListener(e -> returnToMenu());
        
        controlPanel.add(addAircraftBtn);
        controlPanel.add(toggleTimeBtn);
        controlPanel.add(weatherBtn);
        controlPanel.add(returnBtn);
        
        add(controlPanel, BorderLayout.NORTH);
    }
    
    private void createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(0, 0, 0, 150));
        
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.WHITE);
        statusLabel = new JLabel("Weather: Clear");
        statusLabel.setForeground(Color.WHITE);
        
        statusPanel.add(scoreLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(statusLabel);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw sky gradient
        GradientPaint skyGradient;
        if (isNight) {
            skyGradient = new GradientPaint(0, 0, new Color(0, 0, 20),
                                          0, getHeight() * 2/3, new Color(20, 20, 40));
        } else {
            skyGradient = new GradientPaint(0, 0, new Color(135, 206, 235),
                                          0, getHeight() * 2/3, new Color(65, 105, 225));
        }
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw stars if night
        if (isNight) {
            g2d.setColor(Color.WHITE);
            for (Point star : stars) {
                int brightness = random.nextInt(128) + 128;
                g2d.setColor(new Color(brightness, brightness, brightness));
                g2d.fillRect(star.x, star.y, 2, 2);
            }
        }
        
        // Draw radar overlay
        drawRadarOverlay(g2d);
        
        // Draw clouds with transparency
        for (Cloud cloud : clouds) {
            cloud.draw(g2d);
        }
        
        // Draw ground with gradient
        drawGround(g2d);
        
        // Draw airports
        for (Airport airport : airports) {
            airport.draw(g2d);
        }
        
        // Draw aircraft
        for (Aircraft a : aircraft) {
            a.draw(g2d, a == selectedAircraft);
        }
        
        // Draw weather effects
        drawWeatherEffects(g2d);
        
        // Draw compass rose
        drawCompassRose(g2d);
        
        // Draw aircraft details if selected
        if (selectedAircraft != null) {
            int x = 10;
            int textY = 30;
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(x, 10, 200, 100);
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.format("ID: %s", selectedAircraft.getId()), x + 10, textY);
            g2d.drawString(String.format("Altitude: FL%d", (int)(selectedAircraft.getAltitude()/100)), x + 10, textY + 20);
            g2d.drawString(String.format("Speed: %d kts", (int)selectedAircraft.getSpeed()), x + 10, textY + 40);
            g2d.drawString(String.format("Heading: %d°", (int)selectedAircraft.getHeading()), x + 10, textY + 60);
        }
    }
    
    private void drawGround(Graphics2D g2d) {
        int groundY = (int)(getHeight() * 0.8);
        
        // Draw ground gradient
        GradientPaint groundGradient = new GradientPaint(
            0, groundY, new Color(34, 139, 34),
            0, getHeight(), new Color(20, 80, 20)
        );
        g2d.setPaint(groundGradient);
        g2d.fillRect(0, groundY, getWidth(), getHeight() - groundY);
        
        // Draw grid lines
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int x = 0; x < getWidth(); x += 50) {
            g2d.drawLine(x, groundY, x, getHeight());
        }
    }
    
    private void drawWeatherEffects(Graphics2D g2d) {
        // Draw wind indicator
        int centerX = getWidth() - 60;
        int centerY = 60;
        g2d.setColor(Color.WHITE);
        g2d.drawOval(centerX - 20, centerY - 20, 40, 40);
        double windX = Math.cos(Math.toRadians(windDirection)) * windSpeed * 2;
        double windY = Math.sin(Math.toRadians(windDirection)) * windSpeed * 2;
        g2d.drawLine(centerX, centerY, 
                    (int)(centerX + windX * 15), 
                    (int)(centerY + windY * 15));
    }
    
    private void drawRadarOverlay(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int maxRadius = Math.min(getWidth(), getHeight()) / 2;
        
        // Draw radar rings
        g2d.setColor(new Color(0, 255, 0, 30));
        for (int i = 1; i <= RADAR_RINGS; i++) {
            int radius = (maxRadius * i) / RADAR_RINGS;
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
        
        // Draw radar sweep
        double sweepAngle = Math.toRadians(radarAngle);
        int sweepLength = maxRadius;
        int endX = centerX + (int)(Math.cos(sweepAngle) * sweepLength);
        int endY = centerY + (int)(Math.sin(sweepAngle) * sweepLength);
        
        // Create gradient for radar sweep
        GradientPaint sweepGradient = new GradientPaint(
            centerX, centerY, new Color(0, 255, 0, 100),
            endX, endY, new Color(0, 255, 0, 0)
        );
        g2d.setPaint(sweepGradient);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(centerX, centerY, endX, endY);
    }
    
    private void drawCompassRose(Graphics2D g2d) {
        int size = 80;
        int x = getWidth() - size - 20;
        int y = size + 20;
        
        // Draw compass circle
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillOval(x - size/2, y - size/2, size, size);
        g2d.setColor(Color.WHITE);
        g2d.drawOval(x - size/2, y - size/2, size, size);
        
        // Draw cardinal directions
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String[] directions = {"N", "E", "S", "W"};
        int[] angles = {0, 90, 180, 270};
        
        for (int i = 0; i < directions.length; i++) {
            double angle = Math.toRadians(angles[i]);
            int textX = x + (int)(Math.sin(angle) * (size/2 - 15)) - fm.stringWidth(directions[i])/2;
            int textY = y - (int)(Math.cos(angle) * (size/2 - 15)) + fm.getHeight()/2;
            g2d.drawString(directions[i], textX, textY);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
        lastUpdateTime = currentTime;
        
        updateGameState(deltaTime);
        repaint();
    }
    
    private void updateGameState(double deltaTime) {
        // Update radar sweep
        radarAngle = (radarAngle + RADAR_SWEEP_SPEED) % 360;
        
        // Update aircraft positions
        for (Aircraft a : aircraft) {
            a.update(deltaTime, windSpeed, windDirection);
        }
        
        // Apply wind effects to clouds
        for (Cloud cloud : clouds) {
            cloud.update(windSpeed, windDirection);
        }
        
        // Wrap aircraft around screen edges
        for (Aircraft a : aircraft) {
            if (a.getX() < -100) {
                a.setX(getWidth() + 100);
            }
            if (a.getX() > getWidth() + 100) {
                a.setX(-100);
            }
            if (a.getY() < -100) {
                a.setY(getHeight() + 100);
            }
            if (a.getY() > getHeight() + 100) {
                a.setY(-100);
            }
        }
        
        // Update weather
        updateWeather();
    }
    
    private void updateWeather() {
        weatherTimer++;
        if (weatherTimer > 300) { // Change weather every ~10 seconds
            weatherTimer = 0;
            if (random.nextDouble() < 0.3) { // 30% chance of weather change
                changeWeather();
            }
        }
    }
    
    private void toggleDayNight() {
        isNight = !isNight;
        repaint();
    }
    
    private void changeWeather() {
        windSpeed = random.nextDouble() * 20;
        windDirection = random.nextDouble() * 360;
        String weather = String.format("Weather: Wind %.1f kts at %.0f°", windSpeed, windDirection);
        statusLabel.setText(weather);
    }
    
    // KeyListener methods
    @Override
    public void keyPressed(KeyEvent e) {
        if (selectedAircraft != null) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    selectedAircraft.setHeading((selectedAircraft.getHeading() - 5 + 360) % 360);
                    break;
                case KeyEvent.VK_RIGHT:
                    selectedAircraft.setHeading((selectedAircraft.getHeading() + 5) % 360);
                    break;
                case KeyEvent.VK_UP:
                    selectedAircraft.setSpeed(selectedAircraft.getSpeed() + 10);
                    break;
                case KeyEvent.VK_DOWN:
                    selectedAircraft.setSpeed(Math.max(50, selectedAircraft.getSpeed() - 10));
                    break;
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    // Inner classes
    private class Cloud {
        double x, y;
        int size;
        
        Cloud(int x, int y) {
            this.x = x;
            this.y = y;
            this.size = 30 + random.nextInt(40);
        }
        
        void update(double windSpeed, double windDirection) {
            x += Math.cos(Math.toRadians(windDirection)) * windSpeed * 0.05;
            y += Math.sin(Math.toRadians(windDirection)) * windSpeed * 0.05;
        }
        
        void draw(Graphics2D g2d) {
            g2d.setColor(new Color(255, 255, 255, 100));
            for (int i = 0; i < 5; i++) {
                g2d.fillOval((int)x + i * 10, (int)y, size, size/2);
            }
        }
    }
    
    private class Airport {
        String name;
        int x, y;
        
        Airport(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
        
        void draw(Graphics2D g2d) {
            // Draw runway
            g2d.setColor(Color.GRAY);
            g2d.fillRect(x - 40, y - 5, 80, 10);
            
            // Draw runway markings
            g2d.setColor(Color.WHITE);
            for (int i = 0; i < 8; i++) {
                g2d.fillRect(x - 35 + i * 10, y - 2, 5, 4);
            }
            
            // Draw airport name
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(name, x - fm.stringWidth(name)/2, y + 20);
        }
    }
    
    private void addTestAircraft() {
        aircraft.add(new Aircraft("FL001", 100, 100, 270, 200));
        aircraft.add(new Aircraft("FL002", getWidth() - 100, 200, 180, 180));
    }
    
    private void addRandomAircraft() {
        int x = random.nextInt(getWidth());
        int y = random.nextInt((int)(getHeight() * 0.7));
        double heading = random.nextDouble() * 360;
        aircraft.add(new Aircraft("FL" + (aircraft.size() + 1), x, y, heading, 200));
    }
    
    private void handleMouseClick(Point p) {
        // Check if clicked on any aircraft
        for (Aircraft a : aircraft) {
            double dx = a.getX() - p.x;
            double dy = a.getY() - p.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < 30) {  // 30 pixels radius for selection
                selectedAircraft = a;
                return;
            }
        }
        selectedAircraft = null;
        repaint();
    }
    
    private void returnToMenu() {
        simulationTimer.stop();
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new MenuPage(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        }
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Reinitialize elements when the panel size changes
        if (width > 0 && height > 0) {
            initializeGameElements();
        }
    }
} 