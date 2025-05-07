package com.mycompany.bookingsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Passenger entity
 */
public class PassengerDAO implements Dao<Passenger, Integer> {

    /**
     * Save a new passenger to the database
     *
     * @param passenger Passenger to save
     * @return Passenger with generated ID
     */
    @Override
    public Passenger save(Passenger passenger) {
        String sql = "INSERT INTO Passengers (first_name, last_name, date_of_birth, gender, "
                + "passport_number, nationality, email, phone_number, address) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, passenger.getFirstName());
            pstmt.setString(2, passenger.getLastName());
            pstmt.setDate(3, java.sql.Date.valueOf(passenger.getDateOfBirth()));
            pstmt.setString(4, String.valueOf(passenger.getGender()));
            pstmt.setString(5, passenger.getPassportNumber());
            pstmt.setString(6, passenger.getNationality());
            pstmt.setString(7, passenger.getEmail());
            pstmt.setString(8, passenger.getPhoneNumber());
            pstmt.setString(9, passenger.getAddress());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating passenger failed, no rows affected.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                passenger.setPassengerId(rs.getInt(1));
            } else {
                throw new SQLException("Creating passenger failed, no ID obtained.");
            }

            return passenger;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error saving passenger: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Update an existing passenger
     *
     * @param passenger Passenger to update
     * @return Updated passenger
     */
    @Override
    public Passenger update(Passenger passenger) {
        String sql = "UPDATE Passengers SET first_name = ?, last_name = ?, date_of_birth = ?, "
                + "gender = ?, passport_number = ?, nationality = ?, email = ?, "
                + "phone_number = ?, address = ? WHERE passenger_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, passenger.getFirstName());
            pstmt.setString(2, passenger.getLastName());
            pstmt.setDate(3, java.sql.Date.valueOf(passenger.getDateOfBirth()));
            pstmt.setString(4, String.valueOf(passenger.getGender()));
            pstmt.setString(5, passenger.getPassportNumber());
            pstmt.setString(6, passenger.getNationality());
            pstmt.setString(7, passenger.getEmail());
            pstmt.setString(8, passenger.getPhoneNumber());
            pstmt.setString(9, passenger.getAddress());
            pstmt.setInt(10, passenger.getPassengerId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating passenger failed, no rows affected.");
            }

            return passenger;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating passenger: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find a passenger by ID
     *
     * @param id Passenger ID
     * @return Optional containing passenger if found
     */
    @Override
    public Optional<Passenger> findById(Integer id) {
        String sql = "SELECT * FROM Passengers WHERE passenger_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setPassengerId(rs.getInt("passenger_id"));
                passenger.setFirstName(rs.getString("first_name"));
                passenger.setLastName(rs.getString("last_name"));
                passenger.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                passenger.setGender(rs.getString("gender").charAt(0));
                passenger.setPassportNumber(rs.getString("passport_number"));
                passenger.setNationality(rs.getString("nationality"));
                passenger.setEmail(rs.getString("email"));
                passenger.setPhoneNumber(rs.getString("phone_number"));
                passenger.setAddress(rs.getString("address"));
                passenger.setCreatedAt(rs.getTimestamp("created_at"));
                passenger.setUpdatedAt(rs.getTimestamp("updated_at"));

                return Optional.of(passenger);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding passenger: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find passenger by email
     *
     * @param email Passenger email
     * @return Optional containing passenger if found
     */
    public Optional<Passenger> findByEmail(String email) {
        String sql = "SELECT * FROM Passengers WHERE email = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setPassengerId(rs.getInt("passenger_id"));
                passenger.setFirstName(rs.getString("first_name"));
                passenger.setLastName(rs.getString("last_name"));
                passenger.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                passenger.setGender(rs.getString("gender").charAt(0));
                passenger.setPassportNumber(rs.getString("passport_number"));
                passenger.setNationality(rs.getString("nationality"));
                passenger.setEmail(rs.getString("email"));
                passenger.setPhoneNumber(rs.getString("phone_number"));
                passenger.setAddress(rs.getString("address"));
                passenger.setCreatedAt(rs.getTimestamp("created_at"));
                passenger.setUpdatedAt(rs.getTimestamp("updated_at"));

                return Optional.of(passenger);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding passenger by email: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find all passengers
     *
     * @return List of all passengers
     */
    @Override
    public List<Passenger> findAll() {
        String sql = "SELECT * FROM Passengers";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Passenger> passengers = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setPassengerId(rs.getInt("passenger_id"));
                passenger.setFirstName(rs.getString("first_name"));
                passenger.setLastName(rs.getString("last_name"));
                passenger.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                passenger.setGender(rs.getString("gender").charAt(0));
                passenger.setPassportNumber(rs.getString("passport_number"));
                passenger.setNationality(rs.getString("nationality"));
                passenger.setEmail(rs.getString("email"));
                passenger.setPhoneNumber(rs.getString("phone_number"));
                passenger.setAddress(rs.getString("address"));
                passenger.setCreatedAt(rs.getTimestamp("created_at"));
                passenger.setUpdatedAt(rs.getTimestamp("updated_at"));

                passengers.add(passenger);
            }

            return passengers;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding all passengers: " + e.getMessage());
            return passengers;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Delete a passenger by ID
     *
     * @param id Passenger ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Passengers WHERE passenger_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting passenger: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }
}
