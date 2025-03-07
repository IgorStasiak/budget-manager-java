package org.example;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BudgetManager {
    private static final String DB_URL = "jdbc:sqlite:budget.db";
    private List<Transaction> transactions = new ArrayList<>();

    public BudgetManager() {
        createDatabase();
        loadTransactions();
    }

    private void createDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "amount REAL, " +
                    "category TEXT, " +
                    "isIncome INTEGER, " +
                    "dateTime TEXT)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
        }
    }

    public void addTransaction(double amount, String category, boolean isIncome) {
        String sql = "INSERT INTO transactions (amount, category, isIncome, dateTime) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = LocalDateTime.now().format(formatter);

            pstmt.setDouble(1, amount);
            pstmt.setString(2, category);
            pstmt.setBoolean(3, isIncome);
            pstmt.setString(4, formattedDateTime);

            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                transactions.add(new Transaction(id, amount, category, isIncome, formattedDateTime));
            }
        } catch (SQLException e) {
            System.out.println("Error adding transaction: " + e.getMessage());
        }
    }

    private void saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (amount, category, isIncome, dateTime) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, transaction.getAmount());
            pstmt.setString(2, transaction.getCategory());
            pstmt.setInt(3, transaction.isIncome() ? 1 : 0);
            pstmt.setString(4, transaction.getDateTime());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    private void loadTransactions() {
        transactions.clear();
        String sql = "SELECT id, amount, category, isIncome, dateTime FROM transactions";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getInt("isIncome") == 1,
                        rs.getString("dateTime")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    public void deleteTransaction(int id) {
        transactions.removeIf(t -> t.getId() == id);

        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("No transaction found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting transaction: " + e.getMessage());
        }
    }

    public void clearAllTransactions() {
        transactions.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM transactions");
        } catch (SQLException e) {
            System.out.println("Error clearing transactions: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public double getBalance() {
        double balance = 0;
        for (Transaction t : transactions) {
            if (t.isIncome()) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }
        }
        return balance;
    }
}