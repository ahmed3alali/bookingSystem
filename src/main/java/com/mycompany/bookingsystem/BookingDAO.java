package com.mycompany.bookingsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingDAO implements Dao<Booking, Integer> {

    /**
     * Save a new booking to the database
     *
     * @param booking Booking to save
     * @return Booking with generated ID
     */
    @Override
    public Booking save(Booking booking) {
        String sql = "INSERT INTO Bookings (booking_reference, passenger_id, flight_id, class_id, "
                + "seat_number, booking_date, booking_status, total_amount) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, booking.getBookingReference());
            pstmt.setInt(2, booking.getPassengerId());
            pstmt.setInt(3, booking.getFlightId());
            pstmt.setInt(4, booking.getClassId());
            pstmt.setString(5, booking.getSeatNumber());
            pstmt.setTimestamp(6, booking.getBookingDate());
            pstmt.setString(7, booking.getBookingStatus());
            pstmt.setBigDecimal(8, booking.getTotalAmount());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating booking failed, no rows affected.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                booking.setBookingId(rs.getInt(1));
            } else {
                throw new SQLException("Creating booking failed, no ID obtained.");
            }

            return booking;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error saving booking: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Update an existing booking
     *
     * @param booking Booking to update
     * @return Updated booking
     */
    @Override
    public Booking update(Booking booking) {
        String sql = "UPDATE Bookings SET booking_reference = ?, passenger_id = ?, flight_id = ?, "
                + "class_id = ?, seat_number = ?, booking_date = ?, booking_status = ?, "
                + "total_amount = ? WHERE booking_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, booking.getBookingReference());
            pstmt.setInt(2, booking.getPassengerId());
            pstmt.setInt(3, booking.getFlightId());
            pstmt.setInt(4, booking.getClassId());
            pstmt.setString(5, booking.getSeatNumber());
            pstmt.setTimestamp(6, booking.getBookingDate());
            pstmt.setString(7, booking.getBookingStatus());
            pstmt.setBigDecimal(8, booking.getTotalAmount());
            pstmt.setInt(9, booking.getBookingId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating booking failed, no rows affected.");
            }

            return booking;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find a booking by ID
     *
     * @param id Booking ID
     * @return Optional containing booking if found
     */
    @Override
    public Optional<Booking> findById(Integer id) {
        String sql = "SELECT b.*, p.first_name, p.last_name, f.flight_number, tc.class_name "
                + "FROM Bookings b "
                + "JOIN Passengers p ON b.passenger_id = p.passenger_id "
                + "JOIN Flights f ON b.flight_id = f.flight_id "
                + "JOIN TravelClasses tc ON b.class_id = tc.class_id "
                + "WHERE b.booking_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToBooking(rs));
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding booking: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find booking by reference
     *
     * @param reference Booking reference
     * @return Optional containing booking if found
     */
    public Optional<Booking> findByReference(String reference) {
        String sql = "SELECT b.*, p.first_name, p.last_name, f.flight_number, tc.class_name "
                + "FROM Bookings b "
                + "JOIN Passengers p ON b.passenger_id = p.passenger_id "
                + "JOIN Flights f ON b.flight_id = f.flight_id "
                + "JOIN TravelClasses tc ON b.class_id = tc.class_id "
                + "WHERE b.booking_reference = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reference);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToBooking(rs));
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding booking by reference: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find bookings by passenger ID
     *
     * @param passengerId Passenger ID
     * @return List of bookings for the passenger
     */
    public List<Booking> findByPassengerId(int passengerId) {
        String sql = "SELECT b.*, p.first_name, p.last_name, f.flight_number, tc.class_name "
                + "FROM Bookings b "
                + "JOIN Passengers p ON b.passenger_id = p.passenger_id "
                + "JOIN Flights f ON b.flight_id = f.flight_id "
                + "JOIN TravelClasses tc ON b.class_id = tc.class_id "
                + "WHERE b.passenger_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, passengerId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            return bookings;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding bookings by passenger ID: " + e.getMessage());
            return bookings;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find all bookings
     *
     * @return List of all bookings
     */
    @Override
    public List<Booking> findAll() {
        String sql = "SELECT b.*, p.first_name, p.last_name, f.flight_number, tc.class_name "
                + "FROM Bookings b "
                + "JOIN Passengers p ON b.passenger_id = p.passenger_id "
                + "JOIN Flights f ON b.flight_id = f.flight_id "
                + "JOIN TravelClasses tc ON b.class_id = tc.class_id";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Booking> bookings = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }

            return bookings;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding all bookings: " + e.getMessage());
            return bookings;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Delete a booking by ID
     *
     * @param id Booking ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Bookings WHERE booking_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Map ResultSet to Booking object
     *
     * @param rs ResultSet containing booking data
     * @return Booking object
     * @throws SQLException if database access error occurs
     */
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setBookingReference(rs.getString("booking_reference"));
        booking.setPassengerId(rs.getInt("passenger_id"));
        booking.setFlightId(rs.getInt("flight_id"));
        booking.setClassId(rs.getInt("class_id"));
        booking.setSeatNumber(rs.getString("seat_number"));
        booking.setBookingDate(rs.getTimestamp("booking_date"));
        booking.setBookingStatus(rs.getString("booking_status"));
        booking.setTotalAmount(rs.getBigDecimal("total_amount"));

        // Set transient properties for display
        booking.setPassengerName(rs.getString("first_name") + " " + rs.getString("last_name"));
        booking.setFlightNumber(rs.getString("flight_number"));
        booking.setClassName(rs.getString("class_name"));

        return booking;
    }
}
