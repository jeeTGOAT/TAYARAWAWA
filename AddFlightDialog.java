import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddFlightDialog extends JDialog {
    private JTextField flightNumberField;
    private JTextField departureField;
    private JTextField arrivalField;
    private JTextField dateTimeField;
    private JTextField statusField;
    private JTextField seatsField;
    private Flight result;
    
    public AddFlightDialog(Component parent) {
        super((Frame)SwingUtilities.getWindowAncestor(parent), "Ajouter un Vol", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        // Création des composants
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Numéro de vol
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Numéro de vol:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        flightNumberField = new JTextField(20);
        mainPanel.add(flightNumberField, gbc);
        
        // Départ
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Départ:"), gbc);
        gbc.gridx = 1;
        departureField = new JTextField(20);
        mainPanel.add(departureField, gbc);
        
        // Arrivée
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Arrivée:"), gbc);
        gbc.gridx = 1;
        arrivalField = new JTextField(20);
        mainPanel.add(arrivalField, gbc);
        
        // Date et heure
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Date et heure (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        dateTimeField = new JTextField(20);
        mainPanel.add(dateTimeField, gbc);
        
        // Statut
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1;
        statusField = new JTextField(20);
        mainPanel.add(statusField, gbc);
        
        // Nombre de places
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Places disponibles:"), gbc);
        gbc.gridx = 1;
        seatsField = new JTextField(20);
        mainPanel.add(seatsField, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");
        
        okButton.addActionListener(e -> {
            try {
                String flightNumber = flightNumberField.getText();
                String departure = departureField.getText();
                String arrival = arrivalField.getText();
                LocalDateTime dateTime = LocalDateTime.parse(
                    dateTimeField.getText(), 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                );
                String status = statusField.getText();
                int seats = Integer.parseInt(seatsField.getText());
                
                result = new Flight(flightNumber, departure, arrival, dateTime, status, seats);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur de format: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    public Flight getResult() {
        return result;
    }
} 