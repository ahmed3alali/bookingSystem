package com.mycompany.bookingsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Airline entity
 */
public class AirlineDAO implements Dao<Airline, Integer> {

    /**
     * Save a new airline to the database
     *
     * @param airline Airline to save
     * @return Airline with generated ID
     */
    @Override
    public Airline save(Airline airline) {
        String sql = "INSERT INTO Airlines (airline_code, airline_name, is_active) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, airline.getAirlineCode());
            pstmt.setString(2, airline.getAirlineName());
            pstmt.setBoolean(3, airline.isActive());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating airline failed, no rows affected.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                airline.setAirlineId(rs.getInt(1));
            } else {
                throw new SQLException("Creating airline failed, no ID obtained.");
            }

            return airline;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error saving airline: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Update an existing airline
     *
     * @param airline Airline to update
     * @return Updated airline
     */
    @Override
    public Airline update(Airline airline) {
        String sql = "UPDATE Airlines SET airline_code = ?, airline_name = ?, is_active = ? WHERE airline_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, airline.getAirlineCode());
            pstmt.setString(2, airline.getAirlineName());
            pstmt.setBoolean(3, airline.isActive());
            pstmt.setInt(4, airline.getAirlineId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating airline failed, no rows affected.");
            }

            return airline;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating airline: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find an airline by ID
     *
     * @param id Airline ID
     * @return Optional containing airline if found
     */
    @Override
    public Optional<Airline> findById(Integer id) {
        String sql = "SELECT * FROM Airlines WHERE airline_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Airline airline = extractAirlineFromResultSet(rs);
                return Optional.of(airline);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding airline: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find all airlines
     *
     * @return List of all airlines
     */
    @Override
    public List<Airline> findAll() {
        String sql = "SELECT * FROM Airlines";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Airline> airlines = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Airline airline = extractAirlineFromResultSet(rs);
                airlines.add(airline);
            }

            return airlines;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding all airlines: " + e.getMessage());
            return airlines;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Delete an airline by ID
     *
     * @param id Airline ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Airlines WHERE airline_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting airline: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find an airline by its code
     *
     * @param code Airline code
     * @return Optional containing airline if found
     */
    public Optional<Airline> findByCode(String code) {
        String sql = "SELECT * FROM Airlines WHERE airline_code = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, code);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Airline airline = extractAirlineFromResultSet(rs);
                return Optional.of(airline);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding airline by code: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find all active airlines
     *
     * @return List of active airlines
     */
    public List<Airline> findActiveAirlines() {
        String sql = "SELECT * FROM Airlines WHERE is_active = 1";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Airline> airlines = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Airline airline = extractAirlineFromResultSet(rs);
                airlines.add(airline);
            }

            return airlines;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding active airlines: " + e.getMessage());
            return airlines;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Helper method to extract airline from result set
     *
     * @param rs Result set
     * @return Airline object
     * @throws SQLException if error occurs
     */
    private Airline extractAirlineFromResultSet(ResultSet rs) throws SQLException {
        Airline airline = new Airline();
        airline.setAirlineId(rs.getInt("airline_id"));
        airline.setAirlineCode(rs.getString("airline_code"));
        airline.setAirlineName(rs.getString("airline_name"));
        airline.setActive(rs.getBoolean("is_active"));
        return airline;
    }
}
