import java.time.LocalDateTime;

public class Booking {
    private String bookingNumber;
    private Flight flight;
    private Passenger passenger;
    private LocalDateTime bookingDate;
    private String status;
    private double price;
    
    public Booking(String bookingNumber, Flight flight, Passenger passenger, 
                  LocalDateTime bookingDate, String status, double price) {
        this.bookingNumber = bookingNumber;
        this.flight = flight;
        this.passenger = passenger;
        this.bookingDate = bookingDate;
        this.status = status;
        this.price = price;
    }
    
    // Getters
    public String getBookingNumber() { return bookingNumber; }
    public Flight getFlight() { return flight; }
    public Passenger getPassenger() { return passenger; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public String getStatus() { return status; }
    public double getPrice() { return price; }
    
    // Setters
    public void setBookingNumber(String bookingNumber) { this.bookingNumber = bookingNumber; }
    public void setFlight(Flight flight) { this.flight = flight; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public void setStatus(String status) { this.status = status; }
    public void setPrice(double price) { this.price = price; }
    
    @Override
    public String toString() {
        return "Réservation " + bookingNumber + " - " + passenger.toString() + 
               " sur le vol " + flight.getFlightNumber();
    }
} 