import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class AirTrafficManagement extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel flightsPanel;
    private JPanel bookingsPanel;
    private JPanel passengersPanel;
    private DataManager dataManager;
    
    // Tables
    private JTable flightsTable;
    private JTable bookingsTable;
    private JTable passengersTable;
    
    // Modèles de table
    private DefaultTableModel flightsModel;
    private DefaultTableModel bookingsModel;
    private DefaultTableModel passengersModel;
    
    // Barre de recherche
    private JTextField searchField;
    
    // Statistiques
    private JLabel statsLabel;
    
    private JFrame parentFrame;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private JPanel mainPanel;
    private JPanel controlPanel;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private LocalDateTime currentTime;
    private javax.swing.Timer updateTimer;
    private java.util.List<Flight> flights;
    private Random random;
    
    public AirTrafficManagement(JFrame parent) {
        super("Gestion du Trafic Aérien");
        this.parentFrame = parent;
        this.flights = new ArrayList<>();
        this.random = new Random();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        currentTime = LocalDateTime.now();
        
        // Initialisation du gestionnaire de données
        dataManager = DataManager.getInstance();
        
        // Initialisation des composants
        initComponents();
    }
    
    private void initComponents() {
        // Configuration du TabbedPane
        tabbedPane = new JTabbedPane();
        
        // Création des différents onglets
        flightsPanel = createFlightsPanel();
        bookingsPanel = createBookingsPanel();
        passengersPanel = createPassengersPanel();
        
        // Ajout des onglets
        tabbedPane.addTab("Vols", flightsPanel);
        tabbedPane.addTab("Réservations", bookingsPanel);
        tabbedPane.addTab("Passagers", passengersPanel);
        
        // Création de la barre d'outils
        JToolBar toolBar = createToolBar();
        
        // Panel des statistiques
        JPanel statsPanel = createStatsPanel();
        
        // Ajout du bouton retour
        JButton backButton = new JButton("Retour au Menu");
        backButton.addActionListener(e -> {
            dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });
        
        // Panel supérieur avec bouton retour et barre d'outils
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(toolBar, BorderLayout.CENTER);
        
        // Ajout des composants
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
        
        // Mise à jour initiale des tables
        updateAllTables();
        updateStats();
        
        // Create main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 35));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create flight table
        String[] columns = {"Vol", "Départ", "Arrivée", "Altitude", "Vitesse", "État"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightTable = new JTable(tableModel);
        flightTable.setBackground(new Color(45, 45, 50));
        flightTable.setForeground(Color.WHITE);
        flightTable.setSelectionBackground(new Color(60, 60, 65));
        flightTable.setSelectionForeground(Color.WHITE);
        flightTable.setGridColor(new Color(60, 60, 65));
        flightTable.setShowGrid(true);
        flightTable.setRowHeight(30);
        flightTable.getTableHeader().setBackground(new Color(40, 40, 45));
        flightTable.getTableHeader().setForeground(Color.WHITE);
        flightTable.setFont(new Font("Arial", Font.PLAIN, 14));
        flightTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.setBackground(new Color(30, 30, 35));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create control panel
        controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.EAST);
        
        // Create status panel
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(30, 30, 35));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel = new JLabel("État du Système: Normal");
        statusLabel.setForeground(Color.WHITE);
        timeLabel = new JLabel("Temps: " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeLabel.setForeground(Color.WHITE);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(timeLabel, BorderLayout.EAST);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Add some test flights
        addTestFlights();
        
        // Start update timer
        updateTimer = new javax.swing.Timer(1000, e -> {
            currentTime = currentTime.plusSeconds(1);
            updateTimeLabel();
            updateFlights();
        });
        updateTimer.start();
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);
        
        // Barre de recherche
        searchField = new JTextField(20);
        searchField.setMaximumSize(new Dimension(200, 30));
        searchField.setToolTipText("Rechercher...");
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTables(searchField.getText());
            }
        });
        
        // Boutons de la barre d'outils
        JButton refreshButton = new JButton("Actualiser");
        JButton helpButton = new JButton("Aide");
        
        // Style des boutons
        styleButton(refreshButton);
        styleButton(helpButton);
        
        refreshButton.addActionListener(e -> updateAllTables());
        helpButton.addActionListener(e -> showHelp());
        
        toolBar.add(new JLabel("Rechercher: "));
        toolBar.add(searchField);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        toolBar.add(helpButton);
        
        return toolBar;
    }
    
    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 122, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 240, 240));
        statsLabel = new JLabel();
        panel.add(statsLabel);
        return panel;
    }
    
    private void updateStats() {
        int totalFlights = dataManager.getAllFlights().size();
        int totalBookings = dataManager.getAllBookings().size();
        int totalPassengers = dataManager.getAllPassengers().size();
        
        statsLabel.setText(String.format(
            "Statistiques - Vols: %d | Réservations: %d | Passagers: %d",
            totalFlights, totalBookings, totalPassengers
        ));
    }
    
    private void filterTables(String searchText) {
        if (searchText.isEmpty()) {
            updateAllTables();
            return;
        }
        
        searchText = searchText.toLowerCase();
        
        // Filtrer les vols
        flightsModel.setRowCount(0);
        for (Flight flight : dataManager.getAllFlights()) {
            if (matchesFlight(flight, searchText)) {
                addFlightToTable(flight);
            }
        }
        
        // Filtrer les réservations
        bookingsModel.setRowCount(0);
        for (Booking booking : dataManager.getAllBookings()) {
            if (matchesBooking(booking, searchText)) {
                addBookingToTable(booking);
            }
        }
        
        // Filtrer les passagers
        passengersModel.setRowCount(0);
        for (Passenger passenger : dataManager.getAllPassengers()) {
            if (matchesPassenger(passenger, searchText)) {
                addPassengerToTable(passenger);
            }
        }
    }
    
    private boolean matchesFlight(Flight flight, String searchText) {
        return flight.getFlightNumber().toLowerCase().contains(searchText) ||
               flight.getDeparture().toLowerCase().contains(searchText) ||
               flight.getArrival().toLowerCase().contains(searchText) ||
               flight.getStatus().toLowerCase().contains(searchText);
    }
    
    private boolean matchesBooking(Booking booking, String searchText) {
        return booking.getBookingNumber().toLowerCase().contains(searchText) ||
               booking.getFlight().getFlightNumber().toLowerCase().contains(searchText) ||
               booking.getPassenger().toString().toLowerCase().contains(searchText);
    }
    
    private boolean matchesPassenger(Passenger passenger, String searchText) {
        return passenger.getId().toLowerCase().contains(searchText) ||
               passenger.getFirstName().toLowerCase().contains(searchText) ||
               passenger.getLastName().toLowerCase().contains(searchText) ||
               passenger.getEmail().toLowerCase().contains(searchText);
    }
    
    private void showHelp() {
        JOptionPane.showMessageDialog(this,
            "Guide d'utilisation:\n\n" +
            "1. Utiliser la barre de recherche pour filtrer les données\n" +
            "2. Cliquer sur 'Actualiser' pour mettre à jour les données\n" +
            "3. Utiliser les onglets pour naviguer entre les sections\n" +
            "4. Les statistiques sont affichées en bas de la fenêtre\n\n" +
            "Pour plus d'aide, consultez la documentation.",
            "Aide",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private JPanel createFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Création du tableau des vols
        String[] columnNames = {"Numéro de Vol", "Départ", "Arrivée", "Date", "Statut", "Places"};
        flightsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(flightsModel);
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        
        // Panneau des boutons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Ajouter un Vol");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        
        addButton.addActionListener(e -> {
            AddFlightDialog dialog = new AddFlightDialog(this);
            dialog.setVisible(true);
            Flight newFlight = dialog.getResult();
            if (newFlight != null) {
                dataManager.addFlight(newFlight);
                updateFlightsTable();
            }
        });
        
        editButton.addActionListener(e -> {
            int selectedRow = flightsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String flightNumber = (String) flightsModel.getValueAt(selectedRow, 0);
                Flight flight = dataManager.getFlightByNumber(flightNumber);
                if (flight != null) {
                    // TODO: Implémenter la modification de vol
                    JOptionPane.showMessageDialog(this, "Fonctionnalité à venir");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un vol");
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = flightsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String flightNumber = (String) flightsModel.getValueAt(selectedRow, 0);
                Flight flight = dataManager.getFlightByNumber(flightNumber);
                if (flight != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer ce vol ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        dataManager.removeFlight(flight);
                        updateFlightsTable();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un vol");
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Création du tableau des réservations
        String[] columnNames = {"Numéro de Réservation", "Vol", "Passager", "Date", "Statut", "Prix"};
        bookingsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsTable = new JTable(bookingsModel);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        
        // Panneau des boutons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Nouvelle Réservation");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Annuler");
        
        addButton.addActionListener(e -> {
            AddBookingDialog dialog = new AddBookingDialog(this);
            dialog.setVisible(true);
            Booking newBooking = dialog.getResult();
            if (newBooking != null) {
                dataManager.addBooking(newBooking);
                updateBookingsTable();
            }
        });
        
        editButton.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String bookingNumber = (String) bookingsModel.getValueAt(selectedRow, 0);
                Booking booking = dataManager.getBookingByNumber(bookingNumber);
                if (booking != null) {
                    // TODO: Implémenter la modification de réservation
                    JOptionPane.showMessageDialog(this, "Fonctionnalité à venir");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une réservation");
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String bookingNumber = (String) bookingsModel.getValueAt(selectedRow, 0);
                Booking booking = dataManager.getBookingByNumber(bookingNumber);
                if (booking != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir annuler cette réservation ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        dataManager.removeBooking(booking);
                        updateBookingsTable();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une réservation");
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPassengersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Création du tableau des passagers
        String[] columnNames = {"ID", "Nom", "Prénom", "Email", "Téléphone"};
        passengersModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        passengersTable = new JTable(passengersModel);
        JScrollPane scrollPane = new JScrollPane(passengersTable);
        
        // Panneau des boutons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Ajouter un Passager");
        JButton editButton = new JButton("Modifier");
        JButton deleteButton = new JButton("Supprimer");
        
        addButton.addActionListener(e -> {
            AddPassengerDialog dialog = new AddPassengerDialog(this);
            dialog.setVisible(true);
            Passenger newPassenger = dialog.getResult();
            if (newPassenger != null) {
                dataManager.addPassenger(newPassenger);
                updatePassengersTable();
            }
        });
        
        editButton.addActionListener(e -> {
            int selectedRow = passengersTable.getSelectedRow();
            if (selectedRow >= 0) {
                String passengerId = (String) passengersModel.getValueAt(selectedRow, 0);
                Passenger passenger = dataManager.getPassengerById(passengerId);
                if (passenger != null) {
                    // TODO: Implémenter la modification de passager
                    JOptionPane.showMessageDialog(this, "Fonctionnalité à venir");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un passager");
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = passengersTable.getSelectedRow();
            if (selectedRow >= 0) {
                String passengerId = (String) passengersModel.getValueAt(selectedRow, 0);
                Passenger passenger = dataManager.getPassengerById(passengerId);
                if (passenger != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer ce passager ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        dataManager.removePassenger(passenger);
                        updatePassengersTable();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un passager");
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addFlightToTable(Flight flight) {
        flightsModel.addRow(new Object[]{
            flight.getFlightNumber(),
            flight.getDeparture(),
            flight.getArrival(),
            flight.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            flight.getStatus(),
            flight.getAvailableSeats()
        });
    }
    
    private void addBookingToTable(Booking booking) {
        bookingsModel.addRow(new Object[]{
            booking.getBookingNumber(),
            booking.getFlight().getFlightNumber(),
            booking.getPassenger().toString(),
            booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            booking.getStatus(),
            String.format("%.2f €", booking.getPrice())
        });
    }
    
    private void addPassengerToTable(Passenger passenger) {
        passengersModel.addRow(new Object[]{
            passenger.getId(),
            passenger.getLastName(),
            passenger.getFirstName(),
            passenger.getEmail(),
            passenger.getPhone()
        });
    }
    
    private void updateAllTables() {
        updateFlightsTable();
        updateBookingsTable();
        updatePassengersTable();
        updateStats();
    }
    
    private void updateFlightsTable() {
        flightsModel.setRowCount(0);
        for (Flight flight : dataManager.getAllFlights()) {
            addFlightToTable(flight);
        }
    }
    
    private void updateBookingsTable() {
        bookingsModel.setRowCount(0);
        for (Booking booking : dataManager.getAllBookings()) {
            addBookingToTable(booking);
        }
    }
    
    private void updatePassengersTable() {
        passengersModel.setRowCount(0);
        for (Passenger passenger : dataManager.getAllPassengers()) {
            addPassengerToTable(passenger);
        }
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 35));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add flight controls
        JButton addFlightButton = new JButton("Ajouter un Vol");
        addFlightButton.addActionListener(e -> showAddFlightDialog());
        panel.add(addFlightButton);
        
        JButton removeFlightButton = new JButton("Supprimer un Vol");
        removeFlightButton.addActionListener(e -> removeSelectedFlight());
        panel.add(removeFlightButton);
        
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
    
    private void showAddFlightDialog() {
        AddFlightDialog dialog = new AddFlightDialog(this);
        dialog.setVisible(true);
        Flight newFlight = dialog.getResult();
        if (newFlight != null) {
            dataManager.addFlight(newFlight);
            updateFlightsTable();
        }
    }
    
    private void removeSelectedFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow >= 0) {
            String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
            Flight flight = dataManager.getFlightByNumber(flightNumber);
            if (flight != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer ce vol ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dataManager.removeFlight(flight);
                    updateFlightsTable();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un vol");
        }
    }
    
    private void updateTimeLabel() {
        timeLabel.setText("Temps: " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    private void updateFlights() {
        // Implementation of updateFlights method
    }
    
    private void addTestFlights() {
        // Implementation of addTestFlights method
    }
} 