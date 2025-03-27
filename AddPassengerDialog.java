import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddPassengerDialog extends JDialog {
    private JTextField nameField;
    private JTextField dobField;
    private JTextField nationalityField;
    private boolean confirmed = false;

    public AddPassengerDialog(JFrame parent) {
        super(parent, "Ajouter un Passager", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 30, 48));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(36, 59, 85));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel nameLabel = new JLabel("Nom Complet:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel dobLabel = new JLabel("Date de Naissance (JJ/MM/AAAA):");
        dobLabel.setForeground(Color.WHITE);
        dobLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        dobField = new JTextField();
        dobField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dobField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel nationalityLabel = new JLabel("Nationalité:");
        nationalityLabel.setForeground(Color.WHITE);
        nationalityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        nationalityField = new JTextField();
        nationalityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nationalityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(dobLabel);
        formPanel.add(dobField);
        formPanel.add(nationalityLabel);
        formPanel.add(nationalityField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = new JButton("Annuler");
        styleButton(cancelButton, new Color(231, 76, 60));
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = new JButton("Enregistrer");
        styleButton(saveButton, new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            if (validateForm()) {
                confirmed = true;
                dispose();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir le nom du passager", 
                "Erreur de Validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (dobField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir la date de naissance", 
                "Erreur de Validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (nationalityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir la nationalité", 
                "Erreur de Validation", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getPassengerName() {
        return nameField.getText().trim();
    }
    
    public String getPassengerDOB() {
        return dobField.getText().trim();
    }
    
    public String getPassengerNationality() {
        return nationalityField.getText().trim();
    }
    
    public void setPassengerName(String name) {
        nameField.setText(name);
    }
    
    public void setPassengerDOB(String dob) {
        dobField.setText(dob);
    }
    
    public void setPassengerNationality(String nationality) {
        nationalityField.setText(nationality);
    }
} 