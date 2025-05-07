package com.mycompany.bookingsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entity
 */
public class UserDAO implements Dao<User, Integer> {

    /**
     * Save a new user to the database
     *
     * @param user User to save
     * @return User with generated ID
     */
    @Override
    public User save(User user) {
        String sql = "INSERT INTO Users (username, password_hash, role, is_active) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.setBoolean(4, user.isActive());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                user.setUserId(rs.getInt(1));
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }

            return user;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Update an existing user
     *
     * @param user User to update
     * @return Updated user
     */
    @Override
    public User update(User user) {
        String sql = "UPDATE Users SET username = ?, password_hash = ?, role = ?, is_active = ?, last_login = ? WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.setBoolean(4, user.isActive());

            if (user.getLastLogin() != null) {
                pstmt.setTimestamp(5, user.getLastLogin());
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }

            pstmt.setInt(6, user.getUserId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }

            return user;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find a user by ID
     *
     * @param id User ID
     * @return Optional containing user if found
     */
    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                return Optional.of(user);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding user: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find all users
     *
     * @return List of all users
     */
    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM Users";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }

            return users;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding all users: " + e.getMessage());
            return users;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Delete a user by ID
     *
     * @param id User ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Users WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find a user by username
     *
     * @param username Username to search for
     * @return Optional containing user if found
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                return Optional.of(user);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Update last login timestamp for user
     *
     * @param userId User ID
     * @param timestamp Last login timestamp
     * @return true if update was successful
     */
    public boolean updateLastLogin(int userId, Timestamp timestamp) {
        String sql = "UPDATE Users SET last_login = ? WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, timestamp);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating last login: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find users by role
     *
     * @param role Role to search for
     * @return List of users with the given role
     */
    public List<User> findByRole(String role) {
        String sql = "SELECT * FROM Users WHERE role = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }

            return users;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding users by role: " + e.getMessage());
            return users;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Helper method to map ResultSet to User object
     *
     * @param rs ResultSet containing user data
     * @return User object
     * @throws SQLException if a database access error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("is_active"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
