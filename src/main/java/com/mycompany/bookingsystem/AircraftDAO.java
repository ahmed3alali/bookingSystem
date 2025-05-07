package com.mycompany.bookingsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Aircraft entity
 */
public class AircraftDAO implements Dao<Aircraft, Integer> {

    /**
     * Save a new aircraft to the database
     *
     * @param aircraft Aircraft to save
     * @return Aircraft with generated ID
     */
    @Override
    public Aircraft save(Aircraft aircraft) {
        String sql = "INSERT INTO Aircraft (aircraft_code, model, total_seats, airline_id) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, aircraft.getAircraftCode());
            pstmt.setString(2, aircraft.getModel());
            pstmt.setInt(3, aircraft.getTotalSeats());
            pstmt.setInt(4, aircraft.getAirlineId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating aircraft failed, no rows affected.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                aircraft.setAircraftId(rs.getInt(1));
            } else {
                throw new SQLException("Creating aircraft failed, no ID obtained.");
            }

            return aircraft;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error saving aircraft: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Update an existing aircraft
     *
     * @param aircraft Aircraft to update
     * @return Updated aircraft
     */
    @Override
    public Aircraft update(Aircraft aircraft) {
        String sql = "UPDATE Aircraft SET aircraft_code = ?, model = ?, total_seats = ?, airline_id = ? WHERE aircraft_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, aircraft.getAircraftCode());
            pstmt.setString(2, aircraft.getModel());
            pstmt.setInt(3, aircraft.getTotalSeats());
            pstmt.setInt(4, aircraft.getAirlineId());
            pstmt.setInt(5, aircraft.getAircraftId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating aircraft failed, no rows affected.");
            }

            return aircraft;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating aircraft: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find an aircraft by ID
     *
     * @param id Aircraft ID
     * @return Optional containing aircraft if found
     */
    @Override
    public Optional<Aircraft> findById(Integer id) {
        String sql = "SELECT * FROM Aircraft WHERE aircraft_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Aircraft aircraft = new Aircraft();
                aircraft.setAircraftId(rs.getInt("aircraft_id"));
                aircraft.setAircraftCode(rs.getString("aircraft_code"));
                aircraft.setModel(rs.getString("model"));
                aircraft.setTotalSeats(rs.getInt("total_seats"));
                aircraft.setAirlineId(rs.getInt("airline_id"));

                return Optional.of(aircraft);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding aircraft: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find all aircraft
     *
     * @return List of all aircraft
     */
    @Override
    public List<Aircraft> findAll() {
        String sql = "SELECT * FROM Aircraft";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Aircraft> aircraftList = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Aircraft aircraft = new Aircraft();
                aircraft.setAircraftId(rs.getInt("aircraft_id"));
                aircraft.setAircraftCode(rs.getString("aircraft_code"));
                aircraft.setModel(rs.getString("model"));
                aircraft.setTotalSeats(rs.getInt("total_seats"));
                aircraft.setAirlineId(rs.getInt("airline_id"));

                aircraftList.add(aircraft);
            }

            return aircraftList;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding all aircraft: " + e.getMessage());
            return aircraftList;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Delete an aircraft by ID
     *
     * @param id Aircraft ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Aircraft WHERE aircraft_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting aircraft: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find aircraft by airline ID
     *
     * @param airlineId Airline ID
     * @return List of aircraft for the given airline
     */
    public List<Aircraft> findByAirlineId(Integer airlineId) {
        String sql = "SELECT * FROM Aircraft WHERE airline_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Aircraft> aircraftList = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, airlineId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Aircraft aircraft = new Aircraft();
                aircraft.setAircraftId(rs.getInt("aircraft_id"));
                aircraft.setAircraftCode(rs.getString("aircraft_code"));
                aircraft.setModel(rs.getString("model"));
                aircraft.setTotalSeats(rs.getInt("total_seats"));
                aircraft.setAirlineId(rs.getInt("airline_id"));

                aircraftList.add(aircraft);
            }

            return aircraftList;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding aircraft by airline ID: " + e.getMessage());
            return aircraftList;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }
}
