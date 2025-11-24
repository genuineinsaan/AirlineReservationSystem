package gui;

import dao.BookingDAO;
import dao.FlightDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardView extends JFrame {
    
    private JButton addFlightButton, viewFlightsButton, viewBookingsButton, logoutButton;
    private JTable displayTable;
    private DefaultTableModel tableModel;
    private FlightDAO flightDAO;
    private BookingDAO bookingDAO;

    // Column headers for the two different views
    private final String[] flightColumns = {"ID", "Company", "Flight No", "Source", "Dest", "Dep Time", "Arr Time", "Price(₹)", "Days"};
    private final String[] bookingColumns = {"Trans ID", "User Email", "Flight No", "Flight Date", "Seats", "Total (₹)"};

    public AdminDashboardView() {
        flightDAO = new FlightDAO();
        bookingDAO = new BookingDAO();

        setTitle("Admin Dashboard - V2.0");
        setSize(950, 600);
        // Acts like a "Back" button (returns to previous context usually, or just closes this specific window)
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Left Panel (Buttons) ---
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        
        addFlightButton = new JButton("Add Flight Schedule");
        viewFlightsButton = new JButton("View All Schedules");
        viewBookingsButton = new JButton("View All Bookings");
        logoutButton = new JButton("Logout");
        
        buttonPanel.add(addFlightButton);
        buttonPanel.add(viewFlightsButton);
        buttonPanel.add(viewBookingsButton);
        buttonPanel.add(new JLabel()); // Spacer
        buttonPanel.add(logoutButton);
        
        mainPanel.add(buttonPanel, BorderLayout.WEST);

        // --- Center Panel (Table) ---
        // Initialize with flight columns by default
        tableModel = new DefaultTableModel(flightColumns, 0) {
             @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        displayTable = new JTable(tableModel);
        displayTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(displayTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        // --- ACTION LISTENERS ---

        // 1. Add Flight
        addFlightButton.addActionListener(e -> {
            new AddFlightView(); // Open the Add Flight Window
        });

        // 2. View All Flights
        viewFlightsButton.addActionListener(e -> {
            List<Object[]> results = flightDAO.getAllFlightSchedulesForTable();
            
            tableModel.setColumnIdentifiers(flightColumns); // Set correct headers
            tableModel.setRowCount(0); // Clear table
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No flight schedules found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Object[] row : results) {
                    tableModel.addRow(row);
                }
            }
        });

        // 3. View All Bookings
        viewBookingsButton.addActionListener(e -> {
             List<Object[]> results = bookingDAO.getAllBookingsForTable();
             
             tableModel.setColumnIdentifiers(bookingColumns); // Set correct headers
             tableModel.setRowCount(0); // Clear table
             
             if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No bookings found.", "Info", JOptionPane.INFORMATION_MESSAGE);
             } else {
                for (Object[] row : results) {
                    tableModel.addRow(row);
                }
             }
        });

        // 4. Logout
        logoutButton.addActionListener(e -> {
            new LoginView();
            dispose();
        });
        
        // Automatically load flights on startup
        viewFlightsButton.doClick();
    }
}