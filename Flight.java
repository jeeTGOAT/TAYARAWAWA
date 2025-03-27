import java.time.LocalDateTime;

public class Flight {
    private String flightNumber;
    private String departure;
    private String arrival;
    private LocalDateTime dateTime;
    private String status;
    private int availableSeats;
    
    public Flight(String flightNumber, String departure, String arrival, 
                 LocalDateTime dateTime, String status, int availableSeats) {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.dateTime = dateTime;
        this.status = status;
        this.availableSeats = availableSeats;
    }
    
    // Getters
    public String getFlightNumber() { return flightNumber; }
    public String getDeparture() { return departure; }
    public String getArrival() { return arrival; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getStatus() { return status; }
    public int getAvailableSeats() { return availableSeats; }
    
    // Setters
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public void setDeparture(String departure) { this.departure = departure; }
    public void setArrival(String arrival) { this.arrival = arrival; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setStatus(String status) { this.status = status; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    
    @Override
    public String toString() {
        return flightNumber + " - " + departure + " → " + arrival + " (" + dateTime + ")";
    }
} 