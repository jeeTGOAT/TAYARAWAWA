import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.URL;
import javax.swing.border.TitledBorder;

public class FlightSimulation extends JFrame {
    private JFrame parentFrame;
    private BufferedImage worldMap;
    private java.util.List<Flight> activeFlights = new ArrayList<>();
    private Map<String, Flight> flights = new HashMap<>();
    private Map<String, Color> flightColors = new HashMap<>();
    private javax.swing.Timer animationTimer;
    private JPanel controlPanel;
    private JPanel simulationPanel;
    private JLabel timeLabel;
    private LocalDateTime simulationTime;
    private double simulationSpeed = 1.0;
    private Point2D.Double mapOffset = new Point2D.Double(0, 0);
    private double zoomLevel = 1.0;
    private Point lastMousePos;
    private boolean isDragging = false;
    private Random random = new Random();
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JButton backButton;
    private JSlider speedSlider;
    private JCheckBox showTrailsCheckBox;
    private JCheckBox showLabelsCheckBox;
    private JCheckBox showAltitudeCheckBox;
    private JCheckBox showSpeedCheckBox;
    private boolean isRunning = false;
    private javax.swing.Timer simulationTimer;

    // Coordonnées des principaux aéroports
    private HashMap<String, Point2D.Double> airports = new HashMap<>();

