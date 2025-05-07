package com.mycompany.bookingsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Transaction entity
 */
public class TransactionDAO implements Dao<Transaction, Integer> {

    /**
     * Save a new transaction to the database
     *
     * @param transaction Transaction to save
     * @return Transaction with generated ID
     */
    @Override
    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO Transactions (booking_id, amount, payment_method, transaction_status, transaction_date, reference_number) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, transaction.getBookingId());
            pstmt.setBigDecimal(2, transaction.getAmount());
            pstmt.setString(3, transaction.getPaymentMethod());
            pstmt.setString(4, transaction.getTransactionStatus());

            // If transaction date is not set, use current timestamp
            if (transaction.getTransactionDate() == null) {
                pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            } else {
                pstmt.setTimestamp(5, transaction.getTransactionDate());
            }

            pstmt.setString(6, transaction.getReferenceNumber());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                transaction.setTransactionId(rs.getInt(1));
            } else {
                throw new SQLException("Creating transaction failed, no ID obtained.");
            }

            return transaction;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Update an existing transaction
     *
     * @param transaction Transaction to update
     * @return Updated transaction
     */
    @Override
    public Transaction update(Transaction transaction) {
        String sql = "UPDATE Transactions SET booking_id = ?, amount = ?, payment_method = ?, "
                + "transaction_status = ?, transaction_date = ?, reference_number = ? "
                + "WHERE transaction_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, transaction.getBookingId());
            pstmt.setBigDecimal(2, transaction.getAmount());
            pstmt.setString(3, transaction.getPaymentMethod());
            pstmt.setString(4, transaction.getTransactionStatus());
            pstmt.setTimestamp(5, transaction.getTransactionDate());
            pstmt.setString(6, transaction.getReferenceNumber());
            pstmt.setInt(7, transaction.getTransactionId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating transaction failed, no rows affected.");
            }

            return transaction;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error updating transaction: " + e.getMessage());
            return null;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find a transaction by ID
     *
     * @param id Transaction ID
     * @return Optional containing transaction if found
     */
    @Override
    public Optional<Transaction> findById(Integer id) {
        String sql = "SELECT * FROM Transactions WHERE transaction_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Transaction transaction = extractTransactionFromResultSet(rs);
                return Optional.of(transaction);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding transaction: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find all transactions
     *
     * @return List of all transactions
     */
    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM Transactions";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Transaction> transactions = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = extractTransactionFromResultSet(rs);
                transactions.add(transaction);
            }

            return transactions;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding all transactions: " + e.getMessage());
            return transactions;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Delete a transaction by ID
     *
     * @param id Transaction ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM Transactions WHERE transaction_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeQuietly(pstmt, conn);
        }
    }

    /**
     * Find transactions by booking ID
     *
     * @param bookingId Booking ID
     * @return List of transactions for the booking
     */
    public List<Transaction> findByBookingId(Integer bookingId) {
        String sql = "SELECT * FROM Transactions WHERE booking_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Transaction> transactions = new ArrayList<>();

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = extractTransactionFromResultSet(rs);
                transactions.add(transaction);
            }

            return transactions;

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding transactions by booking ID: " + e.getMessage());
            return transactions;
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Find transaction by reference number
     *
     * @param referenceNumber Transaction reference number
     * @return Optional containing transaction if found
     */
    public Optional<Transaction> findByReferenceNumber(String referenceNumber) {
        String sql = "SELECT * FROM Transactions WHERE reference_number = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, referenceNumber);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Transaction transaction = extractTransactionFromResultSet(rs);
                return Optional.of(transaction);
            }

            return Optional.empty();

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error finding transaction by reference number: " + e.getMessage());
            return Optional.empty();
        } finally {
            DatabaseConnection.closeQuietly(rs, pstmt, conn);
        }
    }

    /**
     * Helper method to extract a Transaction from a ResultSet
     *
     * @param rs ResultSet
     * @return Transaction object
     * @throws SQLException if a database access error occurs
     */
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setBookingId(rs.getInt("booking_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setPaymentMethod(rs.getString("payment_method"));
        transaction.setTransactionStatus(rs.getString("transaction_status"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
        transaction.setReferenceNumber(rs.getString("reference_number"));

        return transaction;
    }
}
