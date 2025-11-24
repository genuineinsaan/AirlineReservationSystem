package dao;

import java.sql.*;
import db.DBConnection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class BookingDAO {

    /**
     * 1. User: Gets booking details for a specific user, grouped by transaction.
     * Returns data formatted for the JTable.
     */
    public List<Object[]> getUserBookingsForTable(int userId) {
        List<Object[]> bookingsData = new ArrayList<>();
        // This query joins transactions, booked_seats, and flights.
        // It uses GROUP_CONCAT to show all seats (e.g., "3A, 3B") in one cell.
        String query = """
            SELECT
                t.transaction_id,
                t.transaction_date,
                f.flight_company,
                f.flight_no,
                f.source,
                f.destination,
                bs.flight_date,
                GROUP_CONCAT(CONCAT(bs.seat_row, bs.seat_column) ORDER BY bs.seat_row, bs.seat_column SEPARATOR ', ') AS seats,
                COUNT(bs.seat_id) AS num_seats,
                t.total_amount
            FROM transactions t
            JOIN booked_seats bs ON t.transaction_id = bs.transaction_id
            JOIN flights f ON bs.flight_id = f.id
            WHERE t.user_id = ?
            GROUP BY t.transaction_id, f.id, bs.flight_date
            ORDER BY t.transaction_date DESC;
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("transaction_id"),
                        rs.getTimestamp("transaction_date").toString(),
                        rs.getString("flight_company"),
                        rs.getString("flight_no"),
                        rs.getString("source") + " -> " + rs.getString("destination"),
                        rs.getDate("flight_date").toString(),
                        rs.getString("seats"),
                        rs.getInt("num_seats"),
                        rs.getDouble("total_amount")
                };
                bookingsData.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookingsData;
    }
    
    /**
     * 2. Admin: Gets ALL bookings from ALL users for the Admin JTable.
     */
    public List<Object[]> getAllBookingsForTable() {
        List<Object[]> bookingsData = new ArrayList<>();
        String query = """
            SELECT
                t.transaction_id, u.email AS user_email, f.flight_no,
                bs.flight_date,
                GROUP_CONCAT(CONCAT(bs.seat_row, bs.seat_column) SEPARATOR ', ') AS seats,
                t.total_amount
            FROM transactions t
            JOIN users u ON t.user_id = u.id
            JOIN booked_seats bs ON t.transaction_id = bs.transaction_id
            JOIN flights f ON bs.flight_id = f.id
            GROUP BY t.transaction_id, u.email, f.flight_no, bs.flight_date
            ORDER BY t.transaction_date DESC;
            """;
            
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("transaction_id"),
                        rs.getString("user_email"),
                        rs.getString("flight_no"),
                        rs.getDate("flight_date").toString(),
                        rs.getString("seats"),
                        rs.getDouble("total_amount")
                };
                bookingsData.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookingsData;
    }

    /**
     * 3. Helper: Gets occupied seats for a specific flight on a specific date.
     * Used by the Seat Map.
     */
    public Set<String> getBookedSeats(int flightId, Date flightDate) {
        Set<String> bookedSeats = new HashSet<>();
        java.sql.Date sqlFlightDate = new java.sql.Date(flightDate.getTime());
        
        String sql = "SELECT seat_row, seat_column FROM booked_seats WHERE flight_id = ? AND flight_date = ?";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            ps.setDate(2, sqlFlightDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookedSeats.add(rs.getInt("seat_row") + rs.getString("seat_column"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedSeats;
    }

    /**
     * 4. User: Books multiple seats in a single transaction.
     * This is the most critical method. It uses a Database Transaction.
     */
    public String bookMultipleSeats(int userId, int flightId, Date flightDate, List<String> seatIds) {
        String insertBookingSQL = "INSERT INTO booked_seats (transaction_id, flight_id, flight_date, seat_row, seat_column, seat_price) VALUES (?, ?, ?, ?, ?, ?)";
        String priceQuerySQL = "SELECT base_price FROM flights WHERE id = ?";
        String transactionSQL = "INSERT INTO transactions (user_id, total_amount) VALUES (?, ?)";
        
        Connection conn = null;
        double totalAmount = 0;
        int transactionId = -1;
        java.sql.Date sqlFlightDate = new java.sql.Date(flightDate.getTime());
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // Step A: Get Base Price
            double basePrice = 0.0;
            try (PreparedStatement priceStmt = conn.prepareStatement(priceQuerySQL)) {
                priceStmt.setInt(1, flightId);
                ResultSet rs = priceStmt.executeQuery();
                if (rs.next()) { basePrice = rs.getDouble("base_price"); }
                else { conn.rollback(); return "Error: Invalid Flight ID."; }
            }
            
            // Step B: Calculate Total Amount
            for (String seatId : seatIds) {
                int row = Integer.parseInt(seatId.replaceAll("[^0-9]", ""));
                double multiplier = (row <= 3) ? 1.5 : (row <= 7) ? 1.2 : 1.0;
                totalAmount += (basePrice * multiplier);
            }
            
            // Step C: Create Transaction Record
            try (PreparedStatement txStmt = conn.prepareStatement(transactionSQL, Statement.RETURN_GENERATED_KEYS)) {
                txStmt.setInt(1, userId);
                txStmt.setDouble(2, totalAmount);
                txStmt.executeUpdate();
                ResultSet generatedKeys = txStmt.getGeneratedKeys();
                if (generatedKeys.next()) { 
                    transactionId = generatedKeys.getInt(1); 
                } else { 
                    conn.rollback(); return "Error: Could not create transaction record."; 
                }
            }
            
            // Step D: Insert Individual Seats
            try (PreparedStatement insertStmt = conn.prepareStatement(insertBookingSQL)) {
                for (String seatId : seatIds) {
                    int row = Integer.parseInt(seatId.replaceAll("[^0-9]", ""));
                    char col = seatId.replaceAll("[^A-Z]", "").charAt(0);
                    double multiplier = (row <= 3) ? 1.5 : (row <= 7) ? 1.2 : 1.0;
                    double seatPrice = basePrice * multiplier;
                    
                    insertStmt.setInt(1, transactionId);
                    insertStmt.setInt(2, flightId);
                    insertStmt.setDate(3, sqlFlightDate);
                    insertStmt.setInt(4, row);
                    insertStmt.setString(5, String.valueOf(col));
                    insertStmt.setDouble(6, seatPrice);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
            
            conn.commit(); // Commit Transaction
            return String.format("✅ Booking successful for %d seats!\nTransaction ID: %d\nTotal Amount Paid: ₹%.2f", seatIds.size(), transactionId, totalAmount);
            
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (e.getMessage().contains("Duplicate entry")) {
                 return "❌ Booking failed. One or more selected seats might already be booked for this date. Please try again.";
            }
            e.printStackTrace();
            return "❌ Booking failed due to a database error.";
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    /**
     * 5. User: Cancels an entire transaction.
     */
    public boolean cancelBooking(int userId, int transactionId) {
        Connection conn = null;
        String deleteSeatsSQL = "DELETE FROM booked_seats WHERE transaction_id = ?";
        String deleteTxSQL = "DELETE FROM transactions WHERE transaction_id = ? AND user_id = ?"; // Security check

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // Step A: Delete Seats
            try (PreparedStatement psSeats = conn.prepareStatement(deleteSeatsSQL)) {
                psSeats.setInt(1, transactionId);
                psSeats.executeUpdate();
            }

            // Step B: Delete Transaction Header
            try (PreparedStatement psTx = conn.prepareStatement(deleteTxSQL)) {
                psTx.setInt(1, transactionId);
                psTx.setInt(2, userId);
                int rowsAffected = psTx.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit(); // Success
                    return true;
                } else {
                    conn.rollback(); // Failed (wrong user or id)
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            return false;
        } finally {
             if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }
    
    // Placeholder for compatibility
    public String bookFlight(int userId, int flightId, int row, char col) { return ""; }
    public String getAllBookingsAsString() { return ""; }
}