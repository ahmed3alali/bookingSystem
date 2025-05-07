package com.mycompany.bookingsystem;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service class for handling booking-related business logic
 */
public class BookingService {

    private final BookingDAO bookingDAO;
    private final FlightDAO flightDAO;
    private final PassengerDAO passengerDAO;
    private final TravelClassDAO travelClassDAO;
    private final TransactionDAO transactionDAO;

    /**
     * Constructor for BookingService
     */
    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.flightDAO = new FlightDAO();
        this.passengerDAO = new PassengerDAO();
        this.travelClassDAO = new TravelClassDAO();
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Create a new booking
     *
     * @param passengerId Passenger ID
     * @param flightId Flight ID
     * @param classId Travel class ID
     * @return Created booking or null if creation failed
     */
    public Booking createBooking(int passengerId, int flightId, int classId) {
        // Check if passenger exists
        Optional<Passenger> passengerOpt = passengerDAO.findById(passengerId);
        if (!passengerOpt.isPresent()) {
            System.err.println("Passenger not found with ID: " + passengerId);
            return null;
        }

        // Check if flight exists
        Optional<Flight> flightOpt = flightDAO.findById(flightId);
        if (!flightOpt.isPresent()) {
            System.err.println("Flight not found with ID: " + flightId);
            return null;
        }

        // Check if travel class exists
        Optional<TravelClass> classOpt = travelClassDAO.findById(classId);
        if (!classOpt.isPresent()) {
            System.err.println("Travel class not found with ID: " + classId);
            return null;
        }

        Flight flight = flightOpt.get();
        TravelClass travelClass = classOpt.get();

        // Calculate total amount
        BigDecimal totalAmount = calculateTicketPrice(flight.getBasePrice(), travelClass.getPriceMultiplier());

        // Create booking
        Booking booking = new Booking(passengerId, flightId, classId, totalAmount);

        // Generate unique booking reference
        booking.setBookingReference(generateBookingReference());

        // Set booking date
        booking.setBookingDate(Timestamp.valueOf(LocalDateTime.now()));

        // Save booking to database
        Booking savedBooking = bookingDAO.save(booking);

        // Set display properties
        if (savedBooking != null) {
            savedBooking.setPassengerName(passengerOpt.get().getFullName());
            savedBooking.setFlightNumber(flight.getFlightNumber());
            savedBooking.setClassName(travelClass.getClassName());
        }

        return savedBooking;
    }

    /**
     * Find booking by ID
     *
     * @param bookingId Booking ID
     * @return Optional containing booking if found
     */
    public Optional<Booking> findBookingById(int bookingId) {
        return bookingDAO.findById(bookingId);
    }

    /**
     * Find booking by reference
     *
     * @param reference Booking reference
     * @return Optional containing booking if found
     */
    public Optional<Booking> findBookingByReference(String reference) {
        return bookingDAO.findByReference(reference);
    }

    /**
     * Find bookings by passenger ID
     *
     * @param passengerId Passenger ID
     * @return List of bookings for the passenger
     */
    public List<Booking> findBookingsByPassenger(int passengerId) {
        return bookingDAO.findByPassengerId(passengerId);
    }

    /**
     * Update booking status
     *
     * @param bookingId Booking ID
     * @param newStatus New status
     * @return Updated booking or null if update failed
     */
    public Booking updateBookingStatus(int bookingId, String newStatus) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            System.err.println("Booking not found with ID: " + bookingId);
            return null;
        }

        Booking booking = bookingOpt.get();
        booking.setBookingStatus(newStatus);

        return bookingDAO.update(booking);
    }

    /**
     * Confirm booking after payment
     *
     * @param bookingId Booking ID
     * @param paymentMethod Payment method
     * @param amount Payment amount
     * @return true if confirmation was successful
     */
    public boolean confirmBooking(int bookingId, String paymentMethod, BigDecimal amount) {
        // Update booking status
        Booking updatedBooking = updateBookingStatus(bookingId, "Confirmed");
        if (updatedBooking == null) {
            return false;
        }

        // Create transaction record
        Transaction transaction = new Transaction(bookingId, amount, paymentMethod);
        transaction.setTransactionStatus("Completed");
        transaction.setReferenceNumber(generateTransactionReference());
        transaction.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));

        Transaction savedTransaction = transactionDAO.save(transaction);

        return savedTransaction != null;
    }

    /**
     * Cancel booking
     *
     * @param bookingId Booking ID
     * @return true if cancellation was successful
     */
    public boolean cancelBooking(int bookingId) {
        Booking updatedBooking = updateBookingStatus(bookingId, "Cancelled");
        return updatedBooking != null;
    }

    /**
     * Check-in for a flight
     *
     * @param bookingId Booking ID
     * @param seatNumber Selected seat number
     * @return true if check-in was successful
     */
    public boolean checkIn(int bookingId, String seatNumber) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (!bookingOpt.isPresent()) {
            System.err.println("Booking not found with ID: " + bookingId);
            return false;
        }

        // Check if seat is available
        if (!isValidSeat(bookingOpt.get().getFlightId(), seatNumber)) {
            System.err.println("Seat " + seatNumber + " is not available");
            return false;
        }

        Booking booking = bookingOpt.get();
        booking.setBookingStatus("Checked-in");
        booking.setSeatNumber(seatNumber);

        Booking updatedBooking = bookingDAO.update(booking);
        return updatedBooking != null;
    }

    /**
     * Check if a seat is available
     *
     * @param flightId Flight ID
     * @param seatNumber Seat number
     * @return true if seat is available
     */
    private boolean isValidSeat(int flightId, String seatNumber) {
        // In a real application, we would check against a seat map
        // For simplicity, we'll assume the seat is valid if it follows a pattern
        if (seatNumber == null || seatNumber.isEmpty()) {
            return false;
        }

        // Check if seat follows pattern like "A1", "B12", etc.
        return seatNumber.matches("[A-Z][0-9]{1,2}");
    }

    /**
     * Calculate ticket price based on base price and class multiplier
     *
     * @param basePrice Base price
     * @param classMultiplier Class price multiplier
     * @return Total price
     */
    private BigDecimal calculateTicketPrice(BigDecimal basePrice, BigDecimal classMultiplier) {
        return basePrice.multiply(classMultiplier);
    }

    /**
     * Generate unique booking reference
     *
     * @return Booking reference
     */
    private String generateBookingReference() {
        // In a real application, we would ensure this is unique
        // For simplicity, we'll generate a random 6-character alphanumeric string
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(6);
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    /**
     * Generate unique transaction reference
     *
     * @return Transaction reference
     */
    private String generateTransactionReference() {
        // In a real application, we would ensure this is unique
        // For simplicity, we'll generate a random 10-character alphanumeric string
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(10);
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return "TX" + sb.toString();
    }
}
