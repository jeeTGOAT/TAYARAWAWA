import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class DataManager {
    private static DataManager instance;
    private List<Flight> flights;
    private List<Passenger> passengers;
    private List<Booking> bookings;
    
    private DataManager() {
        flights = new ArrayList<>();
        passengers = new ArrayList<>();
        bookings = new ArrayList<>();
        
        // Ajout de quelques données de test
        initializeTestData();
    }
    
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    private void initializeTestData() {
        // Ajout de vols de test
        flights.add(new Flight("AF123", "Paris", "New York", 
            LocalDateTime.now().plusDays(1), "À l'heure", 150));
        flights.add(new Flight("AF456", "Paris", "Tokyo", 
            LocalDateTime.now().plusDays(2), "À l'heure", 200));
        
        // Ajout de passagers de test
        passengers.add(new Passenger("P001", "Jean", "Dupont", 
            "jean.dupont@email.com", "0123456789"));
        passengers.add(new Passenger("P002", "Marie", "Martin", 
            "marie.martin@email.com", "9876543210"));
        
        // Ajout de réservations de test
        bookings.add(new Booking("B001", flights.get(0), passengers.get(0), 
            LocalDateTime.now(), "Confirmée", 500.0));
        bookings.add(new Booking("B002", flights.get(1), passengers.get(1), 
            LocalDateTime.now(), "En attente", 800.0));
    }
    
    // Méthodes pour les vols
    public List<Flight> getAllFlights() { return flights; }
    public void addFlight(Flight flight) { flights.add(flight); }
    public void removeFlight(Flight flight) { flights.remove(flight); }
    public Flight getFlightByNumber(String flightNumber) {
        return flights.stream()
            .filter(f -> f.getFlightNumber().equals(flightNumber))
            .findFirst()
            .orElse(null);
    }
    
    // Méthodes pour les passagers
    public List<Passenger> getAllPassengers() { return passengers; }
    public void addPassenger(Passenger passenger) { passengers.add(passenger); }
    public void removePassenger(Passenger passenger) { passengers.remove(passenger); }
    public Passenger getPassengerById(String id) {
        return passengers.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    // Méthodes pour les réservations
    public List<Booking> getAllBookings() { return bookings; }
    public void addBooking(Booking booking) { bookings.add(booking); }
    public void removeBooking(Booking booking) { bookings.remove(booking); }
    public Booking getBookingByNumber(String bookingNumber) {
        return bookings.stream()
            .filter(b -> b.getBookingNumber().equals(bookingNumber))
            .findFirst()
            .orElse(null);
    }
    
    // Méthodes utilitaires
    public String generateBookingNumber() {
        return "B" + String.format("%03d", bookings.size() + 1);
    }
    
    public String generatePassengerId() {
        return "P" + String.format("%03d", passengers.size() + 1);
    }
} 