    public FlightSimulation(JFrame parent) {
        super("Simulation de Vol");
        this.parentFrame = parent;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        simulationTime = LocalDateTime.now();

        // Create simulation panel
        simulationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Draw world map
                if (worldMap != null) {
                    g2d.drawImage(worldMap, 0, 0, getWidth(), getHeight(), this);
                }

                // Draw airports
                g2d.setColor(Color.RED);
                for (Map.Entry<String, Point2D.Double> entry : airports.entrySet()) {
                    Point2D.Double pos = entry.getValue();
                    int x = (int) (pos.x * getWidth());
                    int y = (int) (pos.y * getHeight());
                    g2d.fillOval(x - 3, y - 3, 6, 6);
                    g2d.drawString(entry.getKey(), x + 5, y - 5);
                }

                // Draw flights
                g2d.setColor(Color.BLUE);
                for (Flight flight : activeFlights) {
                    Point2D.Double pos = flight.getPosition();
                    int x = (int) (pos.x * getWidth());
                    int y = (int) (pos.y * getHeight());
                    g2d.fillOval(x - 4, y - 4, 8, 8);
                    g2d.drawString(flight.getFlightNumber(), x + 5, y - 5);
                    
                    // Draw flight path
                    Point2D.Double start = flight.getStart();
                    Point2D.Double end = flight.getEnd();
                    g2d.setColor(new Color(100, 149, 237, 128));
                    g2d.drawLine(
                        (int)(start.x * getWidth()),
                        (int)(start.y * getHeight()),
                        (int)(end.x * getWidth()),
                        (int)(end.y * getHeight())
                    );
                }
            }
        };
        add(simulationPanel, BorderLayout.CENTER);

        // Create control panel
        controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.EAST);

        // Load world map
        try {
            worldMap = ImageIO.read(new File("resources/images/world_map.png"));
        } catch (IOException e) {
            System.out.println("World map image not found, creating default map.");
            worldMap = createDefaultWorldMap();
        }

        // Initialize airports
        initializeAirports();

        // Add mouse listeners for map interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
                isDragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    double dx = e.getX() - lastMousePos.x;
                    double dy = e.getY() - lastMousePos.y;
                    mapOffset.x += dx / zoomLevel;
                    mapOffset.y += dy / zoomLevel;
                    lastMousePos = e.getPoint();
                    repaint();
                }
            }
        });

        addMouseWheelListener(e -> {
            double oldZoom = zoomLevel;
            if (e.getWheelRotation() < 0) {
                zoomLevel *= 1.1; // Zoom in
            } else {
                zoomLevel /= 1.1; // Zoom out
            }
            // Limit zoom
            zoomLevel = Math.max(0.1, Math.min(5.0, zoomLevel));
            
            // Adjust offset to zoom towards mouse position
            if (zoomLevel != oldZoom) {
                Point mouse = e.getPoint();
                mapOffset.x = mouse.x - (int)((mouse.x - mapOffset.x) * (zoomLevel / oldZoom));
                mapOffset.y = mouse.y - (int)((mouse.y - mapOffset.y) * (zoomLevel / oldZoom));
                repaint();
            }
        });

        // Timer pour l'animation
        animationTimer = new javax.swing.Timer(50, e -> {
            updateFlights();
            repaint();
        });
        animationTimer.start();

        // Timer pour la mise à jour du temps
        javax.swing.Timer timeTimer = new javax.swing.Timer(1000, e -> {
            simulationTime = simulationTime.plusSeconds((long)(simulationSpeed));
            updateTimeLabel();
        });
        timeTimer.start();

        // Ajouter quelques vols initiaux
        addInitialFlights();

        // Start simulation timer
        startSimulationTimer();
    }

    private void initializeAirports() {
        airports = new HashMap<>();
        // Add some major airports with their relative positions
        airports.put("CDG", new Point2D.Double(0.45, 0.35)); // Paris
        airports.put("JFK", new Point2D.Double(0.25, 0.35)); // New York
        airports.put("LAX", new Point2D.Double(0.15, 0.4));  // Los Angeles
        airports.put("DXB", new Point2D.Double(0.6, 0.45));  // Dubai
        airports.put("HND", new Point2D.Double(0.8, 0.4));   // Tokyo
        airports.put("SYD", new Point2D.Double(0.85, 0.7));  // Sydney
        airports.put("GRU", new Point2D.Double(0.3, 0.65));  // São Paulo
        airports.put("CPT", new Point2D.Double(0.5, 0.7));   // Cape Town
    }

    private void addAirport(String code, double lat, double lon) {
        // Convertir lat/lon en coordonnées x,y pour la carte
        double x = (lon + 180) * (worldMap.getWidth() / 360.0);
        double y = (90 - lat) * (worldMap.getHeight() / 180.0);
        airports.put(code, new Point2D.Double(x, y));
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(new Color(44, 62, 80));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton addFlightButton = new JButton("Ajouter un Vol");
        styleButton(addFlightButton);
        addFlightButton.addActionListener(e -> showAddFlightDialog());
        
        JButton clearButton = new JButton("Effacer Tous");
        styleButton(clearButton);
        clearButton.addActionListener(e -> clearAllFlights());
        
        buttonPanel.add(addFlightButton);
        buttonPanel.add(clearButton);
        
        // Speed control panel
        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        speedPanel.setOpaque(false);
        
        JLabel speedLabel = new JLabel("Vitesse:");
        speedLabel.setForeground(Color.WHITE);
        
        // Initialize speedSlider first to avoid null pointer exception
        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 5);
        speedSlider.setOpaque(false);
        speedSlider.setPreferredSize(new Dimension(150, 30));
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintLabels(true);
        speedSlider.setForeground(Color.WHITE);
        
        speedSlider.addChangeListener(e -> updateSimulationSpeed());
        
        speedPanel.add(speedLabel);
        speedPanel.add(speedSlider);
        
        // Display options panel
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        optionsPanel.setOpaque(false);
        
        showTrailsCheckBox = new JCheckBox("Trails");
        showTrailsCheckBox.setForeground(Color.WHITE);
        showTrailsCheckBox.setOpaque(false);
        showTrailsCheckBox.addActionListener(e -> repaint());
        
        showLabelsCheckBox = new JCheckBox("Labels");
        showLabelsCheckBox.setForeground(Color.WHITE);
        showLabelsCheckBox.setOpaque(false);
        showLabelsCheckBox.setSelected(true);
        showLabelsCheckBox.addActionListener(e -> repaint());
        
        showAltitudeCheckBox = new JCheckBox("Altitude");
        showAltitudeCheckBox.setForeground(Color.WHITE);
        showAltitudeCheckBox.setOpaque(false);
        showAltitudeCheckBox.addActionListener(e -> repaint());
        
        showSpeedCheckBox = new JCheckBox("Vitesse");
        showSpeedCheckBox.setForeground(Color.WHITE);
        showSpeedCheckBox.setOpaque(false);
        showSpeedCheckBox.addActionListener(e -> repaint());
        
        optionsPanel.add(showTrailsCheckBox);
        optionsPanel.add(showLabelsCheckBox);
        optionsPanel.add(showAltitudeCheckBox);
        optionsPanel.add(showSpeedCheckBox);
        
        controlPanel.add(buttonPanel, BorderLayout.WEST);
        controlPanel.add(speedPanel, BorderLayout.CENTER);
        controlPanel.add(optionsPanel, BorderLayout.EAST);
        
        add(controlPanel, BorderLayout.SOUTH);
        
        return controlPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(180, 35));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(brighter(color));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setBackground(new Color(30, 30, 35));
        checkBox.setForeground(Color.WHITE);
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBox.setFocusPainted(false);
        return checkBox;
    }

    private Color brighter(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.min(1.0f, hsb[2] * 1.2f));
    }

    private void addInitialFlights() {
        String[] routes = {
            // Routes transatlantiques
            "CDG-JFK", "LHR-JFK", "FRA-JFK",  // Europe vers New York
            "CDG-LAX", "LHR-LAX",             // Europe vers Los Angeles
            
            // Routes Europe-Asie
            "CDG-PEK", "LHR-PEK", "FRA-PEK",  // Europe vers Pékin
            "CDG-HND", "LHR-HND", "FRA-HND",  // Europe vers Tokyo
            
            // Routes Moyen-Orient
            "DXB-LHR", "DXB-CDG", "DXB-JFK",  // Dubai vers l'Europe/USA
            
            // Routes Pacifique
            "LAX-HND", "LAX-SYD",             // USA vers Asie/Océanie
            "HND-SYD", "PEK-SYD",             // Asie vers Océanie
            
            // Routes Amérique du Sud
            "GRU-JFK", "GRU-CDG", "GRU-FRA",  // São Paulo vers USA/Europe
            
            // Routes Afrique
            "JNB-DXB", "JNB-LHR", "JNB-CDG"   // Johannesburg vers Dubai/Europe
        };

        for (String route : routes) {
            String[] airports = route.split("-");
            addFlight(airports[0], airports[1], 10000); // Altitude par défaut de 10000 pieds
        }
    }

    private void addFlight(String departure, String arrival, int altitude) {
        if (airports.containsKey(departure) && airports.containsKey(arrival)) {
            Flight flight = new Flight(departure, arrival, altitude);
            flight.position = airports.get(departure);
            flight.destination = airports.get(arrival);
            activeFlights.add(flight);
        }
    }

    private void removeFlight(String flightNumber) {
        Flight flight = flights.get(flightNumber);
        if (flight != null) {
            activeFlights.remove(flight);
            flights.remove(flightNumber);
            flightColors.remove(flightNumber);
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Vol non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTimeLabel() {
        if (timeLabel != null) {
            timeLabel.setText(simulationTime.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            ));
        }
    }

    private void updateFlights() {
        for (Flight flight : activeFlights) {
            flight.update();
        }
    }

    private void startSimulation() {
        animationTimer.start();
    }

    private void pauseSimulation() {
        animationTimer.stop();
    }

    private void resetSimulation() {
        activeFlights.clear();
        flights.clear();
        flightColors.clear();
        addInitialFlights();
        simulationTime = LocalDateTime.now();
        mapOffset = new Point2D.Double(0, 0);
        zoomLevel = 1.0;
        repaint();
    }

    private BufferedImage createPlaceholderMap() {
        BufferedImage placeholder = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setColor(new Color(200, 200, 255)); // Light blue color
        g2d.fillRect(0, 0, 1920, 1080);
        g2d.setColor(Color.GRAY);
        g2d.drawString("Carte non disponible", 50, 50);
        g2d.dispose();
        return placeholder;
    }

    private void styleSlider(JSlider slider) {
        slider.setBackground(new Color(30, 30, 35));
        slider.setForeground(Color.WHITE);
        slider.setMajorTickSpacing(100);
        slider.setMinorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBackground(new Color(30, 30, 35));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65)),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setBackground(new Color(45, 45, 50));
        scrollBar.setForeground(Color.WHITE);
        scrollBar.setBorder(BorderFactory.createEmptyBorder());
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(30, 30, 35));
        panel.setForeground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                title,
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(200, 200, 200)
            ),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return panel;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setBackground(new Color(45, 45, 50));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65)),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        return textField;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private void updateSimulationSpeed() {
        simulationSpeed = speedSlider.getValue() / 100.0;
    }

    private void showModifyFlightDialog(String flightNumber) {
        Flight flight = flights.get(flightNumber);
        if (flight == null) {
            JOptionPane.showMessageDialog(this, "Vol non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Modifier le Vol " + flightNumber, true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Champs de modification
        JTextField newDepartureField = createStyledTextField(4);
        newDepartureField.setText(flight.getDeparture());
        JTextField newArrivalField = createStyledTextField(4);
        newArrivalField.setText(flight.getArrival());
        JTextField newAltitudeField = createStyledTextField(5);
        newAltitudeField.setText(String.valueOf(flight.getAltitude()));
        JTextField newSpeedField = createStyledTextField(5);
        newSpeedField.setText(String.valueOf(flight.getCruisingSpeed()));

        // Ajout des composants
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(createStyledLabel("Nouveau Départ:"), gbc);
        gbc.gridx = 1;
        dialog.add(newDepartureField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(createStyledLabel("Nouvelle Arrivée:"), gbc);
        gbc.gridx = 1;
        dialog.add(newArrivalField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(createStyledLabel("Nouvelle Altitude (pieds):"), gbc);
        gbc.gridx = 1;
        dialog.add(newAltitudeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(createStyledLabel("Nouvelle Vitesse (km/h):"), gbc);
        gbc.gridx = 1;
        dialog.add(newSpeedField, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton applyButton = createStyledButton("Appliquer", new Color(46, 204, 113));
        JButton cancelButton = createStyledButton("Annuler", new Color(231, 76, 60));

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        // Gestion des événements
        applyButton.addActionListener(e -> {
            try {
                String newDeparture = newDepartureField.getText().toUpperCase();
                String newArrival = newArrivalField.getText().toUpperCase();
                int newAltitude = Integer.parseInt(newAltitudeField.getText());
                double newSpeed = Double.parseDouble(newSpeedField.getText());

                if (airports.containsKey(newDeparture) && airports.containsKey(newArrival)) {
                    flight.setDeparture(newDeparture);
                    flight.setArrival(newArrival);
                    flight.setAltitude(newAltitude);
                    flight.setCruisingSpeed(newSpeed);
                    flight.updateSpeed();
                    dialog.dispose();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Aéroport non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Valeurs numériques invalides", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        // Centrer la fenêtre et l'afficher
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddFlightDialog() {
        String departure = JOptionPane.showInputDialog(this, "Code de l'aéroport de départ:");
        if (departure != null) {
            String arrival = JOptionPane.showInputDialog(this, "Code de l'aéroport d'arrivée:");
            if (arrival != null) {
                String altitudeStr = JOptionPane.showInputDialog(this, "Altitude (en pieds):");
                if (altitudeStr != null) {
                    try {
                        int altitude = Integer.parseInt(altitudeStr);
                        addFlight(departure.toUpperCase(), arrival.toUpperCase(), altitude);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Altitude invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    // Classe interne pour représenter un vol
    public static class Flight {
        private String flightNumber;
        private String departure;
        private String arrival;
        private int altitude;
        private Point2D.Double position;
        private Point2D.Double destination;
        private double speed;

        public Flight(String departure, String arrival, int altitude) {
            this.flightNumber = "FL" + (int)(Math.random() * 1000);
            this.departure = departure;
            this.arrival = arrival;
            this.altitude = altitude;
            this.speed = 0.001 + Math.random() * 0.002;
            this.position = new Point2D.Double(0.5, 0.5); // Start at center for now
            this.destination = new Point2D.Double(0.7, 0.3); // Random destination for now
        }

        public String getFlightNumber() { return flightNumber; }
        public Point2D.Double getPosition() { return position; }
        public void update() {
            double dx = destination.x - position.x;
            double dy = destination.y - position.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance > speed) {
                position.x += (dx / distance) * speed;
                position.y += (dy / distance) * speed;
            }
        }

        public String getDeparture() {
            return departure;
        }

        public String getArrival() {
            return arrival;
        }

        public int getAltitude() {
            return altitude;
        }

        public double getCruisingSpeed() {
            return speed;
        }

        public void setDeparture(String departure) {
            this.departure = departure;
        }

        public void setArrival(String arrival) {
            this.arrival = arrival;
        }

        public void setAltitude(int altitude) {
            this.altitude = altitude;
        }

        public void setCruisingSpeed(double speed) {
            this.speed = speed;
        }

        public Point2D.Double getStart() {
            return position;
        }

        public Point2D.Double getEnd() {
            return destination;
        }

        public String getEstimatedTime() {
            // Implementation of getEstimatedTime method
            return "Estimated Time";
        }

        public void updateSpeed() {
            // Implementation of updateSpeed method
        }

        public double getProgress() {
            Point2D.Double start = getStart();
            Point2D.Double end = getEnd();
            Point2D.Double current = getPosition();
            
            double totalDistance = Math.sqrt(
                Math.pow(end.x - start.x, 2) + 
                Math.pow(end.y - start.y, 2)
            );
            
            double currentDistance = Math.sqrt(
                Math.pow(current.x - start.x, 2) + 
                Math.pow(current.y - start.y, 2)
            );
            
            return Math.min(1.0, currentDistance / totalDistance);
        }
    }

    private void startSimulationTimer() {
        simulationTimer = new javax.swing.Timer(50, e -> updateFlightInfo());
        simulationTimer.start();
    }

    private void updateFlightInfo() {
        for (Flight flight : activeFlights) {
            flight.update();
        }
        simulationPanel.repaint();
    }

    private BufferedImage createDefaultWorldMap() {
        BufferedImage map = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = map.createGraphics();
        
        // Draw dark blue background
        g2d.setColor(new Color(10, 20, 40));
        g2d.fillRect(0, 0, 1920, 1080);
        
        // Draw grid lines
        g2d.setColor(new Color(30, 40, 60));
        for (int i = 0; i < 1920; i += 50) {
            g2d.drawLine(i, 0, i, 1080);
        }
        for (int i = 0; i < 1080; i += 50) {
            g2d.drawLine(0, i, 1920, i);
        }
        
        // Draw continents outline (simplified)
        g2d.setColor(new Color(40, 50, 70));
        g2d.fillOval(200, 200, 400, 300);  // North America
        g2d.fillOval(700, 300, 300, 400);  // Europe
        g2d.fillOval(900, 200, 500, 400);  // Asia
        g2d.fillOval(300, 600, 300, 300);  // South America
        g2d.fillOval(800, 600, 400, 300);  // Africa
        g2d.fillOval(1200, 700, 300, 200); // Australia
        
        g2d.dispose();
        return map;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void clearAllFlights() {
        flights.clear();
        repaint();
    }

    private void paintFlight(Graphics2D g2d, Flight flight, boolean selected) {
        int x = (int) (flight.position.x * getWidth());
        int y = (int) (flight.position.y * getHeight());
        
        // Draw flight path/trail if enabled
        if (showTrailsCheckBox.isSelected() && flight.path.size() > 1) {
            g2d.setColor(new Color(flight.color.getRed(), flight.color.getGreen(), flight.color.getBlue(), 100));
            g2d.setStroke(new BasicStroke(1.5f));
            
            java.awt.geom.Path2D.Double path = new java.awt.geom.Path2D.Double();
            boolean first = true;
            
            for (Point p : flight.path) {
                int pathX = (int) (p.x * getWidth());
                int pathY = (int) (p.y * getHeight());
                
                if (first) {
                    path.moveTo(pathX, pathY);
                    first = false;
                } else {
                    path.lineTo(pathX, pathY);
                }
            }
            
            g2d.draw(path);
        }
        
        // Draw the aircraft
        int size = selected ? 10 : 8;
        g2d.setColor(flight.color);
        g2d.fillOval(x - size/2, y - size/2, size, size);
        
        // Draw direction indicator
        int dirX = (int) (Math.cos(Math.toRadians(flight.heading)) * size);
        int dirY = (int) (Math.sin(Math.toRadians(flight.heading)) * size);
        g2d.drawLine(x, y, x + dirX, y + dirY);
        
        // Draw labels if enabled
        if (showLabelsCheckBox.isSelected()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.setColor(Color.WHITE);
            g2d.drawString(flight.flightNumber, x + 10, y);
        }
        
        // Draw additional info if enabled
        int textY = y + 15;
        
        if (showAltitudeCheckBox.isSelected()) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString("ALT: " + flight.altitude + " ft", x + 10, textY);
            textY += 12;
        }
        
        if (showSpeedCheckBox.isSelected()) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString("SPD: " + flight.speed + " kts", x + 10, textY);
        }
    }
} 