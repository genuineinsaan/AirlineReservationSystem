package dao;

import java.sql.*;
import db.DBConnection;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FlightDAO {

    /**
     * 1. Admin: Adds a flight schedule based on days of the week.
     * operatesOn is a boolean array: [mon, tue, wed, thu, fri, sat, sun]
     */
    public boolean addFlightSchedule(String company, String flightNo, String source, String destination,
                                     Time departureTime, Time arrivalTime, double price, boolean[] operatesOn) {
        String sql = """
            INSERT INTO flights (
                flight_company, flight_no, source, destination,
                departure_time, arrival_time, base_price,
                operates_mon, operates_tue, operates_wed, operates_thu,
                operates_fri, operates_sat, operates_sun
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, company);
            ps.setString(2, flightNo);
            ps.setString(3, source);
            ps.setString(4, destination);
            ps.setTime(5, departureTime);
            ps.setTime(6, arrivalTime);
            ps.setDouble(7, price);
            // Set boolean values for the 7 days
            for (int i = 0; i < 7; i++) {
                ps.setBoolean(8 + i, operatesOn[i]);
            }

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Add flight failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * 2. Helper: Checks if a flight number already exists to prevent duplicates.
     */
    public boolean isFlightNumberExists(String flightNo) {
        String sql = "SELECT COUNT(*) FROM flights WHERE flight_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flightNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * 3. Admin: Gets all flight schedules formatted for the JTable.
     * Converts the boolean flags (operates_mon) into a string "Mon, Wed, Fri".
     */
    public List<Object[]> getAllFlightSchedulesForTable() {
        List<Object[]> flightsData = new ArrayList<>();
        String sql = "SELECT * FROM flights ORDER BY flight_company, flight_no";
        
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                StringBuilder days = new StringBuilder();
                if (rs.getBoolean("operates_mon")) days.append("Mon,");
                if (rs.getBoolean("operates_tue")) days.append("Tue,");
                if (rs.getBoolean("operates_wed")) days.append("Wed,");
                if (rs.getBoolean("operates_thu")) days.append("Thu,");
                if (rs.getBoolean("operates_fri")) days.append("Fri,");
                if (rs.getBoolean("operates_sat")) days.append("Sat,");
                if (rs.getBoolean("operates_sun")) days.append("Sun,");
                
                // Remove trailing comma
                String daysStr = days.length() > 0 ? days.substring(0, days.length() - 1) : "N/A";

                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("flight_company"),
                        rs.getString("flight_no"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getTime("departure_time").toString(),
                        rs.getTime("arrival_time").toString(),
                        rs.getDouble("base_price"),
                        daysStr 
                };
                flightsData.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return flightsData;
    }

    /**
     * 4. User: Searches for flights based on Source, Dest, and Date.
     * It converts the Date to a Day of Week (e.g., Monday) and checks if the flight runs on that day.
     */
    public List<Object[]> searchFlights(String source, String destination, Date date) {
        List<Object[]> flightsData = new ArrayList<>();
        if (date == null) return flightsData;
        
        // Get day of week from the selected date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        
        // Map Java's Calendar day to our DB column
        String dayColumn;
        switch (dayOfWeek) {
            case Calendar.MONDAY:    dayColumn = "operates_mon"; break;
            case Calendar.TUESDAY:   dayColumn = "operates_tue"; break;
            case Calendar.WEDNESDAY: dayColumn = "operates_wed"; break;
            case Calendar.THURSDAY:  dayColumn = "operates_thu"; break;
            case Calendar.FRIDAY:    dayColumn = "operates_fri"; break;
            case Calendar.SATURDAY:  dayColumn = "operates_sat"; break;
            case Calendar.SUNDAY:    dayColumn = "operates_sun"; break;
            default: return flightsData;
        }

        String sql = "SELECT id, flight_company, flight_no, source, destination, departure_time, arrival_time, base_price " +
                     "FROM flights " +
                     "WHERE source LIKE ? AND destination LIKE ? AND " + dayColumn + " = TRUE " +
                     "ORDER BY departure_time";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + source + "%");
            ps.setString(2, "%" + destination + "%");
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"), 
                        rs.getString("flight_company"), 
                        rs.getString("flight_no"),
                        rs.getString("source"), 
                        rs.getString("destination"),
                        rs.getTime("departure_time").toString(), 
                        rs.getTime("arrival_time").toString(),
                        rs.getDouble("base_price")
                };
                flightsData.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return flightsData;
    }

    /**
     * 5. Helper: Gets the base price for a flight ID.
     */
    public double getBasePrice(int flightId) {
        String sql = "SELECT base_price FROM flights WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("base_price");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
    
    // Old testing method (Deprecated in V2)
    public String getAllFlightsAsString() { return "Deprecated"; }
}