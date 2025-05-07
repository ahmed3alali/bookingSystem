package com.mycompany.bookingsystem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightDAO implements Dao<Flight, Integer> {

    /**
     * Save a new flight to the database
     *
     * @param flight Flight to save
     * @return Flight with generated ID
     */
    @Override
    public Flight save(Flight flight) {
        String sql = "INSERT INTO Flights (flight_number, airline_id, aircraft_id, "
                + "departure_airport_id, arrival_airport_id, departure_time, "
                + "arrival_time, base_price, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, flight.getFlightNumber());
            pstmt.setInt(2, flight.getAirlineId());
            pstmt.setInt(3, flight.getAircraftId());
            pstmt.setInt(4, flight.getDepartureAirportId());
            pstmt.setInt(5, flight.getArrivalAirportId());
            pstmt.setTimestamp(6, Timestamp.valueOf(flight.getDepartureTime()));
            pstmt.setTimestamp(7, Timestamp.valueOf(flight.getArrivalTime()));
            pstmt.setBigDecimal(8, flight.getBasePrice());
            pstmt.setString(9, flight.getStatus());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating flight failed, no rows affected.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                flight.setFlightId(rs.getInt(1));
            } else {
                throw new SQLException("Creating flight failed, no ID obtained.");
            }

            return flight;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error saving flight: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Update an existing flight
     *
     * @param flight Flight to update
     * @return Updated flight
     */
    @Override
    public Flight update(Flight flight) {
        String sql = "UPDATE Flights SET flight_number = ?, airline_id = ?, aircraft_id = ?, "
                + "departure_airport_id = ?, arrival_airport_id = ?, departure_time = ?, "
                + "arrival_time = ?, base_price = ?, status = ? WHERE flight_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, flight.getFlightNumber());
            pstmt.setInt(2, flight.getAirlineId());
            pstmt.setInt(3, flight.getAircraftId());
            pstmt.setInt(4, flight.getDepartureAirportId());
            pstmt.setInt(5, flight.getArrivalAirportId());
            pstmt.setTimestamp(6, Timestamp.valueOf(flight.getDepartureTime()));
            pstmt.setTimestamp(7, Timestamp.valueOf(flight.getArrivalTime()));
            pstmt.setBigDecimal(8, flight.getBasePrice());
            pstmt.setString(9, flight.getStatus());
            pstmt.setInt(10, flight.getFlightId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating flight failed, no rows affected.");
            }

            return flight;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating flight: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find a flight by ID
     *
     * @param id Flight ID
     * @return Optional containing flight if found
     */
    @Override
    public Optional<Flight> findById(Integer id) {
        String sql = "SELECT f.*, a1.airport_code as departure_code, a2.airport_code as arrival_code, "
                + "al.airline_name FROM Flights f "
                + "JOIN Airports a1 ON f.departure_airport_id = a1.airport_id "
                + "JOIN Airports a2 ON f.arrival_airport_id = a2.airport_id "
                + "JOIN Airlines al ON f.airline_id = al.airline_id "
                + "WHERE f.flight_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToFlight(rs));
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding flight: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find all flights
     *
     * @return List of all flights
     */
    @Override
    public List<Flight> findAll() {
        String sql = "SELECT f.*, a1.airport_code as departure_code, a2.airport_code as arrival_code, "
                + "al.airline_name FROM Flights f "
                + "JOIN Airports a1 ON f.departure_airport_id = a1.airport_id "
                + "JOIN Airports a2 ON f.arrival_airport_id = a2.airport_id "
                + "JOIN Airlines al ON f.airline_id = al.airline_id";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Flight> flights = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                flights.add(mapResultSetToFlight(rs));
            }

            return flights;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding all flights: " + e.getMessage());
            return flights;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Delete a flight by ID
     *
     * @param id Flight ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Flights WHERE flight_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting flight: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Search flights by departure and arrival airports and date
     *
     * @param departureAirportId Departure airport ID
     * @param arrivalAirportId Arrival airport ID
     * @param departureDate Departure date
     * @return List of matching flights
     */
    public List<Flight> searchFlights(int departureAirportId, int arrivalAirportId, LocalDateTime departureDate) {
        String sql = "SELECT f.*, a1.airport_code as departure_code, a2.airport_code as arrival_code, "
                + "al.airline_name FROM Flights f "
                + "JOIN Airports a1 ON f.departure_airport_id = a1.airport_id "
                + "JOIN Airports a2 ON f.arrival_airport_id = a2.airport_id "
                + "JOIN Airlines al ON f.airline_id = al.airline_id "
                + "WHERE f.departure_airport_id = ? AND f.arrival_airport_id = ? "
                + "AND DATE(f.departure_time) = ? AND f.status = 'Scheduled'";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Flight> flights = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, departureAirportId);
            pstmt.setInt(2, arrivalAirportId);
            pstmt.setDate(3, java.sql.Date.valueOf(departureDate.toLocalDate()));

            rs = pstmt.executeQuery();

            while (rs.next()) {
                flights.add(mapResultSetToFlight(rs));
            }

            return flights;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error searching flights: " + e.getMessage());
            return flights;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Map ResultSet to Flight object
     *
     * @param rs ResultSet containing flight data
     * @return Flight object
     * @throws SQLException if database access error occurs
     */
    private Flight mapResultSetToFlight(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setFlightId(rs.getInt("flight_id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setAirlineId(rs.getInt("airline_id"));
        flight.setAircraftId(rs.getInt("aircraft_id"));
        flight.setDepartureAirportId(rs.getInt("departure_airport_id"));
        flight.setArrivalAirportId(rs.getInt("arrival_airport_id"));
        flight.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        flight.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
        flight.setBasePrice(rs.getBigDecimal("base_price"));
        flight.setStatus(rs.getString("status"));

        // Set transient properties for display
        flight.setDepartureAirportCode(rs.getString("departure_code"));
        flight.setArrivalAirportCode(rs.getString("arrival_code"));
        flight.setAirlineName(rs.getString("airline_name"));

        return flight;
    }
}
