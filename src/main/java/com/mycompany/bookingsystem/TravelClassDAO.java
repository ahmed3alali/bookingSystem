package com.mycompany.bookingsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for TravelClass entity
 */
public class TravelClassDAO implements Dao<TravelClass, Integer> {

    /**
     * Save a new travel class to the database
     *
     * @param travelClass TravelClass to save
     * @return TravelClass with generated ID
     */
    @Override
    public TravelClass save(TravelClass travelClass) {
        String sql = "INSERT INTO TravelClasses (class_name, price_multiplier) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, travelClass.getClassName());
            pstmt.setBigDecimal(2, travelClass.getPriceMultiplier());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating travel class failed, no rows affected.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                travelClass.setClassId(rs.getInt(1));
            } else {
                throw new SQLException("Creating travel class failed, no ID obtained.");
            }

            return travelClass;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error saving travel class: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Update an existing travel class
     *
     * @param travelClass TravelClass to update
     * @return Updated travel class
     */
    @Override
    public TravelClass update(TravelClass travelClass) {
        String sql = "UPDATE TravelClasses SET class_name = ?, price_multiplier = ? WHERE class_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, travelClass.getClassName());
            pstmt.setBigDecimal(2, travelClass.getPriceMultiplier());
            pstmt.setInt(3, travelClass.getClassId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating travel class failed, no rows affected.");
            }

            return travelClass;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating travel class: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find a travel class by ID
     *
     * @param id TravelClass ID
     * @return Optional containing travel class if found
     */
    @Override
    public Optional<TravelClass> findById(Integer id) {
        String sql = "SELECT * FROM TravelClasses WHERE class_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                TravelClass travelClass = new TravelClass();
                travelClass.setClassId(rs.getInt("class_id"));
                travelClass.setClassName(rs.getString("class_name"));
                travelClass.setPriceMultiplier(rs.getBigDecimal("price_multiplier"));

                return Optional.of(travelClass);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding travel class: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find all travel classes
     *
     * @return List of all travel classes
     */
    @Override
    public List<TravelClass> findAll() {
        String sql = "SELECT * FROM TravelClasses";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TravelClass> travelClasses = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                TravelClass travelClass = new TravelClass();
                travelClass.setClassId(rs.getInt("class_id"));
                travelClass.setClassName(rs.getString("class_name"));
                travelClass.setPriceMultiplier(rs.getBigDecimal("price_multiplier"));

                travelClasses.add(travelClass);
            }

            return travelClasses;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding all travel classes: " + e.getMessage());
            return travelClasses;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Delete a travel class by ID
     *
     * @param id TravelClass ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM TravelClasses WHERE class_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting travel class: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }
}
