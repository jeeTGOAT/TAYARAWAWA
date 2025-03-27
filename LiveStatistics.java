import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LiveStatistics extends JFrame {
    private JFrame parentFrame;
    private JPanel mainPanel;
    private JPanel statsPanel;
    private JPanel chartPanel;
    private JLabel timeLabel;
    private LocalDateTime currentTime;
    private javax.swing.Timer updateTimer;
    private java.util.List<Flight> flights;
    private Random random;

    public LiveStatistics(JFrame parent) {
        super("Statistiques en Temps Réel");
        this.parentFrame = parent;
        this.flights = new ArrayList<>();
        this.random = new Random();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        currentTime = LocalDateTime.now();

        // Create main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 35));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create stats panel
        statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.NORTH);

        // Create chart panel
        chartPanel = createChartPanel();
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        // Create control panel
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.EAST);

        // Create status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(30, 30, 35));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        timeLabel = new JLabel("Temps: " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeLabel.setForeground(Color.WHITE);
        statusPanel.add(timeLabel, BorderLayout.EAST);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Add some test flights
        addTestFlights();

        // Start update timer
        updateTimer = new javax.swing.Timer(1000, e -> {
            currentTime = currentTime.plusSeconds(1);
            updateTimeLabel();
            updateStats();
        });
        updateTimer.start();
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(new Color(30, 30, 35));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add stat cards
        panel.add(createStatCard("Vols Actifs", "0"));
        panel.add(createStatCard("Vols en Attente", "0"));
        panel.add(createStatCard("Vols Terminés", "0"));
        panel.add(createStatCard("Taux d'Occupation", "0%"));

        return panel;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(45, 45, 50));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(200, 200, 200));
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));

        card.add(titleLabel);
        card.add(valueLabel);

        return card;
    }

    private JPanel createChartPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw chart background
                g2d.setColor(new Color(45, 45, 50));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw chart grid
                g2d.setColor(new Color(60, 60, 65));
                for (int i = 0; i < getWidth(); i += 50) {
                    g2d.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 50) {
                    g2d.drawLine(0, i, getWidth(), i);
                }

                // Draw chart data
                int padding = 50;
                int width = getWidth() - 2 * padding;
                int height = getHeight() - 2 * padding;

                // Draw axes
                g2d.setColor(Color.WHITE);
                g2d.drawLine(padding, padding, padding, height + padding);
                g2d.drawLine(padding, height + padding, width + padding, height + padding);

                // Draw time labels
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                for (int i = 0; i <= 6; i++) {
                    int x = padding + (width * i) / 6;
                    LocalDateTime time = currentTime.minusHours(6).plusHours(i);
                    String label = time.format(DateTimeFormatter.ofPattern("HH:mm"));
                    g2d.drawString(label, x - 20, height + padding + 20);
                }

                // Draw flight data
                g2d.setColor(new Color(52, 152, 219));
                g2d.setStroke(new BasicStroke(2));
                
                // Draw active flights line
                Path2D.Double path = new Path2D.Double();
                boolean first = true;
                
                for (int i = 0; i <= 6; i++) {
                    int x = padding + (width * i) / 6;
                    int y = height + padding - (int)(random.nextDouble() * height);
                    
                    if (first) {
                        path.moveTo(x, y);
                        first = false;
                    } else {
                        path.lineTo(x, y);
                    }
                }
                
                g2d.draw(path);

                // Draw data points
                for (int i = 0; i <= 6; i++) {
                    int x = padding + (width * i) / 6;
                    int y = height + padding - (int)(random.nextDouble() * height);
                    g2d.fillOval(x - 3, y - 3, 6, 6);
                }
            }
        };
        panel.setBackground(new Color(45, 45, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 35));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add chart controls
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> updateStats());
        panel.add(refreshButton);

        // Add return button
        JButton returnButton = new JButton("Retour au Menu");
        returnButton.addActionListener(e -> {
            dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });
        panel.add(returnButton);

        return panel;
    }

    private void updateTimeLabel() {
        timeLabel.setText("Temps: " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private void updateStats() {
        // Update stat cards with random data
        int activeFlights = random.nextInt(20) + 10;
        int waitingFlights = random.nextInt(5);
        int completedFlights = random.nextInt(50) + 100;
        int occupancyRate = random.nextInt(30) + 70;

        // Update the stat cards
        Component[] components = statsPanel.getComponents();
        ((JLabel)((JPanel)components[0]).getComponent(1)).setText(String.valueOf(activeFlights));
        ((JLabel)((JPanel)components[1]).getComponent(1)).setText(String.valueOf(waitingFlights));
        ((JLabel)((JPanel)components[2]).getComponent(1)).setText(String.valueOf(completedFlights));
        ((JLabel)((JPanel)components[3]).getComponent(1)).setText(occupancyRate + "%");

        // Repaint the chart
        chartPanel.repaint();
    }

    private void addTestFlights() {
        // Add some test flights with random data
        for (int i = 0; i < 10; i++) {
            Flight flight = new Flight(
                "FL" + (1000 + i),
                "CDG",
                "JFK",
                35000,
                LocalDateTime.now().minusHours(random.nextInt(24)),
                "En cours"
            );
            flights.add(flight);
        }
    }

    // Inner class to represent a flight
    private static class Flight {
        private String flightNumber;
        private String departure;
        private String arrival;
        private int altitude;
        private LocalDateTime dateTime;
        private String status;

        public Flight(String flightNumber, String departure, String arrival, 
                     int altitude, LocalDateTime dateTime, String status) {
            this.flightNumber = flightNumber;
            this.departure = departure;
            this.arrival = arrival;
            this.altitude = altitude;
            this.dateTime = dateTime;
            this.status = status;
        }

        public String getFlightNumber() { return flightNumber; }
        public String getDeparture() { return departure; }
        public String getArrival() { return arrival; }
        public int getAltitude() { return altitude; }
        public LocalDateTime getDateTime() { return dateTime; }
        public String getStatus() { return status; }
    }
} 