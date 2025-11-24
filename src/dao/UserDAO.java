package dao;

import java.sql.*;
import db.DBConnection;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {

    // 1. Register a new user (V2: First Name, Last Name, Phone)
    public boolean register(String firstName, String lastName, String email, String phone, String password) {
        String sql = "INSERT INTO users (first_name, last_name, email, phone_number, password, role) VALUES (?, ?, ?, ?, ?, 'user')";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, password); // Storing as plain text as per request
            
            ps.executeUpdate();
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("⚠️ Email '" + email + "' already exists!");
        } catch (SQLException e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
        }
        return false;
    }

    // 2. Login (Returns the user's First Name on success)
    public String login(String email, String password) {
        String sql = "SELECT password, role, first_name FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (storedPassword != null && storedPassword.equals(password)) {
                        return rs.getString("first_name"); // Return name for greeting
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Login failed: " + e.getMessage());
        }
        return null;
    }

    // 3. Helper: Get User ID
    public int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // 4. Helper: Get User Role
    public String getUserRole(String email) {
        String sql = "SELECT role FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("role");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // 5. Check if Profile is Complete (V2 Feature)
    public boolean isProfileComplete(int userId) {
        String sql = "SELECT dob FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // If DOB is not null, we consider the profile complete
                    return rs.getDate("dob") != null;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // 6. Complete Profile (V2 Feature: updates DOB, Gender, Address, Zip, Country)
    public boolean completeProfile(int userId, String dob, String gender, String address, String zipCode, String country) {
        try {
            java.sql.Date sqlDob = java.sql.Date.valueOf(dob); // Expects YYYY-MM-DD
            
            String sql = "UPDATE users SET dob = ?, gender = ?, address = ?, zip_code = ?, country = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setDate(1, sqlDob);
                ps.setString(2, gender);
                ps.setString(3, address);
                ps.setString(4, zipCode);
                ps.setString(5, country);
                ps.setInt(6, userId);

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Invalid date format. Please use YYYY-MM-DD.");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 7. Get User Details for "My Account" Page
    public Map<String, String> getUserDetails(int userId) {
        Map<String, String> userDetails = new HashMap<>();
        String sql = "SELECT first_name, last_name, email, phone_number, dob, gender, address, zip_code, country FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userDetails.put("firstName", rs.getString("first_name"));
                userDetails.put("lastName", rs.getString("last_name"));
                userDetails.put("email", rs.getString("email"));
                userDetails.put("phone", rs.getString("phone_number"));
                Date dob = rs.getDate("dob");
                userDetails.put("dob", (dob != null) ? dob.toString() : "");
                userDetails.put("gender", rs.getString("gender"));
                userDetails.put("address", rs.getString("address"));
                userDetails.put("zipCode", rs.getString("zip_code"));
                userDetails.put("country", rs.getString("country"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return userDetails;
    }

    // 8. Update Password
    public boolean updatePassword(int userId, String currentPassword, String newPassword) {
        String checkSql = "SELECT password FROM users WHERE id = ?";
        String updateSql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Check current password
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, userId);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next()) {
                    String stored = rs.getString("password");
                    if (stored == null || !stored.equals(currentPassword)) return false;
                } else { return false; }
            }

            // Update to new password
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setString(1, newPassword);
                updatePs.setInt(2, userId);
                return updatePs.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}