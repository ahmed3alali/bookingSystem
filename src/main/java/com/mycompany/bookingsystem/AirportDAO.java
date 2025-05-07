package com.mycompany.bookingsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Airport entity
 */
public class AirportDAO implements Dao<Airport, Integer> {

    /**
     * Save a new airport to the database
     *
     * @param airport Airport to save
     * @return Airport with generated ID
     */
    @Override
    public Airport save(Airport airport) {
        String sql = "INSERT INTO Airports (airport_code, airport_name, city, country, is_active) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, airport.getAirportCode());
            pstmt.setString(2, airport.getAirportName());
            pstmt.setString(3, airport.getCity());
            pstmt.setString(4, airport.getCountry());
            pstmt.setBoolean(5, airport.isActive());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating airport failed, no rows affected.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                airport.setAirportId(rs.getInt(1));
            } else {
                throw new SQLException("Creating airport failed, no ID obtained.");
            }

            return airport;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error saving airport: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Update an existing airport
     *
     * @param airport Airport to update
     * @return Updated airport
     */
    @Override
    public Airport update(Airport airport) {
        String sql = "UPDATE Airports SET airport_code = ?, airport_name = ?, city = ?, country = ?, is_active = ? WHERE airport_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, airport.getAirportCode());
            pstmt.setString(2, airport.getAirportName());
            pstmt.setString(3, airport.getCity());
            pstmt.setString(4, airport.getCountry());
            pstmt.setBoolean(5, airport.isActive());
            pstmt.setInt(6, airport.getAirportId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating airport failed, no rows affected.");
            }

            return airport;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating airport: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find an airport by ID
     *
     * @param id Airport ID
     * @return Optional containing airport if found
     */
    @Override
    public Optional<Airport> findById(Integer id) {
        String sql = "SELECT * FROM Airports WHERE airport_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Airport airport = extractAirportFromResultSet(rs);
                return Optional.of(airport);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding airport: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find all airports
     *
     * @return List of all airports
     */
    @Override
    public List<Airport> findAll() {
        String sql = "SELECT * FROM Airports";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Airport> airports = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Airport airport = extractAirportFromResultSet(rs);
                airports.add(airport);
            }

            return airports;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding all airports: " + e.getMessage());
            return airports;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Delete an airport by ID
     *
     * @param id Airport ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Airports WHERE airport_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting airport: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find an airport by its code
     *
     * @param code Airport code
     * @return Optional containing airport if found
     */
    public Optional<Airport> findByCode(String code) {
        String sql = "SELECT * FROM Airports WHERE airport_code = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, code);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Airport airport = extractAirportFromResultSet(rs);
                return Optional.of(airport);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding airport by code: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find airports by city
     *
     * @param city City name
     * @return List of airports in the city
     */
    public List<Airport> findByCity(String city) {
        String sql = "SELECT * FROM Airports WHERE city = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Airport> airports = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, city);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Airport airport = extractAirportFromResultSet(rs);
                airports.add(airport);
            }

            return airports;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding airports by city: " + e.getMessage());
            return airports;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find airports by country
     *
     * @param country Country name
     * @return List of airports in the country
     */
    public List<Airport> findByCountry(String country) {
        String sql = "SELECT * FROM Airports WHERE country = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Airport> airports = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, country);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Airport airport = extractAirportFromResultSet(rs);
                airports.add(airport);
            }

            return airports;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding airports by country: " + e.getMessage());
            return airports;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Get only active airports
     *
     * @return List of active airports
     */
    public List<Airport> findActiveAirports() {
        String sql = "SELECT * FROM Airports WHERE is_active = 1";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Airport> airports = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Airport airport = extractAirportFromResultSet(rs);
                airports.add(airport);
            }

            return airports;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding active airports: " + e.getMessage());
            return airports;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Helper method to extract airport from result set
     *
     * @param rs Result set
     * @return Airport object
     * @throws SQLException if error occurs
     */
    private Airport extractAirportFromResultSet(ResultSet rs) throws SQLException {
        Airport airport = new Airport();
        airport.setAirportId(rs.getInt("airport_id"));
        airport.setAirportCode(rs.getString("airport_code"));
        airport.setAirportName(rs.getString("airport_name"));
        airport.setCity(rs.getString("city"));
        airport.setCountry(rs.getString("country"));
        airport.setActive(rs.getBoolean("is_active"));
        return airport;
    }
}
