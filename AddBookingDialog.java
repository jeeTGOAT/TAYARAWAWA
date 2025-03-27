import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class AddBookingDialog extends JDialog {
    private JComboBox<String> flightComboBox;
    private JComboBox<String> passengerComboBox;
    private JTextField dateField;
    private JTextField seatField;
    private JComboBox<String> classComboBox;
    private JTextField priceField;
    private boolean confirmed = false;

    public AddBookingDialog(JFrame parent) {
        super(parent, "Nouvelle Réservation", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 30, 48));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBackground(new Color(36, 59, 85));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Flight
        JLabel flightLabel = new JLabel("Vol:");
        flightLabel.setForeground(Color.WHITE);
        flightLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String[] flights = {"AL123 - Alger-Paris", "AL456 - Oran-Marseille", "TU789 - Tunis-Lyon", "MA234 - Casablanca-Montpellier", "AL345 - Constantine-Bordeaux"};
        flightComboBox = new JComboBox<>(flights);
        styleComboBox(flightComboBox);
        
        // Passenger
        JLabel passengerLabel = new JLabel("Passager:");
        passengerLabel.setForeground(Color.WHITE);
        passengerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String[] passengers = {"Mohammed Walid", "Ali Hassan", "Fatima Zahra", "Salima Benali", "Karim Mansouri"};
        passengerComboBox = new JComboBox<>(passengers);
        styleComboBox(passengerComboBox);
        
        // Date
        JLabel dateLabel = new JLabel("Date (AAAA-MM-JJ):");
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        dateField = new JTextField();
        styleTextField(dateField);
        
        // Seat
        JLabel seatLabel = new JLabel("Siège:");
        seatLabel.setForeground(Color.WHITE);
        seatLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        seatField = new JTextField();
        styleTextField(seatField);
        
        // Class
        JLabel classLabel = new JLabel("Classe:");
        classLabel.setForeground(Color.WHITE);
        classLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        String[] classes = {"Économique", "Business", "Première"};
        classComboBox = new JComboBox<>(classes);
        styleComboBox(classComboBox);
        classComboBox.addActionListener(e -> updatePrice());
        
        // Price
        JLabel priceLabel = new JLabel("Prix:");
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        priceField = new JTextField();
        styleTextField(priceField);
        priceField.setEditable(false);
        
        // Add components to form
        formPanel.add(flightLabel);
        formPanel.add(flightComboBox);
        formPanel.add(passengerLabel);
        formPanel.add(passengerComboBox);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(seatLabel);
        formPanel.add(seatField);
        formPanel.add(classLabel);
        formPanel.add(classComboBox);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = new JButton("Annuler");
        styleButton(cancelButton, new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dispose());
        
        JButton confirmButton = new JButton("Confirmer");
        styleButton(confirmButton, new Color(46, 204, 113));
        confirmButton.addActionListener(e -> {
            if (validateForm()) {
                confirmed = true;
                dispose();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        updatePrice(); // Set initial price
    }
    
    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(new Color(44, 62, 80));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(new Color(44, 62, 80));
        comboBox.setForeground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219)));
        
        // Style the dropdown
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? new Color(52, 152, 219) : new Color(44, 62, 80));
                setForeground(Color.WHITE);
                return this;
            }
        });
    }
    
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void updatePrice() {
        String selectedClass = (String) classComboBox.getSelectedItem();
        String price;
        
        switch (selectedClass) {
            case "Économique":
                price = "12500 DA";
                break;
            case "Business":
                price = "45000 DA";
                break;
            case "Première":
                price = "78000 DA";
                break;
            default:
                price = "0 DA";
        }
        
        priceField.setText(price);
    }
    
    private boolean validateForm() {
        if (dateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir la date du vol", 
                "Erreur de Validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (seatField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir le numéro de siège", 
                "Erreur de Validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getFlightId() {
        String flight = (String) flightComboBox.getSelectedItem();
        return flight.split(" - ")[0];
    }
    
    public String getPassengerName() {
        return (String) passengerComboBox.getSelectedItem();
    }
    
    public String getFlightDate() {
        return dateField.getText().trim();
    }
    
    public String getSeatNumber() {
        return seatField.getText().trim();
    }
    
    public String getTravelClass() {
        return (String) classComboBox.getSelectedItem();
    }
    
    public String getPrice() {
        return priceField.getText();
    }
} 