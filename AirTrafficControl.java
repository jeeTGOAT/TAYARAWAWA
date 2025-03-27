import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.border.TitledBorder;

public class AirTrafficControl extends JFrame {
    private JFrame parentFrame;
    private Set<ControlledFlight> activeFlights;
    private javax.swing.Timer updateTimer;
    private Point2D.Double radarCenter;
    private double radarRadius;
    private double rotation = 0;
    private JPanel controlPanel;
    private JTextArea flightInfoArea;
    private Map<String, Color> flightColors;
    private Random random;
    private double radarRotationSpeed = 2;
    private double radarRange = 500;
    private JPanel radarPanel;

    public AirTrafficControl(JFrame parent) {
        super("Contrôle du Trafic Aérien");
        this.parentFrame = parent;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        activeFlights = new HashSet<>();
        flightColors = new HashMap<>();
        random = new Random();

        // Initialisation du radar
        radarCenter = new Point2D.Double(400, 400);
        radarRadius = 350;

        // Création des panneaux
        createControlPanel();
        createInfoPanel();

        // Ajout du bouton retour au menu
        JButton backButton = new JButton("Retour au Menu");
        backButton.addActionListener(e -> {
            updateTimer.stop();
            dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });
        JPanel backPanel = new JPanel();
        backPanel.add(backButton);
        add(backPanel, BorderLayout.NORTH);

        // Timer pour la mise à jour du radar
        updateTimer = new javax.swing.Timer(50, e -> {
            rotation = (rotation + 0.02) % (2 * Math.PI);
            updateFlights();
            repaint();
        });
        updateTimer.start();

        // Ajout des interactions souris
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });

        // Ajout de quelques vols de test
        addTestFlights();

        // Create radar panel
        radarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw radar background
                g2d.setColor(new Color(0, 20, 0));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw radar grid
                g2d.setColor(new Color(0, 100, 0, 128));
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int maxRadius = Math.min(centerX, centerY);
                
                // Draw concentric circles
                for (int r = maxRadius / 4; r <= maxRadius; r += maxRadius / 4) {
                    g2d.drawOval(centerX - r, centerY - r, r * 2, r * 2);
                }
                
                // Draw crosshairs
                g2d.drawLine(centerX, 0, centerX, getHeight());
                g2d.drawLine(0, centerY, getWidth(), centerY);
                
                // Draw active flights
                g2d.setColor(Color.GREEN);
                for (ControlledFlight flight : activeFlights) {
                    Point2D.Double pos = flight.getPosition();
                    int x = centerX + (int)(pos.x * maxRadius);
                    int y = centerY + (int)(pos.y * maxRadius);
                    g2d.fillOval(x - 3, y - 3, 6, 6);
                    g2d.drawString(flight.getCallsign(), x + 5, y - 5);
                }
            }
        };
        radarPanel.setPreferredSize(new Dimension(600, 600));
        add(radarPanel, BorderLayout.CENTER);
    }

    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(new Color(30, 30, 35));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Groupe de contrôle radar
        JPanel radarPanel = new JPanel();
        radarPanel.setLayout(new BoxLayout(radarPanel, BoxLayout.Y_AXIS));
        radarPanel.setBackground(new Color(40, 40, 45));
        radarPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65)),
            "Contrôle Radar",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            Color.WHITE
        ));
        
        // Contrôle de rotation
        JLabel rotationLabel = new JLabel("Rotation (°/s):");
        rotationLabel.setForeground(Color.WHITE);
        JSlider rotationSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
        rotationSlider.setBackground(new Color(40, 40, 45));
        rotationSlider.setForeground(Color.WHITE);
        
        // Contrôle de portée
        JLabel rangeLabel = new JLabel("Portée (km):");
        rangeLabel.setForeground(Color.WHITE);
        JSlider rangeSlider = new JSlider(JSlider.HORIZONTAL, 100, 1000, 500);
        rangeSlider.setBackground(new Color(40, 40, 45));
        rangeSlider.setForeground(Color.WHITE);
        
        radarPanel.add(Box.createVerticalStrut(5));
        radarPanel.add(rotationLabel);
        radarPanel.add(rotationSlider);
        radarPanel.add(Box.createVerticalStrut(10));
        radarPanel.add(rangeLabel);
        radarPanel.add(rangeSlider);
        radarPanel.add(Box.createVerticalStrut(5));

        // Groupe de gestion des vols
        JPanel flightPanel = new JPanel();
        flightPanel.setLayout(new BoxLayout(flightPanel, BoxLayout.Y_AXIS));
        flightPanel.setBackground(new Color(40, 40, 45));
        flightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 65)),
            "Gestion des Vols",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            Color.WHITE
        ));

        // Boutons de contrôle
        JButton addFlightButton = createStyledButton("Ajouter un Vol", new Color(0, 120, 212));
        addFlightButton.addActionListener(e -> showAddFlightDialog());

        JButton emergencyButton = createStyledButton("Mode Urgence", new Color(200, 30, 30));
        emergencyButton.addActionListener(e -> handleEmergency());

        JButton backButton = createStyledButton("Retour au Menu", new Color(60, 60, 65));
        backButton.addActionListener(e -> {
            updateTimer.stop();
            dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });

        flightPanel.add(Box.createVerticalStrut(5));
        flightPanel.add(addFlightButton);
        flightPanel.add(Box.createVerticalStrut(5));
        flightPanel.add(emergencyButton);
        flightPanel.add(Box.createVerticalStrut(5));
        flightPanel.add(backButton);
        flightPanel.add(Box.createVerticalStrut(5));

        // Add panels to control panel
        controlPanel.add(radarPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(flightPanel);
        
        // Add listeners
        rotationSlider.addChangeListener(e -> radarRotationSpeed = rotationSlider.getValue());
        rangeSlider.addChangeListener(e -> radarRange = rangeSlider.getValue());
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(180, 35));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(brighter(bgColor));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private Color brighter(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.min(1.0f, hsb[2] * 1.2f));
    }

    private void createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        flightInfoArea = new JTextArea(10, 30);
        flightInfoArea.setEditable(false);
        flightInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(flightInfoArea);
        infoPanel.add(new JLabel("Informations des Vols"), BorderLayout.NORTH);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
    }

    private void showAddFlightDialog() {
        JDialog dialog = new JDialog(parentFrame, "Ajouter un Vol", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Champs de saisie
        JTextField callsignField = new JTextField();
        JTextField altitudeField = new JTextField();
        JTextField speedField = new JTextField();
        JTextField headingField = new JTextField();

        // Ajout des composants
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Indicatif:"), gbc);
        gbc.gridx = 1;
        panel.add(callsignField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Altitude (ft):"), gbc);
        gbc.gridx = 1;
        panel.add(altitudeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Vitesse (kts):"), gbc);
        gbc.gridx = 1;
        panel.add(speedField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Cap (°):"), gbc);
        gbc.gridx = 1;
        panel.add(headingField, gbc);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            try {
                String callsign = callsignField.getText();
                int altitude = Integer.parseInt(altitudeField.getText());
                int speed = Integer.parseInt(speedField.getText());
                int heading = Integer.parseInt(headingField.getText());

                ControlledFlight flight = new ControlledFlight(
                    callsign, 
                    new Point2D.Double(radarCenter.x + random.nextDouble() * 200 - 100,
                                     radarCenter.y + random.nextDouble() * 200 - 100),
                    altitude,
                    speed,
                    heading
                );
                activeFlights.add(flight);
                flightColors.put(callsign, new Color(
                    random.nextFloat(),
                    random.nextFloat(),
                    random.nextFloat()
                ));
                dialog.dispose();
                updateFlightInfo();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Veuillez entrer des valeurs numériques valides",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void handleEmergency() {
        // Simulation d'une situation d'urgence
        for (ControlledFlight flight : activeFlights) {
            // Augmenter la séparation entre les avions
            flight.setEmergencyMode(true);
        }
        
        // Afficher une alerte
        JOptionPane.showMessageDialog(this,
            "MODE URGENCE ACTIVÉ\n" +
            "- Séparation augmentée\n" +
            "- Vitesses réduites\n" +
            "- Communications prioritaires",
            "URGENCE",
            JOptionPane.WARNING_MESSAGE);
    }

    private void handleMouseClick(Point point) {
        for (ControlledFlight flight : activeFlights) {
            if (flight.isNear(point, 20)) {
                showFlightControlDialog(flight);
                break;
            }
        }
    }

    private void showFlightControlDialog(ControlledFlight flight) {
        JDialog dialog = new JDialog(parentFrame, "Contrôle du Vol " + flight.getCallsign(), false);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Contrôles de vol
        JLabel altitudeLabel = new JLabel("Altitude: " + flight.getAltitude() + " ft");
        JSlider altitudeSlider = new JSlider(0, 40000, flight.getAltitude());
        altitudeSlider.addChangeListener(e -> {
            flight.setAltitude(altitudeSlider.getValue());
            altitudeLabel.setText("Altitude: " + flight.getAltitude() + " ft");
        });

        JLabel headingLabel = new JLabel("Cap: " + flight.getHeading() + "°");
        JSlider headingSlider = new JSlider(0, 359, flight.getHeading());
        headingSlider.addChangeListener(e -> {
            flight.setHeading(headingSlider.getValue());
            headingLabel.setText("Cap: " + flight.getHeading() + "°");
        });

        panel.add(altitudeLabel);
        panel.add(altitudeSlider);
        panel.add(headingLabel);
        panel.add(headingSlider);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateFlights() {
        for (ControlledFlight flight : activeFlights) {
            flight.update();
        }
        updateFlightInfo();
        radarPanel.repaint();
    }

    private void updateFlightInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== VOLS ACTIFS ===\n");
        for (ControlledFlight flight : activeFlights) {
            info.append(String.format(
                "%s: Alt:%5d Cap:%3d°\n",
                flight.getCallsign(),
                flight.getAltitude(),
                flight.getHeading()
            ));
        }
        flightInfoArea.setText(info.toString());
    }

    private void addTestFlights() {
        String[] testCallsigns = {"AF1234", "BA2345", "LH5678"};
        for (String callsign : testCallsigns) {
            ControlledFlight flight = new ControlledFlight(
                callsign,
                new Point2D.Double(
                    radarCenter.x + random.nextDouble() * 300 - 150,
                    radarCenter.y + random.nextDouble() * 300 - 150
                ),
                (int)(random.nextDouble() * 35000 + 5000),
                (int)(random.nextDouble() * 200 + 400),
                (int)(random.nextDouble() * 360)
            );
            activeFlights.add(flight);
            flightColors.put(callsign, new Color(
                random.nextFloat(),
                random.nextFloat(),
                random.nextFloat()
            ));
        }
    }

    private class ControlledFlight {
        private String callsign;
        private Point2D.Double position;
        private int altitude;
        private int speed;
        private int heading;
        private boolean emergencyMode;

        public ControlledFlight(String callsign, Point2D.Double position, 
                              int altitude, int speed, int heading) {
            this.callsign = callsign;
            this.position = position;
            this.altitude = altitude;
            this.speed = speed;
            this.heading = heading;
            this.emergencyMode = false;
        }

        public void update() {
            double headingRad = Math.toRadians(heading);
            double distance = speed * 0.0005 * (emergencyMode ? 0.5 : 1.0);
            position.x += Math.sin(headingRad) * distance;
            position.y -= Math.cos(headingRad) * distance;

            // Garder l'avion dans la zone du radar
            if (position.distance(radarCenter) > radarRadius) {
                double angle = Math.atan2(
                    position.y - radarCenter.y,
                    position.x - radarCenter.x
                );
                position.x = radarCenter.x + Math.cos(angle) * (radarRadius * 0.9);
                position.y = radarCenter.y + Math.sin(angle) * (radarRadius * 0.9);
                heading = (heading + 180) % 360;
            }
        }

        public boolean isNear(Point point, int threshold) {
            return position.distance(point) < threshold;
        }

        // Getters et setters
        public String getCallsign() { return callsign; }
        public Point2D.Double getPosition() { return position; }
        public int getAltitude() { return altitude; }
        public void setAltitude(int altitude) { this.altitude = altitude; }
        public int getHeading() { return heading; }
        public void setHeading(int heading) { this.heading = heading; }
        public void setEmergencyMode(boolean emergency) { this.emergencyMode = emergency; }
    }
} 