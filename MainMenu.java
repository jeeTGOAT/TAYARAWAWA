import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends JPanel {
    private Timer animationTimer;
    private float alpha = 0.0f;
    private int buttonHover = -1;
    private JFrame parentFrame;
    private String[] menuItems = {
        "Gestion des Vols",
        "Simulation de Vol",
        "Mode Contrôleur Aérien",
        "Statistiques en Direct",
        "Gestion des Passagers",
        "Réservations",
        "Maintenance",
        "Quitter"
    };
    private String[] menuIcons = {
        "✈️", "🌍", "🎮", "📊", "👥", "🎫", "🔧", "🚪"
    };
    private Color[] buttonColors = {
        new Color(46, 204, 113),  // Green for Flight Management
        new Color(52, 152, 219),  // Blue for Flight Simulation
        new Color(155, 89, 182),  // Purple for ATC
        new Color(230, 126, 34),  // Orange for Statistics
        new Color(26, 188, 156),  // Turquoise for Passenger Management
        new Color(241, 196, 15),  // Yellow for Bookings
        new Color(231, 76, 60),   // Red for Maintenance
        new Color(149, 165, 166)  // Gray for Exit
    };
    private float[] buttonScales;
    private Clip backgroundMusic;
    private boolean isMusicPlaying = false;
    private BufferedImage logoImage;
    private float logoScale = 1.0f;
    private Timer logoAnimationTimer;
    private JLabel timeLabel;
    private JLabel weatherLabel;
    private JLabel systemStatusLabel;
    private BufferedImage backgroundImage;

    private BufferedImage createDefaultLogo() {
        int width = 800;
        int height = 200;
        BufferedImage logo = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = logo.createGraphics();
        
        // Enable high quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        
        // Draw transparent background
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, width, height);
        
        // Draw airplane silhouette
        int planeSize = 120;
        g2d.setColor(new Color(52, 152, 219));
        
        // Draw airplane body
        g2d.fillRoundRect(50, height/2 - 20, 160, 40, 30, 30);
        
        // Draw wings
        int[] xPointsWings = {120, 180, 150, 90};
        int[] yPointsWings = {height/2 - 60, height/2 - 40, height/2 - 40, height/2 - 60};
        g2d.fillPolygon(xPointsWings, yPointsWings, 4);
        
        int[] xPointsWings2 = {120, 180, 150, 90};
        int[] yPointsWings2 = {height/2 + 60, height/2 + 40, height/2 + 40, height/2 + 60};
        g2d.fillPolygon(xPointsWings2, yPointsWings2, 4);
        
        // Draw tail
        int[] xPointsTail = {45, 65, 55};
        int[] yPointsTail = {height/2 - 60, height/2, height/2 + 60};
        g2d.fillPolygon(xPointsTail, yPointsTail, 3);
        
        // Draw outer glow for the airplane
        g2d.setColor(new Color(52, 152, 219, 50));
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRoundRect(45, height/2 - 25, 170, 50, 35, 35);
        
        // Draw text with professional font
        Font titleFont = new Font("Arial", Font.BOLD, 72);
        g2d.setFont(titleFont);
        
        // Draw text shadow
        g2d.setColor(new Color(0, 0, 0, 70));
        g2d.drawString("TAYARAWAWA", 253, 112);
        
        // Draw main text
        GradientPaint textGradient = new GradientPaint(
            250, 60, new Color(255, 255, 255),
            250, 120, new Color(200, 220, 255)
        );
        g2d.setPaint(textGradient);
        g2d.drawString("TAYARAWAWA", 250, 110);
        
        // Add subtle glow effect
        g2d.setColor(new Color(100, 180, 255, 20));
        for (int i = 1; i < 10; i++) {
            g2d.setStroke(new BasicStroke(i));
            g2d.drawString("TAYARAWAWA", 250, 110);
        }
        
        // Add tagline
        g2d.setFont(new Font("Arial", Font.ITALIC, 16));
        g2d.setColor(new Color(220, 220, 220));
        g2d.drawString("Système de Gestion Aérienne Professionnelle", 320, 140);
        
        g2d.dispose();
        
        // Save the logo to file for reuse
        try {
            File outputFile = new File("resources/images/logo.png");
            outputFile.getParentFile().mkdirs();
            ImageIO.write(logo, "PNG", outputFile);
            System.out.println("Professional logo created and saved!");
        } catch (IOException e) {
            System.err.println("Error saving logo: " + e.getMessage());
        }
        
        return logo;
    }

    private BufferedImage createDefaultBackground() {
        BufferedImage background = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = background.createGraphics();
        
        // Enable high quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Create premium gradient background
        GradientPaint gradient = new GradientPaint(0, 0, new Color(12, 20, 39),
                                                 1920, 1080, new Color(48, 70, 103));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 1920, 1080);
        
        // Add worldwide network effect
        g2d.setColor(new Color(255, 255, 255, 10));
        // Draw grid
        for (int i = 0; i < 1920; i += 40) {
            g2d.drawLine(i, 0, i, 1080);
        }
        for (int i = 0; i < 1080; i += 40) {
            g2d.drawLine(0, i, 1920, i);
        }
        
        // Add flight path arcs
        g2d.setColor(new Color(100, 180, 255, 30));
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Several curved paths representing flight routes
        g2d.draw(new CubicCurve2D.Double(200, 300, 600, 100, 1200, 200, 1600, 500));
        g2d.draw(new CubicCurve2D.Double(300, 900, 600, 600, 1000, 700, 1500, 300));
        g2d.draw(new CubicCurve2D.Double(100, 500, 400, 300, 800, 800, 1700, 600));
        
        // Add some "airports" as dots at the endpoints of the curves
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.fillOval(195, 295, 10, 10);
        g2d.fillOval(1595, 495, 10, 10);
        g2d.fillOval(295, 895, 10, 10);
        g2d.fillOval(1495, 295, 10, 10);
        g2d.fillOval(95, 495, 10, 10);
        g2d.fillOval(1695, 595, 10, 10);
        
        // Add subtle vignette effect (darkened corners)
        RadialGradientPaint vignette = new RadialGradientPaint(
            new Point(1920/2, 1080/2),
            1200,
            new float[] {0.0f, 0.8f, 1.0f},
            new Color[] {
                new Color(0, 0, 0, 0),
                new Color(0, 0, 0, 0),
                new Color(0, 0, 0, 160)
            }
        );
        g2d.setPaint(vignette);
        g2d.fillRect(0, 0, 1920, 1080);
        
        // Add a subtle light effect in the center
        RadialGradientPaint centerLight = new RadialGradientPaint(
            new Point(1920/2, 1080/2),
            400,
            new float[] {0.0f, 1.0f},
            new Color[] {
                new Color(255, 255, 255, 15),
                new Color(255, 255, 255, 0)
            }
        );
        g2d.setPaint(centerLight);
        g2d.fillOval(1920/2 - 400, 1080/2 - 400, 800, 800);
        
        try {
            File outputFile = new File("resources/images/background.png");
            outputFile.getParentFile().mkdirs();
            ImageIO.write(background, "PNG", outputFile);
            System.out.println("Professional background created and saved!");
        } catch (IOException e) {
            System.err.println("Error saving background: " + e.getMessage());
        }
        
        g2d.dispose();
        return background;
    }

    public MainMenu(JFrame parent) {
        this.parentFrame = parent;
        setLayout(null);
        buttonScales = new float[menuItems.length];
        
        for (int i = 0; i < menuItems.length; i++) {
            buttonScales[i] = 1.0f;
        }

        // Load logo
        try {
            File logoFile = new File("resources/images/logo.png");
            if (!logoFile.exists()) {
                System.out.println("Logo not found, creating logo file...");
                // Create the logo image
                BufferedImage logo = new BufferedImage(400, 100, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = logo.createGraphics();
                
                // Enable antialiasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(new Color(0, 0, 0, 0));
                g2d.fillRect(0, 0, 400, 100);
                
                // Draw futuristic logo
                g2d.setColor(new Color(52, 152, 219));
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 60));
                g2d.drawString("✈️", 20, 70);
                
                // Draw text with futuristic style
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 40));
                g2d.drawString("TAYARAWAWA", 100, 60);
                
                // Add neon glow effect
                g2d.setColor(new Color(52, 152, 219, 30));
                for (int i = 0; i < 10; i++) {
                    g2d.drawString("TAYARAWAWA", 100 - i, 60 - i);
                    g2d.drawString("TAYARAWAWA", 100 + i, 60 + i);
                }
                
                g2d.dispose();
                
                // Save the logo file
                logoFile.getParentFile().mkdirs();
                ImageIO.write(logo, "PNG", logoFile);
                System.out.println("Logo file created successfully!");
            }
            logoImage = ImageIO.read(logoFile);
        } catch (IOException e) {
            System.out.println("Error creating/loading logo: " + e.getMessage());
            logoImage = createDefaultLogo();
        }

        // Logo animation
        logoAnimationTimer = new Timer(50, e -> {
            logoScale = 1.0f + (float)Math.sin(System.currentTimeMillis() / 1000.0) * 0.03f;
            repaint();
        });
        logoAnimationTimer.start();

        // Status panel with glass effect
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create glass effect
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw subtle gradient
                GradientPaint gradient = new GradientPaint(0, 0, 
                    new Color(255, 255, 255, 10),
                    0, getHeight(),
                    new Color(255, 255, 255, 0));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                super.paintComponent(g);
            }
        };
        statusPanel.setOpaque(false);
        statusPanel.setBounds(0, 0, getWidth(), 40);

        // Time label with modern styling
        timeLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateTimeLabel();

        // Weather label with icon
        weatherLabel = new JLabel("☀️ 25°C | 1013 hPa") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        weatherLabel.setForeground(Color.WHITE);
        weatherLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // System status label with animated dot
        systemStatusLabel = new JLabel("🟢 Système Opérationnel") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        systemStatusLabel.setForeground(Color.WHITE);
        systemStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        statusPanel.add(timeLabel);
        statusPanel.add(weatherLabel);
        statusPanel.add(systemStatusLabel);
        add(statusPanel);

        // Update time every second
        Timer timeTimer = new Timer(1000, e -> updateTimeLabel());
        timeTimer.start();

        // Music control with modern styling
        JPanel musicPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        musicPanel.setOpaque(false);
        JButton musicButton = new JButton("🎵") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        styleButton(musicButton);
        musicButton.addActionListener(e -> toggleMusic());
        musicPanel.add(musicButton);
        musicPanel.setBounds(getWidth() - 60, 5, 50, 30);
        add(musicPanel);

        initializeMusic();

        // Fade-in animation
        animationTimer = new Timer(50, e -> {
            if (alpha < 1.0f) {
                alpha += 0.05f;
                if (alpha > 1.0f) alpha = 1.0f;
                repaint();
            } else {
                animationTimer.stop();
            }
        });
        animationTimer.start();

        // Mouse interactions with improved feedback
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int button = getButtonAtPoint(e.getPoint());
                if (button != -1) {
                    // Add click animation
                    buttonScales[button] = 0.95f;
                    repaint();
                    Timer resetTimer = new Timer(100, ev -> {
                        buttonScales[button] = 1.0f;
                        repaint();
                        ((Timer)ev.getSource()).stop();
                    });
                    resetTimer.start();
                    
                    handleMenuSelection(button);
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int newHover = getButtonAtPoint(e.getPoint());
                if (newHover != buttonHover) {
                    buttonHover = newHover;
                    animateButton(buttonHover);
                    repaint();
                }
            }
        });

        // Replace the image loading code with:
        try {
            backgroundImage = ImageIO.read(new File("resources/images/avion-800_1.png"));
        } catch (Exception e) {
            System.out.println("Background image not found, creating default background.");
            backgroundImage = createDefaultBackground();
        }
    }

    private void updateTimeLabel() {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String dateStr = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        timeLabel.setText("🕒 " + timeStr + " | 📅 " + dateStr);
    }

    private void initializeMusic() {
        try {
            File musicFile = new File("resources/menu_music.wav");
            if (!musicFile.exists()) {
                System.out.println("Music file not found: " + musicFile.getAbsolutePath());
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            
            // Start playing automatically
            backgroundMusic.start();
            isMusicPlaying = true;
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error loading music: " + e.getMessage());
        }
    }

    private void toggleMusic() {
        if (backgroundMusic != null) {
            if (isMusicPlaying) {
                backgroundMusic.stop();
            } else {
                backgroundMusic.setFramePosition(0);
                backgroundMusic.start();
            }
            isMusicPlaying = !isMusicPlaying;
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (backgroundMusic != null) {
            backgroundMusic.close();
        }
    }

    private void animateButton(int index) {
        if (index != -1) {
            Timer scaleTimer = new Timer(50, null);
            scaleTimer.addActionListener(new ActionListener() {
                float scale = 1.0f;
                boolean growing = true;
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (growing) {
                        scale += 0.05f;
                        if (scale >= 1.1f) {
                            scale = 1.1f;
                            growing = false;
                        }
                    } else {
                        scale -= 0.02f;
                        if (scale <= 1.0f) {
                            scale = 1.0f;
                            growing = true;
                        }
                    }
                    buttonScales[index] = scale;
                    repaint();
                    if (!growing && scale <= 1.0f) {
                        scaleTimer.stop();
                    }
                }
            });
            scaleTimer.start();
        }
    }

    private int getButtonAtPoint(Point p) {
        int buttonHeight = 65;
        int spacing = 20;
        int totalHeight = menuItems.length * (buttonHeight + spacing);
        int startY = (getHeight() - totalHeight) / 2 + 50;
        int buttonWidth = 400;
        int x = (getWidth() - buttonWidth) / 2;

        for (int i = 0; i < menuItems.length; i++) {
            int y = startY + i * (buttonHeight + spacing);
            Rectangle button = new Rectangle(x, y, buttonWidth, buttonHeight);
            if (button.contains(p)) {
                return i;
            }
        }
        return -1;
    }

    private void handleMenuSelection(int index) {
        // Stop music when leaving menu
        if (backgroundMusic != null && isMusicPlaying) {
            backgroundMusic.stop();
            isMusicPlaying = false;
        }

        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;

        switch (index) {
            case 0: // Gestion des Vols
                showAirTrafficManagement();
                break;
            case 1: // Simulation de Vol
                showFlightSimulation();
                break;
            case 2: // Mode Contrôleur Aérien
                showAirTrafficControl();
                break;
            case 3: // Statistiques en Direct
                showLiveStatistics();
                break;
            case 4: // Gestion des Passagers
                showPassengerManagement();
                break;
            case 5: // Réservations
                showBookingManagement();
                break;
            case 6: // Maintenance
                showMaintenance();
                break;
            case 7: // Quitter
                System.exit(0);
                break;
        }
    }

    private void showAirTrafficManagement() {
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;
        
        // Create a new air traffic management window
        JFrame atmFrame = new JFrame("Gestion du Trafic Aérien");
        atmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        atmFrame.setSize(1280, 720);
        atmFrame.setLocationRelativeTo(frame);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 30, 48));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setPreferredSize(new Dimension(1280, 60));
        
        JLabel titleLabel = new JLabel("Gestion du Trafic Aérien");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        
        JButton searchButton = new JButton("Rechercher");
        styleButton(searchButton);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(36, 59, 85));
        
        // Create flight table
        String[] columnNames = {"ID", "Origine", "Destination", "Départ", "Arrivée", "Avion", "Statut"};
        Object[][] data = {
            {"AL123", "Alger (ALG)", "Paris (CDG)", "08:30", "11:45", "Boeing 737-800", "En vol"},
            {"AL456", "Paris (CDG)", "Alger (ALG)", "12:30", "15:45", "Airbus A320", "À l'heure"},
            {"TU789", "Tunis (TUN)", "Marseille (MRS)", "09:15", "10:30", "Boeing 737-600", "Retardé"},
            {"MA234", "Casablanca (CMN)", "Madrid (MAD)", "14:00", "16:30", "Boeing 787-9", "À l'heure"},
            {"AL345", "Alger (ALG)", "Istanbul (IST)", "23:00", "03:30", "Airbus A330-200", "À l'heure"},
            {"DZ678", "Oran (ORN)", "Alger (ALG)", "16:20", "17:10", "ATR 72-600", "À l'heure"},
            {"TU901", "Lyon (LYS)", "Tunis (TUN)", "18:30", "20:15", "Airbus A319", "Retardé"},
            {"MA432", "Rabat (RBA)", "Paris (ORY)", "10:45", "14:00", "Boeing 737-800", "En vol"}
        };
        
        JTable flightTable = new JTable(data, columnNames) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                String status = (String) getModel().getValueAt(row, 6);
                if (status.equals("En vol")) {
                    comp.setForeground(new Color(52, 152, 219)); // Blue
                } else if (status.equals("À l'heure")) {
                    comp.setForeground(new Color(46, 204, 113)); // Green
                } else if (status.equals("Retardé")) {
                    comp.setForeground(new Color(241, 196, 15)); // Yellow
                } else if (status.equals("Annulé")) {
                    comp.setForeground(new Color(231, 76, 60)); // Red
                } else {
                    comp.setForeground(Color.WHITE);
                }
                return comp;
            }
        };
        flightTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        flightTable.setBackground(new Color(20, 30, 48));
        flightTable.setForeground(Color.WHITE);
        flightTable.setGridColor(new Color(52, 73, 94));
        flightTable.setRowHeight(30);
        flightTable.setSelectionBackground(new Color(52, 152, 219));
        flightTable.setSelectionForeground(Color.WHITE);
        flightTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        flightTable.getTableHeader().setBackground(new Color(44, 62, 80));
        flightTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219)));
        
        // Create control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(new Color(44, 62, 80));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create filter panel (left)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setOpaque(false);
        
        JLabel filterLabel = new JLabel("Filtrer par:");
        filterLabel.setForeground(Color.WHITE);
        
        String[] filterOptions = {"Tous", "En vol", "À l'heure", "Retardé", "Annulé"};
        JComboBox<String> filterBox = new JComboBox<>(filterOptions);
        filterBox.setBackground(new Color(52, 73, 94));
        filterBox.setForeground(Color.WHITE);
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterBox);
        
        // Create button panel (right)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);
        
        JButton addButton = new JButton("Ajouter Vol");
        styleButton(addButton);
        addButton.addActionListener(e -> {
            AddFlightDialog dialog = new AddFlightDialog(atmFrame);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                // In a real app, we would add the new flight to the table
                JOptionPane.showMessageDialog(atmFrame, 
                    "Vol ajouté avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton editButton = new JButton("Modifier");
        styleButton(editButton);
        editButton.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow != -1) {
                // In a real app, we would open a dialog to edit the flight
                JOptionPane.showMessageDialog(atmFrame, 
                    "Modification du vol " + flightTable.getValueAt(selectedRow, 0), 
                    "Édition", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(atmFrame, 
                    "Veuillez sélectionner un vol à modifier", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton deleteButton = new JButton("Supprimer");
        styleButton(deleteButton);
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(atmFrame, 
                    "Êtes-vous sûr de vouloir supprimer ce vol ?", 
                    "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // In a real app, we would delete the flight from the table
                    JOptionPane.showMessageDialog(atmFrame, 
                        "Vol supprimé avec succès!", 
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(atmFrame, 
                    "Veuillez sélectionner un vol à supprimer", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton backButton = new JButton("Retour au Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> {
            atmFrame.dispose();
            frame.setVisible(true);
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        
        // Add panels to the control panel
        controlPanel.add(filterPanel, BorderLayout.WEST);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create info panel for selected flight
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(52, 73, 94));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(52, 152, 219)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        infoPanel.setPreferredSize(new Dimension(1280, 120));
        
        JPanel flightInfoPanel = new JPanel(new GridLayout(2, 4, 20, 10));
        flightInfoPanel.setOpaque(false);
        
        // Add flight info labels
        String[] infoLabels = {
            "Vol:", "AL123",
            "Origine:", "Alger (ALG)",
            "Destination:", "Paris (CDG)",
            "Statut:", "En vol",
            "Départ:", "08:30",
            "Arrivée:", "11:45",
            "Avion:", "Boeing 737-800",
            "Retard:", "0 min"
        };
        
        for (int i = 0; i < infoLabels.length; i++) {
            JLabel label = new JLabel(infoLabels[i]);
            label.setForeground(Color.WHITE);
            if (i % 2 == 0) {
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
            flightInfoPanel.add(label);
        }
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        // Create a custom progress bar
        JPanel progressBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(new Color(30, 40, 50));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw progress (40% complete for example)
                double progress = 0.4;
                g2d.setColor(new Color(52, 152, 219));
                g2d.fillRoundRect(0, 0, (int)(getWidth() * progress), getHeight(), 10, 10);
                
                // Draw origin and destination cities
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2d.drawString("ALG", 5, getHeight() - 5);
                g2d.drawString("CDG", getWidth() - 30, getHeight() - 5);
                
                // Draw plane icon at current position
                g2d.setColor(Color.WHITE);
                int planeX = (int)(getWidth() * progress);
                int planeY = getHeight() / 2;
                g2d.fillOval(planeX - 5, planeY - 5, 10, 10);
                g2d.drawString("✈️", planeX - 10, planeY - 10);
            }
        };
        progressBar.setPreferredSize(new Dimension(1280, 30));
        
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        infoPanel.add(flightInfoPanel, BorderLayout.CENTER);
        infoPanel.add(progressPanel, BorderLayout.SOUTH);
        
        // Add components to the content panel
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Add all panels to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Set up flight selection listener
        flightTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = flightTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Update flight info panel with selected flight data
                    ((JLabel)flightInfoPanel.getComponent(1)).setText((String)flightTable.getValueAt(selectedRow, 0));
                    ((JLabel)flightInfoPanel.getComponent(3)).setText((String)flightTable.getValueAt(selectedRow, 1));
                    ((JLabel)flightInfoPanel.getComponent(5)).setText((String)flightTable.getValueAt(selectedRow, 2));
                    ((JLabel)flightInfoPanel.getComponent(7)).setText((String)flightTable.getValueAt(selectedRow, 6));
                    ((JLabel)flightInfoPanel.getComponent(9)).setText((String)flightTable.getValueAt(selectedRow, 3));
                    ((JLabel)flightInfoPanel.getComponent(11)).setText((String)flightTable.getValueAt(selectedRow, 4));
                    ((JLabel)flightInfoPanel.getComponent(13)).setText((String)flightTable.getValueAt(selectedRow, 5));
                    
                    // Set delay based on status
                    String status = (String)flightTable.getValueAt(selectedRow, 6);
                    if (status.equals("Retardé")) {
                        ((JLabel)flightInfoPanel.getComponent(15)).setText("30 min");
                    } else {
                        ((JLabel)flightInfoPanel.getComponent(15)).setText("0 min");
                    }
                    
                    // Update progress bar
                    progressBar.repaint();
                }
            }
        });
        
        // Add filter functionality
        filterBox.addActionListener(e -> {
            String selected = (String)filterBox.getSelectedItem();
            if (selected.equals("Tous")) {
                // Show all flights
                // In a real app, we would implement filtering
                JOptionPane.showMessageDialog(atmFrame, 
                    "Affichage de tous les vols", 
                    "Filtre", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Filter flights by selected status
                JOptionPane.showMessageDialog(atmFrame, 
                    "Filtrage des vols par statut: " + selected, 
                    "Filtre", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        atmFrame.setContentPane(mainPanel);
        atmFrame.setVisible(true);
        frame.setVisible(false);
    }

    // Dialog for adding a new flight
    private class AddFlightDialog extends JDialog {
        private boolean confirmed = false;
        private JTextField flightIdField;
        private JComboBox<String> originBox;
        private JComboBox<String> destinationBox;
        private JTextField departureField;
        private JTextField arrivalField;
        private JComboBox<String> aircraftBox;
        
        public AddFlightDialog(JFrame parent) {
            super(parent, "Ajouter un Vol", true);
            setSize(400, 350);
            setLocationRelativeTo(parent);
            setResizable(false);
            
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(new Color(36, 59, 85));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Flight ID
            JLabel flightIdLabel = new JLabel("ID du Vol:");
            flightIdLabel.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(flightIdLabel, gbc);
            
            flightIdField = new JTextField();
            gbc.gridx = 1;
            gbc.gridy = 0;
            panel.add(flightIdField, gbc);
            
            // Origin
            JLabel originLabel = new JLabel("Origine:");
            originLabel.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(originLabel, gbc);
            
            String[] airports = {"Alger (ALG)", "Paris (CDG)", "Paris (ORY)", "Marseille (MRS)", 
                               "Tunis (TUN)", "Casablanca (CMN)", "Madrid (MAD)", "Istanbul (IST)",
                               "Oran (ORN)", "Lyon (LYS)", "Rabat (RBA)"};
            originBox = new JComboBox<>(airports);
            originBox.setBackground(new Color(52, 73, 94));
            originBox.setForeground(Color.WHITE);
            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(originBox, gbc);
            
            // Destination
            JLabel destinationLabel = new JLabel("Destination:");
            destinationLabel.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(destinationLabel, gbc);
            
            destinationBox = new JComboBox<>(airports);
            destinationBox.setBackground(new Color(52, 73, 94));
            destinationBox.setForeground(Color.WHITE);
            gbc.gridx = 1;
            gbc.gridy = 2;
            panel.add(destinationBox, gbc);
            
            // Departure time
            JLabel departureLabel = new JLabel("Heure de Départ:");
            departureLabel.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(departureLabel, gbc);
            
            departureField = new JTextField();
            gbc.gridx = 1;
            gbc.gridy = 3;
            panel.add(departureField, gbc);
            
            // Arrival time
            JLabel arrivalLabel = new JLabel("Heure d'Arrivée:");
            arrivalLabel.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 4;
            panel.add(arrivalLabel, gbc);
            
            arrivalField = new JTextField();
            gbc.gridx = 1;
            gbc.gridy = 4;
            panel.add(arrivalField, gbc);
            
            // Aircraft
            JLabel aircraftLabel = new JLabel("Type d'Avion:");
            aircraftLabel.setForeground(Color.WHITE);
            gbc.gridx = 0;
            gbc.gridy = 5;
            panel.add(aircraftLabel, gbc);
            
            String[] aircraft = {"Boeing 737-800", "Airbus A320", "Boeing 737-600", 
                              "Boeing 787-9", "Airbus A330-200", "ATR 72-600", "Airbus A319"};
            aircraftBox = new JComboBox<>(aircraft);
            aircraftBox.setBackground(new Color(52, 73, 94));
            aircraftBox.setForeground(Color.WHITE);
            gbc.gridx = 1;
            gbc.gridy = 5;
            panel.add(aircraftBox, gbc);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setOpaque(false);
            
            JButton cancelButton = new JButton("Annuler");
            cancelButton.addActionListener(e -> dispose());
            
            JButton confirmButton = new JButton("Confirmer");
            confirmButton.addActionListener(e -> {
                confirmed = true;
                dispose();
            });
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(confirmButton);
            
            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(20, 5, 5, 5);
            panel.add(buttonPanel, gbc);
            
            setContentPane(panel);
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
    }
    
    // Method to style a button with a modern look
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 152, 219));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(41, 128, 185), 1),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
                ));
            }
        });
    }
    
    // Reimplementing the missing methods
    private void showAirTrafficControl() {
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;
        
        // Create a new air traffic control window
        JFrame atcFrame = new JFrame("Contrôle du Trafic Aérien");
        atcFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        atcFrame.setSize(1280, 720);
        atcFrame.setLocationRelativeTo(frame);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 30, 48));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setPreferredSize(new Dimension(1280, 60));
        
        JLabel titleLabel = new JLabel("Contrôle du Trafic Aérien");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        
        JButton searchButton = new JButton("Rechercher");
        styleButton(searchButton);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Create radar panel
        JPanel radarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw dark background
                g2d.setColor(new Color(10, 20, 40));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw radar grid
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int maxRadius = Math.min(centerX, centerY) - 20;
                
                // Draw concentric circles
                g2d.setColor(new Color(0, 150, 0, 50));
                for (int r = 50; r <= maxRadius; r += 50) {
                    g2d.drawOval(centerX - r, centerY - r, r * 2, r * 2);
                }
                
                // Draw radar lines
                for (int angle = 0; angle < 360; angle += 30) {
                    double rad = Math.toRadians(angle);
                    int x = (int)(centerX + Math.cos(rad) * maxRadius);
                    int y = (int)(centerY + Math.sin(rad) * maxRadius);
                    g2d.drawLine(centerX, centerY, x, y);
                }
                
                // Draw radar sweep (rotating line)
                int sweepAngle = (int)(System.currentTimeMillis() / 50) % 360;
                double sweepRad = Math.toRadians(sweepAngle);
                int sweepX = (int)(centerX + Math.cos(sweepRad) * maxRadius);
                int sweepY = (int)(centerY + Math.sin(sweepRad) * maxRadius);
                
                GradientPaint gradient = new GradientPaint(
                    centerX, centerY, 
                    new Color(0, 255, 0, 200),
                    sweepX, sweepY,
                    new Color(0, 255, 0, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillArc(centerX - maxRadius, centerY - maxRadius, 
                           maxRadius * 2, maxRadius * 2, 
                           sweepAngle - 15, 30);
                
                // Draw aircraft
                drawAircraft(g2d, centerX, centerY, centerX + 100, centerY - 120, "AL123", 12000, 500, false);
                drawAircraft(g2d, centerX, centerY, centerX - 150, centerY + 70, "AL456", 9500, 480, false);
                drawAircraft(g2d, centerX, centerY, centerX + 50, centerY + 180, "TU789", 10200, 510, false);
                drawAircraft(g2d, centerX, centerY, centerX - 60, centerY - 150, "MA234", 11500, 500, false);
                
                // Draw two aircraft that are close to each other (conflict)
                drawAircraft(g2d, centerX, centerY, centerX + 180, centerY + 190, "DZ678", 10000, 490, true);
                drawAircraft(g2d, centerX, centerY, centerX + 170, centerY + 200, "TU901", 10100, 510, true);
                
                // Repaint for animation
                repaint();
            }
            
            private void drawAircraft(Graphics2D g2d, int centerX, int centerY, 
                                    int x, int y, String id, int altitude, 
                                    int speed, boolean conflict) {
                // Draw aircraft icon
                Color aircraftColor = conflict ? Color.RED : Color.GREEN;
                g2d.setColor(aircraftColor);
                g2d.fillOval(x - 5, y - 5, 10, 10);
                
                // Draw direction line
                int dx = x - centerX;
                int dy = y - centerY;
                double angle = Math.atan2(dy, dx);
                int length = 15;
                int dirX = (int)(x + Math.cos(angle) * length);
                int dirY = (int)(y + Math.sin(angle) * length);
                g2d.drawLine(x, y, dirX, dirY);
                
                // Draw flight info
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
                g2d.drawString(id, x + 10, y);
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
                g2d.drawString(altitude + " ft", x + 10, y + 12);
                g2d.drawString(speed + " kts", x + 10, y + 24);
                
                // Draw trail
                g2d.setColor(new Color(aircraftColor.getRed(), aircraftColor.getGreen(), 
                                     aircraftColor.getBlue(), 50));
                for (int i = 1; i <= 5; i++) {
                    int trailX = (int)(x - Math.cos(angle) * i * 5);
                    int trailY = (int)(y - Math.sin(angle) * i * 5);
                    g2d.fillOval(trailX - 3, trailY - 3, 6, 6);
                }
            }
        };
        
        // Create control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(new Color(44, 62, 80));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Flight list panel
        JPanel flightListPanel = new JPanel(new BorderLayout());
        flightListPanel.setBackground(new Color(44, 62, 80));
        flightListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        flightListPanel.setPreferredSize(new Dimension(250, 600));
        
        JLabel flightListLabel = new JLabel("Vols Actifs");
        flightListLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        flightListLabel.setForeground(Color.WHITE);
        
        String[] flightData = {
            "AL123 - 12000 ft - 500 kts",
            "AL456 - 9500 ft - 480 kts",
            "TU789 - 10200 ft - 510 kts",
            "MA234 - 11500 ft - 500 kts",
            "DZ678 - 10000 ft - 490 kts [CONFLIT]",
            "TU901 - 10100 ft - 510 kts [CONFLIT]"
        };
        JList<String> flightList = new JList<>(flightData);
        flightList.setBackground(new Color(30, 40, 60));
        flightList.setForeground(Color.WHITE);
        flightList.setSelectionBackground(new Color(52, 152, 219));
        flightList.setSelectionForeground(Color.WHITE);
        flightList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                       int index, boolean isSelected, 
                                                       boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value.toString().contains("[CONFLIT]")) {
                    c.setForeground(new Color(231, 76, 60)); // Red for conflict
                } else {
                    c.setForeground(isSelected ? Color.WHITE : new Color(46, 204, 113)); // Green for normal
                }
                return c;
            }
        });
        
        JScrollPane flightListScroll = new JScrollPane(flightList);
        
        JPanel flightButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flightButtonPanel.setOpaque(false);
        
        JButton contactButton = new JButton("Contacter");
        styleButton(contactButton);
        contactButton.addActionListener(e -> {
            int selectedIndex = flightList.getSelectedIndex();
            if (selectedIndex != -1) {
                String flight = flightList.getModel().getElementAt(selectedIndex);
                JOptionPane.showMessageDialog(atcFrame, 
                    "Contact avec " + flight.split(" - ")[0], 
                    "Communication", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(atcFrame, 
                    "Veuillez sélectionner un vol", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton reroute = new JButton("Dérouter");
        styleButton(reroute);
        reroute.addActionListener(e -> {
            int selectedIndex = flightList.getSelectedIndex();
            if (selectedIndex != -1) {
                String flight = flightList.getModel().getElementAt(selectedIndex);
                JOptionPane.showMessageDialog(atcFrame, 
                    "Déroutement du vol " + flight.split(" - ")[0], 
                    "Déroutement", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(atcFrame, 
                    "Veuillez sélectionner un vol", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        flightButtonPanel.add(contactButton);
        flightButtonPanel.add(reroute);
        
        flightListPanel.add(flightListLabel, BorderLayout.NORTH);
        flightListPanel.add(flightListScroll, BorderLayout.CENTER);
        flightListPanel.add(flightButtonPanel, BorderLayout.SOUTH);
        
        // Options panel
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsPanel.setOpaque(false);
        
        JCheckBox showLabelsCheck = new JCheckBox("Étiquettes");
        showLabelsCheck.setSelected(true);
        showLabelsCheck.setForeground(Color.WHITE);
        showLabelsCheck.setOpaque(false);
        
        JCheckBox showTrailsCheck = new JCheckBox("Trajectoires");
        showTrailsCheck.setSelected(true);
        showTrailsCheck.setForeground(Color.WHITE);
        showTrailsCheck.setOpaque(false);
        
        JCheckBox showWeatherCheck = new JCheckBox("Météo");
        showWeatherCheck.setForeground(Color.WHITE);
        showWeatherCheck.setOpaque(false);
        
        optionsPanel.add(showLabelsCheck);
        optionsPanel.add(showTrailsCheck);
        optionsPanel.add(showWeatherCheck);
        
        // Bottom panel for zoom controls
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        zoomPanel.setOpaque(false);
        
        JLabel zoomLabel = new JLabel("Zoom:");
        zoomLabel.setForeground(Color.WHITE);
        
        JSlider zoomSlider = new JSlider(1, 10, 5);
        zoomSlider.setOpaque(false);
        zoomSlider.setPreferredSize(new Dimension(150, 30));
        
        zoomPanel.add(zoomLabel);
        zoomPanel.add(zoomSlider);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton alertButton = new JButton("Alerte");
        styleButton(alertButton);
        alertButton.setBackground(new Color(231, 76, 60));
        alertButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(atcFrame, 
                "ALERTE: Conflit détecté entre DZ678 et TU901!\nDérouter immédiatement.", 
                "ALERTE", JOptionPane.WARNING_MESSAGE);
        });
        
        JButton backButton = new JButton("Retour au Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> {
            atcFrame.dispose();
            frame.setVisible(true);
        });
        
        buttonPanel.add(alertButton);
        buttonPanel.add(backButton);
        
        // Add all panels to the control panel
        controlPanel.add(optionsPanel, BorderLayout.WEST);
        controlPanel.add(zoomPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Organize layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(radarPanel, BorderLayout.CENTER);
        
        // Add all panels to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(flightListPanel, BorderLayout.EAST);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Set up content pane and show
        atcFrame.setContentPane(mainPanel);
        atcFrame.setVisible(true);
        frame.setVisible(false);
    }
    
    private void showLiveStatistics() {
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;
        
        // Create statistics window
        JFrame statsFrame = new JFrame("Statistiques en Direct");
        statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statsFrame.setSize(1280, 720);
        statsFrame.setLocationRelativeTo(frame);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 30, 48));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setPreferredSize(new Dimension(1280, 60));
        
        JLabel titleLabel = new JLabel("Statistiques en Direct");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        // Create refresh rate control
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.setOpaque(false);
        
        JLabel refreshLabel = new JLabel("Taux de rafraîchissement:");
        refreshLabel.setForeground(Color.WHITE);
        
        String[] refreshRates = {"1s", "5s", "10s", "30s", "1m"};
        JComboBox<String> refreshBox = new JComboBox<>(refreshRates);
        refreshBox.setSelectedIndex(1); // Default to 5s
        refreshBox.setBackground(new Color(52, 73, 94));
        refreshBox.setForeground(Color.WHITE);
        
        refreshPanel.add(refreshLabel);
        refreshPanel.add(refreshBox);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshPanel, BorderLayout.EAST);
        
        // Create content panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(44, 62, 80));
        tabbedPane.setForeground(Color.WHITE);
        
        // Flight Statistics Tab
        JPanel flightStatsPanel = new JPanel(new BorderLayout());
        flightStatsPanel.setBackground(new Color(36, 59, 85));
        flightStatsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel chartsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        chartsPanel.setOpaque(false);
        
        // Flight status chart
        JPanel statusChartPanel = new JPanel(new BorderLayout());
        statusChartPanel.setBackground(new Color(44, 62, 80));
        statusChartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219)),
                "Statut des Vols",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel statusChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw pie chart
                int width = getWidth() - 100;
                int height = width;
                int x = (getWidth() - width) / 2;
                int y = 10;
                
                // Draw chart segments
                g2d.setColor(new Color(52, 152, 219)); // Blue for In Flight
                g2d.fillArc(x, y, width, height, 0, 150);
                
                g2d.setColor(new Color(46, 204, 113)); // Green for On Time
                g2d.fillArc(x, y, width, height, 150, 130);
                
                g2d.setColor(new Color(241, 196, 15)); // Yellow for Delayed
                g2d.fillArc(x, y, width, height, 280, 50);
                
                g2d.setColor(new Color(231, 76, 60)); // Red for Cancelled
                g2d.fillArc(x, y, width, height, 330, 30);
                
                // Draw center circle (donut chart)
                g2d.setColor(new Color(44, 62, 80));
                g2d.fillOval(x + width/4, y + height/4, width/2, height/2);
                
                // Draw legend
                int legendY = y + height + 20;
                int legendX = x + 20;
                
                g2d.setColor(new Color(52, 152, 219));
                g2d.fillRect(legendX, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("En vol (42%)", legendX + 20, legendY + 12);
                
                legendX += 120;
                g2d.setColor(new Color(46, 204, 113));
                g2d.fillRect(legendX, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("À l'heure (36%)", legendX + 20, legendY + 12);
                
                legendX = x + 20;
                legendY += 25;
                g2d.setColor(new Color(241, 196, 15));
                g2d.fillRect(legendX, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Retardé (14%)", legendX + 20, legendY + 12);
                
                legendX += 120;
                g2d.setColor(new Color(231, 76, 60));
                g2d.fillRect(legendX, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Annulé (8%)", legendX + 20, legendY + 12);
            }
        };
        
        statusChartPanel.add(statusChart, BorderLayout.CENTER);
        
        // Passenger load chart
        JPanel loadChartPanel = new JPanel(new BorderLayout());
        loadChartPanel.setBackground(new Color(44, 62, 80));
        loadChartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219)),
                "Taux de Remplissage",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel loadChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw bar chart
                int barWidth = 60;
                int maxHeight = getHeight() - 80;
                int x = 50;
                int y = getHeight() - 40;
                
                // Draw Y axis
                g2d.setColor(Color.WHITE);
                g2d.drawLine(30, 20, 30, y);
                g2d.drawLine(30, y, getWidth() - 30, y);
                
                // Draw Y axis labels
                g2d.drawString("100%", 5, 25);
                g2d.drawString("75%", 10, y - maxHeight * 3/4);
                g2d.drawString("50%", 10, y - maxHeight * 2/4);
                g2d.drawString("25%", 10, y - maxHeight * 1/4);
                g2d.drawString("0%", 15, y);
                
                // Draw bars
                String[] routes = {"ALG-CDG", "CDG-ALG", "ALG-TUN", "ALG-IST", "IST-ALG"};
                int[] loads = {92, 78, 65, 84, 71};
                
                for (int i = 0; i < routes.length; i++) {
                    int barHeight = (int)(maxHeight * loads[i] / 100.0);
                    int barX = x + i * (barWidth + 40);
                    
                    // Draw bar with gradient
                    GradientPaint gradient = new GradientPaint(
                        barX, y - barHeight, new Color(41, 128, 185),
                        barX, y, new Color(52, 152, 219)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(barX, y - barHeight, barWidth, barHeight);
                    
                    // Draw border
                    g2d.setColor(Color.WHITE);
                    g2d.drawRect(barX, y - barHeight, barWidth, barHeight);
                    
                    // Draw label and percentage
                    g2d.drawString(routes[i], barX + 5, y + 15);
                    g2d.drawString(loads[i] + "%", barX + 15, y - barHeight - 5);
                }
            }
        };
        
        loadChartPanel.add(loadChart, BorderLayout.CENTER);
        
        // Route popularity chart
        JPanel routeChartPanel = new JPanel(new BorderLayout());
        routeChartPanel.setBackground(new Color(44, 62, 80));
        routeChartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219)),
                "Routes les Plus Populaires",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel routeChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw horizontal bar chart
                int barHeight = 30;
                int maxWidth = getWidth() - 150;
                int x = 120;
                int y = 40;
                
                // Draw bars
                String[] routes = {"Alger - Paris", "Paris - Alger", "Alger - Istanbul", 
                                "Alger - Tunis", "Oran - Alger"};
                int[] passengers = {1250, 1180, 780, 650, 430};
                int maxPassengers = 1500; // Scale
                
                for (int i = 0; i < routes.length; i++) {
                    int barWidth = (int)(maxWidth * passengers[i] / (double)maxPassengers);
                    int barY = y + i * (barHeight + 20);
                    
                    // Draw label
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(routes[i], 10, barY + 20);
                    
                    // Draw bar with gradient
                    GradientPaint gradient = new GradientPaint(
                        x, barY, new Color(155, 89, 182),
                        x + barWidth, barY, new Color(142, 68, 173)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(x, barY, barWidth, barHeight);
                    
                    // Draw value
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(passengers[i] + " pax", x + barWidth + 10, barY + 20);
                }
            }
        };
        
        routeChartPanel.add(routeChart, BorderLayout.CENTER);
        
        // Revenue chart
        JPanel revenueChartPanel = new JPanel(new BorderLayout());
        revenueChartPanel.setBackground(new Color(44, 62, 80));
        revenueChartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219)),
                "Revenus par Classe (Millions DA)",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel revenueChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw line chart
                int width = getWidth() - 80;
                int height = getHeight() - 80;
                int x = 50;
                int y = getHeight() - 40;
                
                // Draw axes
                g2d.setColor(Color.WHITE);
                g2d.drawLine(x, 20, x, y);
                g2d.drawLine(x, y, x + width, y);
                
                // Draw Y axis labels
                g2d.drawString("50", x - 25, 25);
                g2d.drawString("40", x - 25, y - height * 4/5);
                g2d.drawString("30", x - 25, y - height * 3/5);
                g2d.drawString("20", x - 25, y - height * 2/5);
                g2d.drawString("10", x - 25, y - height * 1/5);
                g2d.drawString("0", x - 15, y);
                
                // Draw X axis labels (months)
                String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin"};
                int segmentWidth = width / (months.length - 1);
                
                for (int i = 0; i < months.length; i++) {
                    int labelX = x + i * segmentWidth;
                    g2d.drawString(months[i], labelX - 10, y + 20);
                    g2d.drawLine(labelX, y, labelX, y + 5); // Tick mark
                }
                
                // Draw data lines
                int[][] data = {
                    {12, 15, 14, 18, 22, 25}, // Economy class
                    {18, 17, 19, 22, 24, 28}, // Business class
                    {8, 9, 10, 11, 13, 15}    // First class
                };
                
                Color[] colors = {
                    new Color(46, 204, 113), // Green for Economy
                    new Color(52, 152, 219), // Blue for Business
                    new Color(155, 89, 182)  // Purple for First
                };
                
                // Draw each line
                for (int line = 0; line < data.length; line++) {
                    int[] values = data[line];
                    
                    // Create line points
                    int[] xPoints = new int[values.length];
                    int[] yPoints = new int[values.length];
                    
                    for (int i = 0; i < values.length; i++) {
                        xPoints[i] = x + i * segmentWidth;
                        yPoints[i] = y - (int)(height * values[i] / 50.0); // Scale to max value 50
                    }
                    
                    // Draw line
                    g2d.setColor(colors[line]);
                    g2d.setStroke(new BasicStroke(3));
                    
                    for (int i = 0; i < xPoints.length - 1; i++) {
                        g2d.drawLine(xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
                    }
                    
                    // Draw points
                    for (int i = 0; i < xPoints.length; i++) {
                        g2d.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
                        g2d.drawString(String.valueOf(values[i]), xPoints[i] - 8, yPoints[i] - 10);
                    }
                }
                
                // Draw legend
                int legendY = 30;
                int legendX = x + width - 130;
                
                g2d.setColor(colors[0]);
                g2d.fillRect(legendX, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Économique", legendX + 20, legendY + 12);
                
                legendY += 20;
                g2d.setColor(colors[1]);
                g2d.fillRect(legendX, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Business", legendX + 20, legendY + 12);
                
                legendY += 20;
                g2d.setColor(colors[2]);
                g2d.fillRect(legendX, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Première", legendX + 20, legendY + 12);
            }
        };
        
        revenueChartPanel.add(revenueChart, BorderLayout.CENTER);
        
        // Add charts to panel
        chartsPanel.add(statusChartPanel);
        chartsPanel.add(loadChartPanel);
        chartsPanel.add(routeChartPanel);
        chartsPanel.add(revenueChartPanel);
        
        flightStatsPanel.add(chartsPanel, BorderLayout.CENTER);
        
        // Create summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Flight count summary card
        JPanel flightCountCard = createSummaryCard("Vols Aujourd'hui", "87", 
                                                new Color(52, 152, 219), "↑ 12% vs hier");
        
        // Passenger count summary card
        JPanel passengerCountCard = createSummaryCard("Passagers Aujourd'hui", "3,456", 
                                                    new Color(46, 204, 113), "↑ 8% vs hier");
        
        // On-time performance card
        JPanel onTimeCard = createSummaryCard("Ponctualité", "86%", 
                                            new Color(241, 196, 15), "↓ 2% vs hier");
        
        // Revenue summary card
        JPanel revenueCard = createSummaryCard("Revenus (Aujourd'hui)", "4.5M DA", 
                                            new Color(155, 89, 182), "↑ 15% vs hier");
        
        summaryPanel.add(flightCountCard);
        summaryPanel.add(passengerCountCard);
        summaryPanel.add(onTimeCard);
        summaryPanel.add(revenueCard);
        
        flightStatsPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Create Airport Traffic Tab
        JPanel airportStatsPanel = new JPanel(new BorderLayout());
        airportStatsPanel.setBackground(new Color(36, 59, 85));
        
        // Add tabs to the tabbed pane
        tabbedPane.addTab("Statistiques des Vols", flightStatsPanel);
        tabbedPane.addTab("Trafic Aéroportuaire", airportStatsPanel);
        
        // Create button panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(new Color(44, 62, 80));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton exportButton = new JButton("Exporter (PDF)");
        styleButton(exportButton);
        exportButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(statsFrame, 
                "Exportation des statistiques en PDF", 
                "Exportation", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton printButton = new JButton("Imprimer");
        styleButton(printButton);
        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(statsFrame, 
                "Impression des statistiques", 
                "Impression", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton backButton = new JButton("Retour au Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> {
            statsFrame.dispose();
            frame.setVisible(true);
        });
        
        controlPanel.add(exportButton);
        controlPanel.add(printButton);
        controlPanel.add(backButton);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Add timer to simulate live updates
        Timer updateTimer = new Timer(5000, e -> {
            repaintCharts(statusChart, loadChart, routeChart, revenueChart);
        });
        updateTimer.start();
        
        // Add refresh rate listener
        refreshBox.addActionListener(e -> {
            String rate = (String)refreshBox.getSelectedItem();
            int delay = 5000; // Default 5s
            
            if (rate.equals("1s")) {
                delay = 1000;
            } else if (rate.equals("10s")) {
                delay = 10000;
            } else if (rate.equals("30s")) {
                delay = 30000;
            } else if (rate.equals("1m")) {
                delay = 60000;
            }
            
            updateTimer.setDelay(delay);
            
            JOptionPane.showMessageDialog(statsFrame, 
                "Taux de rafraîchissement mis à jour: " + rate, 
                "Paramètres", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Set content pane and show
        statsFrame.setContentPane(mainPanel);
        statsFrame.setVisible(true);
        frame.setVisible(false);
    }
    
    // Helper method to create summary cards
    private JPanel createSummaryCard(String title, String value, Color color, String change) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(44, 62, 80));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(color);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        
        JLabel changeLabel = new JLabel(change);
        changeLabel.setForeground(change.contains("↑") ? 
                                new Color(46, 204, 113) : new Color(231, 76, 60));
        changeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(changeLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    // Helper method to repaint charts for animation
    private void repaintCharts(JPanel... charts) {
        for (JPanel chart : charts) {
            chart.repaint();
        }
    }
    
    private void showPassengerManagement() {
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;
        
        // Create passenger management window
        JFrame passengerFrame = new JFrame("Gestion des Passagers");
        passengerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        passengerFrame.setSize(1280, 720);
        passengerFrame.setLocationRelativeTo(frame);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 30, 48));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setPreferredSize(new Dimension(1280, 60));
        
        JLabel titleLabel = new JLabel("Gestion des Passagers");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        
        JButton searchButton = new JButton("Rechercher");
        styleButton(searchButton);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(36, 59, 85));
        
        // Create passenger table
        String[] columnNames = {"ID", "Nom", "Prénom", "Date de Naissance", "Nationalité", "Passeport", "Contact"};
        Object[][] data = {
            {"P001", "Ahmed", "Mohamed", "15/03/1985", "Algérien", "AH12345678", "+213 555 123 456"},
            {"P002", "Ben Ali", "Karim", "22/07/1990", "Tunisien", "TU87654321", "+216 555 234 567"},
            {"P003", "Mansouri", "Lina", "30/11/1988", "Marocaine", "MA23456789", "+212 555 345 678"},
            {"P004", "Touré", "Amadou", "14/05/1975", "Sénégalais", "SN34567890", "+221 555 456 789"},
            {"P005", "Bouazizi", "Fatima", "08/12/1995", "Tunisienne", "TU45678901", "+216 555 567 890"},
            {"P006", "Benali", "Omar", "25/09/1980", "Algérien", "AH56789012", "+213 555 678 901"},
            {"P007", "El Fassi", "Nadia", "17/04/1992", "Marocaine", "MA67890123", "+212 555 789 012"},
            {"P008", "Diallo", "Ibrahim", "03/08/1970", "Guinéen", "GN78901234", "+224 555 890 123"}
        };
        
        JTable passengerTable = new JTable(data, columnNames);
        passengerTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passengerTable.setBackground(new Color(20, 30, 48));
        passengerTable.setForeground(Color.WHITE);
        passengerTable.setGridColor(new Color(52, 73, 94));
        passengerTable.setRowHeight(30);
        passengerTable.setSelectionBackground(new Color(52, 152, 219));
        passengerTable.setSelectionForeground(Color.WHITE);
        passengerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        passengerTable.getTableHeader().setBackground(new Color(44, 62, 80));
        passengerTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(passengerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219)));
        
        // Create control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(new Color(44, 62, 80));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create filter panel (left)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setOpaque(false);
        
        JLabel filterLabel = new JLabel("Filtrer par Nationalité:");
        filterLabel.setForeground(Color.WHITE);
        
        String[] filterOptions = {"Tous", "Algérien", "Tunisien", "Marocain", "Autres"};
        JComboBox<String> filterBox = new JComboBox<>(filterOptions);
        filterBox.setBackground(new Color(52, 73, 94));
        filterBox.setForeground(Color.WHITE);
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterBox);
        
        // Create button panel (right)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);
        
        JButton addButton = new JButton("Ajouter Passager");
        styleButton(addButton);
        addButton.addActionListener(e -> {
            // Open add passenger dialog
            JOptionPane.showMessageDialog(passengerFrame, 
                "Ajout d'un nouveau passager", 
                "Ajout", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton editButton = new JButton("Modifier");
        styleButton(editButton);
        editButton.addActionListener(e -> {
            int selectedRow = passengerTable.getSelectedRow();
            if (selectedRow != -1) {
                // Open edit passenger dialog
                JOptionPane.showMessageDialog(passengerFrame, 
                    "Modification du passager " + passengerTable.getValueAt(selectedRow, 0), 
                    "Édition", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(passengerFrame, 
                    "Veuillez sélectionner un passager à modifier", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton deleteButton = new JButton("Supprimer");
        styleButton(deleteButton);
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.addActionListener(e -> {
            int selectedRow = passengerTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(passengerFrame, 
                    "Êtes-vous sûr de vouloir supprimer ce passager ?", 
                    "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Delete passenger
                    JOptionPane.showMessageDialog(passengerFrame, 
                        "Passager supprimé avec succès!", 
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(passengerFrame, 
                    "Veuillez sélectionner un passager à supprimer", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton backButton = new JButton("Retour au Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> {
            passengerFrame.dispose();
            frame.setVisible(true);
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        
        // Add panels to the control panel
        controlPanel.add(filterPanel, BorderLayout.WEST);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create passenger details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(52, 73, 94));
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(52, 152, 219)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        detailsPanel.setPreferredSize(new Dimension(1280, 180));
        
        // Create tabs for passenger details
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(44, 62, 80));
        tabbedPane.setForeground(Color.WHITE);
        
        // Information personnelle tab
        JPanel infoPanel = new JPanel(new GridLayout(3, 4, 20, 10));
        infoPanel.setBackground(new Color(44, 62, 80));
        
        String[] infoLabels = {
            "Nom Complet:", "Ahmed Mohamed",
            "Date de Naissance:", "15/03/1985",
            "Nationalité:", "Algérien",
            "Passeport:", "AH12345678",
            "Adresse:", "123 Rue des Oliviers, Alger",
            "Téléphone:", "+213 555 123 456",
            "Email:", "ahmed.mohamed@email.com",
            "Numéro Fidélité:", "ALG12345"
        };
        
        for (int i = 0; i < infoLabels.length; i++) {
            JLabel label = new JLabel(infoLabels[i]);
            label.setForeground(Color.WHITE);
            if (i % 2 == 0) {
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
            infoPanel.add(label);
        }
        
        // Historique des vols tab
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(44, 62, 80));
        
        String[] historyColumns = {"Vol", "Date", "Origine", "Destination", "Classe", "Statut"};
        Object[][] historyData = {
            {"AL123", "05/01/2023", "Alger (ALG)", "Paris (CDG)", "Économique", "Complété"},
            {"AL456", "12/03/2023", "Paris (CDG)", "Alger (ALG)", "Business", "Complété"},
            {"TU789", "24/04/2023", "Alger (ALG)", "Tunis (TUN)", "Économique", "Complété"},
            {"MA234", "15/06/2023", "Alger (ALG)", "Casablanca (CMN)", "Première", "Réservé"}
        };
        
        JTable historyTable = new JTable(historyData, historyColumns);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyTable.setBackground(new Color(30, 40, 60));
        historyTable.setForeground(Color.WHITE);
        historyTable.setGridColor(new Color(52, 73, 94));
        historyTable.setRowHeight(30);
        
        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyPanel.add(historyScroll, BorderLayout.CENTER);
        
        // Préférences tab
        JPanel preferencesPanel = new JPanel(new GridLayout(4, 2, 20, 10));
        preferencesPanel.setBackground(new Color(44, 62, 80));
        preferencesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] prefLabels = {
            "Siège préféré:", "Fenêtre",
            "Repas spécial:", "Végétarien",
            "Classe préférée:", "Business",
            "Assistance spéciale:", "Aucune"
        };
        
        for (int i = 0; i < prefLabels.length; i++) {
            JLabel label = new JLabel(prefLabels[i]);
            label.setForeground(Color.WHITE);
            if (i % 2 == 0) {
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
            preferencesPanel.add(label);
        }
        
        tabbedPane.addTab("Information Personnelle", infoPanel);
        tabbedPane.addTab("Historique des Vols", historyPanel);
        tabbedPane.addTab("Préférences", preferencesPanel);
        
        detailsPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add components to the content panel
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(detailsPanel, BorderLayout.SOUTH);
        
        // Add all panels to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Set up passenger selection listener
        passengerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = passengerTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Update passenger details with selected passenger data
                    String fullName = passengerTable.getValueAt(selectedRow, 1) + " " + 
                                     passengerTable.getValueAt(selectedRow, 2);
                    ((JLabel)infoPanel.getComponent(1)).setText(fullName);
                    ((JLabel)infoPanel.getComponent(3)).setText((String)passengerTable.getValueAt(selectedRow, 3));
                    ((JLabel)infoPanel.getComponent(5)).setText((String)passengerTable.getValueAt(selectedRow, 4));
                    ((JLabel)infoPanel.getComponent(7)).setText((String)passengerTable.getValueAt(selectedRow, 5));
                }
            }
        });
        
        passengerFrame.setContentPane(mainPanel);
        passengerFrame.setVisible(true);
        frame.setVisible(false);
    }
    
    private void showBookingManagement() {
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;
        
        // Create booking management window
        JFrame bookingFrame = new JFrame("Gestion des Réservations");
        bookingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bookingFrame.setSize(1280, 720);
        bookingFrame.setLocationRelativeTo(frame);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 30, 48));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setPreferredSize(new Dimension(1280, 60));
        
        JLabel titleLabel = new JLabel("Gestion des Réservations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        
        JButton searchButton = new JButton("Rechercher");
        styleButton(searchButton);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(36, 59, 85));
        
        // Create booking table
        String[] columnNames = {"ID", "Passager", "Vol", "Date", "Origine", "Destination", "Classe", "Statut", "Prix"};
        Object[][] data = {
            {"R001", "Ahmed Mohamed", "AL123", "05/01/2023", "Alger (ALG)", "Paris (CDG)", "Économique", "Confirmé", "25000 DA"},
            {"R002", "Karim Ben Ali", "AL456", "12/03/2023", "Paris (CDG)", "Alger (ALG)", "Business", "Confirmé", "45000 DA"},
            {"R003", "Lina Mansouri", "TU789", "24/04/2023", "Alger (ALG)", "Tunis (TUN)", "Économique", "Confirmé", "18000 DA"},
            {"R004", "Omar Benali", "MA234", "15/06/2023", "Alger (ALG)", "Casablanca (CMN)", "Première", "En attente", "65000 DA"},
            {"R005", "Fatima Bouazizi", "AL345", "07/07/2023", "Istanbul (IST)", "Alger (ALG)", "Économique", "Annulé", "22000 DA"},
            {"R006", "Ibrahim Diallo", "DZ678", "18/07/2023", "Oran (ORN)", "Alger (ALG)", "Économique", "En attente", "8000 DA"},
            {"R007", "Nadia El Fassi", "TU901", "25/07/2023", "Lyon (LYS)", "Tunis (TUN)", "Business", "Confirmé", "38000 DA"},
            {"R008", "Amadou Touré", "MA432", "02/08/2023", "Rabat (RBA)", "Paris (ORY)", "Économique", "Confirmé", "27000 DA"}
        };
        
        JTable bookingTable = new JTable(data, columnNames) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                String status = (String) getModel().getValueAt(row, 7);
                if (status.equals("Confirmé")) {
                    comp.setForeground(new Color(46, 204, 113)); // Green
                } else if (status.equals("En attente")) {
                    comp.setForeground(new Color(241, 196, 15)); // Yellow
                } else if (status.equals("Annulé")) {
                    comp.setForeground(new Color(231, 76, 60)); // Red
                } else {
                    comp.setForeground(Color.WHITE);
                }
                return comp;
            }
        };
        bookingTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingTable.setBackground(new Color(20, 30, 48));
        bookingTable.setForeground(Color.WHITE);
        bookingTable.setGridColor(new Color(52, 73, 94));
        bookingTable.setRowHeight(30);
        bookingTable.setSelectionBackground(new Color(52, 152, 219));
        bookingTable.setSelectionForeground(Color.WHITE);
        bookingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookingTable.getTableHeader().setBackground(new Color(44, 62, 80));
        bookingTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219)));
        
        // Create filter and statistics panel
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(new Color(52, 73, 94));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsPanel.setPreferredSize(new Dimension(300, 600));
        
        JPanel filterPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        filterPanel.setBackground(new Color(44, 62, 80));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219)),
                "Filtres",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel statusLabel = new JLabel("Statut:");
        statusLabel.setForeground(Color.WHITE);
        
        String[] statusOptions = {"Tous", "Confirmé", "En attente", "Annulé"};
        JComboBox<String> statusBox = new JComboBox<>(statusOptions);
        statusBox.setBackground(new Color(30, 40, 60));
        statusBox.setForeground(Color.WHITE);
        
        JLabel classLabel = new JLabel("Classe:");
        classLabel.setForeground(Color.WHITE);
        
        String[] classOptions = {"Toutes", "Économique", "Business", "Première"};
        JComboBox<String> classBox = new JComboBox<>(classOptions);
        classBox.setBackground(new Color(30, 40, 60));
        classBox.setForeground(Color.WHITE);
        
        JButton applyFilterButton = new JButton("Appliquer");
        styleButton(applyFilterButton);
        applyFilterButton.setPreferredSize(new Dimension(100, 30));
        applyFilterButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(bookingFrame, 
                "Filtres appliqués", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });
        
        filterPanel.add(statusLabel);
        filterPanel.add(statusBox);
        filterPanel.add(classLabel);
        filterPanel.add(classBox);
        filterPanel.add(applyFilterButton);
        
        // Statistics panel
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(new Color(44, 62, 80));
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219)),
                "Statistiques",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Create chart
        JPanel pieChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw pie chart
                int width = getWidth() - 40;
                int height = width;
                int x = (getWidth() - width) / 2;
                int y = 10;
                
                // Draw chart segments
                g2d.setColor(new Color(46, 204, 113)); // Green for Confirmed
                g2d.fillArc(x, y, width, height, 0, 180);
                
                g2d.setColor(new Color(241, 196, 15)); // Yellow for Pending
                g2d.fillArc(x, y, width, height, 180, 90);
                
                g2d.setColor(new Color(231, 76, 60)); // Red for Cancelled
                g2d.fillArc(x, y, width, height, 270, 90);
                
                // Draw center circle (donut chart)
                g2d.setColor(new Color(44, 62, 80));
                g2d.fillOval(x + width/4, y + height/4, width/2, height/2);
                
                // Draw legend
                int legendY = y + height + 20;
                
                g2d.setColor(new Color(46, 204, 113));
                g2d.fillRect(x, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Confirmé (50%)", x + 20, legendY + 12);
                
                legendY += 20;
                g2d.setColor(new Color(241, 196, 15));
                g2d.fillRect(x, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("En attente (25%)", x + 20, legendY + 12);
                
                legendY += 20;
                g2d.setColor(new Color(231, 76, 60));
                g2d.fillRect(x, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Annulé (25%)", x + 20, legendY + 12);
            }
        };
        
        chartPanel.add(pieChart, BorderLayout.CENTER);
        
        // Add panels to stats panel
        statsPanel.add(filterPanel, BorderLayout.NORTH);
        statsPanel.add(chartPanel, BorderLayout.CENTER);
        
        // Create booking details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(52, 73, 94));
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(52, 152, 219)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        detailsPanel.setPreferredSize(new Dimension(980, 180));
        
        // Create tabs for booking details
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(44, 62, 80));
        tabbedPane.setForeground(Color.WHITE);
        
        // Détails de la réservation tab
        JPanel bookingInfoPanel = new JPanel(new GridLayout(3, 4, 20, 10));
        bookingInfoPanel.setBackground(new Color(44, 62, 80));
        
        String[] bookingInfoLabels = {
            "ID Réservation:", "R001",
            "Passager:", "Ahmed Mohamed",
            "Vol:", "AL123",
            "Date:", "05/01/2023",
            "Origine:", "Alger (ALG)",
            "Destination:", "Paris (CDG)",
            "Classe:", "Économique",
            "Statut:", "Confirmé",
            "Prix:", "25000 DA",
            "Mode de Paiement:", "Carte Bancaire",
            "Date de Réservation:", "01/01/2023",
            "Agent:", "Nadir Lahlou"
        };
        
        for (int i = 0; i < bookingInfoLabels.length; i++) {
            JLabel label = new JLabel(bookingInfoLabels[i]);
            label.setForeground(Color.WHITE);
            if (i % 2 == 0) {
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
            bookingInfoPanel.add(label);
        }
        
        // Options de Siège tab
        JPanel seatPanel = new JPanel(new BorderLayout());
        seatPanel.setBackground(new Color(44, 62, 80));
        
        JPanel seatMapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw seat map
                int seatWidth = 30;
                int seatHeight = 30;
                int spacing = 5;
                int startX = 50;
                int startY = 20;
                
                // Draw airplane outline
                g2d.setColor(new Color(52, 152, 219, 100));
                g2d.fillRoundRect(startX - 20, startY - 20, 
                                 7 * (seatWidth + spacing) + 40, 
                                 10 * (seatHeight + spacing) + 40, 
                                 50, 50);
                
                // Draw seats
                for (int row = 0; row < 10; row++) {
                    for (int col = 0; col < 6; col++) {
                        // Skip aisle
                        if (col == 3) continue;
                        
                        int x = startX + col * (seatWidth + spacing);
                        if (col > 3) x += spacing * 2; // Add aisle space
                        
                        int y = startY + row * (seatHeight + spacing);
                        
                        // Color seats based on status
                        if (row == 4 && col == 0) {
                            // Selected seat
                            g2d.setColor(new Color(46, 204, 113));
                        } else if ((row < 2) || 
                                  (row == 7 && col == 5) || 
                                  (row == 3 && col == 1) || 
                                  (row == 5 && col == 4)) {
                            // Reserved seats
                            g2d.setColor(new Color(231, 76, 60));
                        } else {
                            // Available seats
                            g2d.setColor(new Color(52, 152, 219));
                        }
                        
                        g2d.fillRoundRect(x, y, seatWidth, seatHeight, 5, 5);
                        
                        // Draw seat number
                        g2d.setColor(Color.WHITE);
                        String seatNum = (row+1) + "" + (char)('A' + (col >= 3 ? col+1 : col));
                        g2d.drawString(seatNum, x + 5, y + 20);
                    }
                }
                
                // Draw legend
                int legendY = startY + 10 * (seatHeight + spacing) + 20;
                g2d.setColor(new Color(52, 152, 219));
                g2d.fillRect(startX, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Disponible", startX + 20, legendY + 12);
                
                g2d.setColor(new Color(231, 76, 60));
                g2d.fillRect(startX + 120, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Occupé", startX + 140, legendY + 12);
                
                g2d.setColor(new Color(46, 204, 113));
                g2d.fillRect(startX + 220, legendY, 15, 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Sélectionné", startX + 240, legendY + 12);
            }
        };
        
        seatPanel.add(seatMapPanel, BorderLayout.CENTER);
        
        // Add tabs
        tabbedPane.addTab("Détails de la Réservation", bookingInfoPanel);
        tabbedPane.addTab("Plan de Siège", seatPanel);
        
        detailsPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(new Color(44, 62, 80));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);
        
        JButton addButton = new JButton("Nouvelle Réservation");
        styleButton(addButton);
        addButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(bookingFrame, 
                "Création d'une nouvelle réservation", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton confirmButton = new JButton("Confirmer");
        styleButton(confirmButton);
        confirmButton.addActionListener(e -> {
            int selectedRow = bookingTable.getSelectedRow();
            if (selectedRow != -1) {
                bookingTable.setValueAt("Confirmé", selectedRow, 7);
                ((JLabel)bookingInfoPanel.getComponent(15)).setText("Confirmé");
                JOptionPane.showMessageDialog(bookingFrame, 
                    "Réservation confirmée avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(bookingFrame, 
                    "Veuillez sélectionner une réservation", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Annuler");
        styleButton(cancelButton);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.addActionListener(e -> {
            int selectedRow = bookingTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(bookingFrame, 
                    "Êtes-vous sûr de vouloir annuler cette réservation ?", 
                    "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    bookingTable.setValueAt("Annulé", selectedRow, 7);
                    ((JLabel)bookingInfoPanel.getComponent(15)).setText("Annulé");
                }
            } else {
                JOptionPane.showMessageDialog(bookingFrame, 
                    "Veuillez sélectionner une réservation", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton backButton = new JButton("Retour au Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> {
            bookingFrame.dispose();
            frame.setVisible(true);
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);
        
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Set up booking selection listener
        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = bookingTable.getSelectedRow();
                if (selectedRow != -1) {
                    // Update booking details with selected booking data
                    ((JLabel)bookingInfoPanel.getComponent(1)).setText((String)bookingTable.getValueAt(selectedRow, 0));
                    ((JLabel)bookingInfoPanel.getComponent(3)).setText((String)bookingTable.getValueAt(selectedRow, 1));
                    ((JLabel)bookingInfoPanel.getComponent(5)).setText((String)bookingTable.getValueAt(selectedRow, 2));
                    ((JLabel)bookingInfoPanel.getComponent(7)).setText((String)bookingTable.getValueAt(selectedRow, 3));
                    ((JLabel)bookingInfoPanel.getComponent(9)).setText((String)bookingTable.getValueAt(selectedRow, 4));
                    ((JLabel)bookingInfoPanel.getComponent(11)).setText((String)bookingTable.getValueAt(selectedRow, 5));
                    ((JLabel)bookingInfoPanel.getComponent(13)).setText((String)bookingTable.getValueAt(selectedRow, 6));
                    ((JLabel)bookingInfoPanel.getComponent(15)).setText((String)bookingTable.getValueAt(selectedRow, 7));
                    ((JLabel)bookingInfoPanel.getComponent(17)).setText((String)bookingTable.getValueAt(selectedRow, 8));
                }
            }
        });
        
        // Add components to the content panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(detailsPanel, BorderLayout.SOUTH);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(statsPanel, BorderLayout.EAST);
        
        // Add all panels to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        bookingFrame.setContentPane(mainPanel);
        bookingFrame.setVisible(true);
        frame.setVisible(false);
    }
    
    private void showMaintenance() {
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;
        
        JOptionPane.showMessageDialog(frame,
            "Le Module de Maintenance est fonctionnel!\nPlanificateur de maintenance activé.",
            "Module Chargé",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showFlightSimulation() {
        JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;
        
        JOptionPane.showMessageDialog(frame,
            "Le Module de Simulation de Vol est fonctionnel!\nCartes et trajectoires chargées.",
            "Module Chargé",
            JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Draw background image if available
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Draw gradient background if no image
            GradientPaint gradient = new GradientPaint(0, 0, new Color(20, 30, 48),
                                                     getWidth(), getHeight(), new Color(36, 59, 85));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Draw logo with shadow
        if (logoImage != null) {
            int logoWidth = (int)(logoImage.getWidth() * logoScale);
            int logoHeight = (int)(logoImage.getHeight() * logoScale);
            int logoX = (getWidth() - logoWidth) / 2;
            int logoY = 100;
            
            // Draw shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillOval(logoX + 5, logoY + 5, logoWidth, logoHeight);
            
            // Draw logo
            g2d.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, this);
        }
        
        // Draw menu buttons
        int buttonHeight = 65;
        int spacing = 20;
        int totalHeight = menuItems.length * (buttonHeight + spacing);
        int startY = (getHeight() - totalHeight) / 2 + 50;
        int buttonWidth = 400;
        int x = (getWidth() - buttonWidth) / 2;
        
        for (int i = 0; i < menuItems.length; i++) {
            int y = startY + i * (buttonHeight + spacing);
            float scale = buttonScales[i];
            int scaledWidth = (int)(buttonWidth * scale);
            int scaledHeight = (int)(buttonHeight * scale);
            int scaledX = x + (buttonWidth - scaledWidth) / 2;
            int scaledY = y + (buttonHeight - scaledHeight) / 2;
            
            // Draw button shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRoundRect(scaledX + 2, scaledY + 2, scaledWidth, scaledHeight, 15, 15);
            
            // Draw button background with gradient
            GradientPaint buttonGradient = new GradientPaint(
                scaledX, scaledY, new Color(0, 0, 0, 180),
                scaledX, scaledY + scaledHeight, new Color(0, 0, 0, 150)
            );
            g2d.setPaint(buttonGradient);
            g2d.fillRoundRect(scaledX, scaledY, scaledWidth, scaledHeight, 15, 15);
            
            // Draw button border
            if (i == buttonHover) {
                g2d.setColor(buttonColors[i]);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(scaledX, scaledY, scaledWidth, scaledHeight, 15, 15);
                
                // Draw glow effect
                g2d.setColor(new Color(buttonColors[i].getRed(), buttonColors[i].getGreen(), 
                                    buttonColors[i].getBlue(), 30));
                g2d.fillRoundRect(scaledX - 5, scaledY - 5, scaledWidth + 10, scaledHeight + 10, 20, 20);
            }
            
            // Draw button text with shadow
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
            FontMetrics fm = g2d.getFontMetrics();
            String text = menuIcons[i] + " " + menuItems[i];
            int textX = scaledX + (scaledWidth - fm.stringWidth(text)) / 2;
            int textY = scaledY + (scaledHeight + fm.getAscent() - fm.getDescent()) / 2;
            g2d.drawString(text, textX + 1, textY + 1);
            
            // Draw button text
            g2d.setColor(Color.WHITE);
            g2d.drawString(text, textX, textY);
        }
    }

    // Add method to return to menu
    public static void returnToMenu(JFrame frame) {
        MainMenu menu = new MainMenu(frame);
        frame.setContentPane(menu);
        frame.setVisible(true);
    }
} 