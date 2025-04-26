import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MenuPage extends JPanel {
    private static final Color BUTTON_COLOR = new Color(0, 122, 204);
    private static final Color BUTTON_HOVER_COLOR = new Color(0, 153, 255);
    private static final Color BUTTON_PRESS_COLOR = new Color(0, 102, 179);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 48);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final int BUTTON_HEIGHT = 60;
    private static final int BUTTON_WIDTH = 250;
    private static final int BUTTON_SPACING = 20;
    
    private JFrame parentFrame;
    private Image backgroundImage;
    private float titleY = -50;
    private float buttonOpacity = 0;
    private Timer animationTimer;
    
    private JButton simulateButton;
    private JButton quitButton;
    
    public MenuPage(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(null);
        
        // Charger l'image de fond
        try {
            backgroundImage = new ImageIcon("resources/pixel_plane_bg.png").getImage();
        } catch (Exception e) {
            System.err.println("Impossible de charger l'image de fond");
        }
        
        createButtons();
        setupAnimations();
        
        setFocusable(true);
        requestFocusInWindow();
    }
    
    private void setupAnimations() {
        animationTimer = new Timer(1000/60, e -> {
            // Animation du titre
            if (titleY < 100) {
                titleY += (100 - titleY) * 0.1;
            }
            
            // Fondu des boutons
            if (buttonOpacity < 1) {
                buttonOpacity += 0.05;
            }
            
            repaint();
        });
        animationTimer.start();
    }
    
    private void createButtons() {
        simulateButton = createStyledButton("SIMULER");
        quitButton = createStyledButton("QUITTER");
        
        simulateButton.addActionListener(e -> startSimulation());
        quitButton.addActionListener(e -> quitGame());
        
        add(simulateButton);
        add(quitButton);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            private float hoverIntensity = 0;
            private Timer hoverTimer;
            
            {
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setForeground(Color.WHITE);
                setFont(BUTTON_FONT);
                
                hoverTimer = new Timer(1000/60, e -> {
                    if (getModel().isRollover() && hoverIntensity < 1) {
                        hoverIntensity += 0.1f;
                    } else if (!getModel().isRollover() && hoverIntensity > 0) {
                        hoverIntensity -= 0.1f;
                    }
                    repaint();
                });
                hoverTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D buttonShape = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 15, 15
                );
                
                Color baseColor = getModel().isPressed() ? BUTTON_PRESS_COLOR : 
                                interpolateColor(BUTTON_COLOR, BUTTON_HOVER_COLOR, hoverIntensity);
                g2.setColor(baseColor);
                g2.fill(buttonShape);
                
                GradientPaint gloss = new GradientPaint(
                    0, 0, new Color(1f, 1f, 1f, 0.2f * (1-hoverIntensity)),
                    0, getHeight()/2, new Color(1f, 1f, 1f, 0f)
                );
                g2.setPaint(gloss);
                g2.fill(buttonShape);
                
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                g2.setColor(new Color(0, 0, 0, 100));
                g2.drawString(getText(), textX + 2, textY + 2);
                
                g2.setColor(getForeground());
                g2.drawString(getText(), textX, textY);
                
                g2.dispose();
            }
        };
        
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dessiner le fond
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
        
        // Dessiner le titre
        drawTitle(g2d);
        
        // Mettre à jour la position des boutons
        updateButtonLayout();
        
        g2d.dispose();
    }
    
    private void drawTitle(Graphics2D g2d) {
        String title = "SIMULATEUR D'AVION";
        g2d.setFont(TITLE_FONT);
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        
        g2d.setColor(new Color(0, 153, 255, 50));
        for (int i = 0; i < 5; i++) {
            g2d.drawString(title, titleX, titleY + i);
        }
        
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, titleX, titleY);
    }
    
    private void updateButtonLayout() {
        if (simulateButton == null) return;
        
        int totalHeight = 2 * BUTTON_HEIGHT + BUTTON_SPACING;
        int startY = (int)((getHeight() - totalHeight) * 0.6);
        int centerX = (getWidth() - BUTTON_WIDTH) / 2;
        
        float alpha = Math.min(1.0f, buttonOpacity);
        simulateButton.setForeground(new Color(1f, 1f, 1f, alpha));
        quitButton.setForeground(new Color(1f, 1f, 1f, alpha));
        
        simulateButton.setBounds(centerX, startY, BUTTON_WIDTH, BUTTON_HEIGHT);
        quitButton.setBounds(centerX, startY + BUTTON_HEIGHT + BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT);
    }
    
    private Color interpolateColor(Color c1, Color c2, float ratio) {
        float[] comp1 = c1.getRGBComponents(null);
        float[] comp2 = c2.getRGBComponents(null);
        float[] result = new float[4];
        for (int i = 0; i < 4; i++) {
            result[i] = comp1[i] + (comp2[i] - comp1[i]) * ratio;
        }
        return new Color(result[0], result[1], result[2], result[3]);
    }
    
    private void startSimulation() {
        System.out.println("Démarrage de la simulation...");
        animationTimer.stop();
        
        Timer fadeOutTimer = new Timer(1000/60, new ActionListener() {
            float alpha = 1.0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha -= 0.05f;
                if (alpha <= 0) {
                    ((Timer)e.getSource()).stop();
                    parentFrame.getContentPane().removeAll();
                    parentFrame.add(new SimulationPanel());
                    parentFrame.revalidate();
                    parentFrame.repaint();
                } else {
                    setOpaque(false);
                    setBackground(new Color(0,0,0,0));
                    repaint();
                }
            }
        });
        fadeOutTimer.start();
    }
    
    private void quitGame() {
        int result = JOptionPane.showConfirmDialog(
            parentFrame,
            "Êtes-vous sûr de vouloir quitter ?",
            "Quitter",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
} 