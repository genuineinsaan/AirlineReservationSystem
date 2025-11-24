package gui;

import dao.BookingDAO;
import dao.FlightDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.toedter.calendar.JDateChooser; // Requires jcalendar-1.4.jar
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class UserDashboardView extends JFrame {

    private int userId;
    private JButton searchFlightsButton, selectSeatsButton, viewMyBookingsButton, cancelBookingButton, myAccountButton, logoutButton;
    private JLabel sourceLabel, destinationLabel, dateLabel;
    private JComboBox<String> sourceComboBox, destinationComboBox;
    private JDateChooser dateChooser;
    private JTable displayTable;
    private DefaultTableModel tableModel;

    // List of cities for the dropdowns
    private final ArrayList<String> CITIES_LIST = new ArrayList<>(List.of(
        "Select City", "Delhi", "Mumbai", "Kolkata", "Bengaluru", "Chennai", "Hyderabad", "Pune", "Ahmedabad"
    ));

    private FlightDAO flightDAO;
    private BookingDAO bookingDAO;

    // Column headers for the two different views (Flights vs Bookings)
    private final String[] flightColumns = {"Flight ID", "Company", "Flight No", "Source", "Destination", "Dep Time", "Arr Time", "Price (₹)"};
    private final String[] bookingColumns = {"Trans ID", "Date Booked", "Company", "Flight No", "Route", "Flight Date", "Seats", "# Seats", "Total (₹)"};

    // Flag to prevent infinite loops when updating dropdowns
    private boolean isUpdatingComboBoxes = false;

    public UserDashboardView(int userId) {
        this.userId = userId;
        flightDAO = new FlightDAO();
        bookingDAO = new BookingDAO();

        setTitle("User Dashboard - V2.0");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // "Back" behavior
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Left Panel (Buttons) ---
        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        
        selectSeatsButton = new JButton("Select Seats for Flight");
        viewMyBookingsButton = new JButton("View My Bookings");
        cancelBookingButton = new JButton("Cancel Booking");
        myAccountButton = new JButton("My Account");
        logoutButton = new JButton("Logout");

        buttonPanel.add(selectSeatsButton);
        buttonPanel.add(viewMyBookingsButton);
        buttonPanel.add(cancelBookingButton);
        buttonPanel.add(myAccountButton);
        buttonPanel.add(new JLabel()); // Spacer
        buttonPanel.add(new JLabel()); // Spacer
        buttonPanel.add(logoutButton);
        mainPanel.add(buttonPanel, BorderLayout.WEST);

        // --- Top Panel (Search) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search for Flights"));
        
        sourceLabel = new JLabel("Source:");
        sourceComboBox = new JComboBox<>(CITIES_LIST.toArray(new String[0]));
        sourceComboBox.setPreferredSize(new Dimension(150, 25));

        destinationLabel = new JLabel("Destination:");
        destinationComboBox = new JComboBox<>(CITIES_LIST.toArray(new String[0]));
        destinationComboBox.setPreferredSize(new Dimension(150, 25));

        dateLabel = new JLabel("Date:");
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setPreferredSize(new Dimension(130, 25));

        searchFlightsButton = new JButton("Search Flights");
        searchFlightsButton.setBackground(new Color(100, 149, 237)); // Cornflower Blue
        searchFlightsButton.setForeground(Color.WHITE);

        searchPanel.add(sourceLabel);
        searchPanel.add(sourceComboBox);
        searchPanel.add(destinationLabel);
        searchPanel.add(destinationComboBox);
        searchPanel.add(dateLabel);
        searchPanel.add(dateChooser);
        searchPanel.add(searchFlightsButton);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // --- Center Panel (Table) ---
        tableModel = new DefaultTableModel(flightColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        displayTable = new JTable(tableModel);
        displayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        displayTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(displayTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);

        // --- ACTION LISTENERS ---

        // 1. Dropdown Filtering Logic
        sourceComboBox.addActionListener(e -> {
            if (!isUpdatingComboBoxes) updateCityComboBoxes(sourceComboBox);
        });
        destinationComboBox.addActionListener(e -> {
            if (!isUpdatingComboBoxes) updateCityComboBoxes(destinationComboBox);
        });

        // 2. Search Button
        searchFlightsButton.addActionListener(e -> {
            String source = (String) sourceComboBox.getSelectedItem();
            String destination = (String) destinationComboBox.getSelectedItem();
            Date selectedDate = dateChooser.getDate();

            if (source == null || source.equals("Select City") || destination == null || destination.equals("Select City") || selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Please select Source, Destination, and Date.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Object[]> results = flightDAO.searchFlights(source, destination, selectedDate);
            
            // Update Table
            tableModel.setColumnIdentifiers(flightColumns);
            tableModel.setRowCount(0);
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No flights found for this route on this day.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Object[] row : results) {
                    tableModel.addRow(row);
                }
            }
        });

        // 3. Select Seats Button
        selectSeatsButton.addActionListener(e -> {
            // Validation: Ensure we are looking at flights, not bookings
            if (!displayTable.getColumnName(0).equals("Flight ID")) {
                JOptionPane.showMessageDialog(this, "Please search for flights first.", "Action Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int selectedRow = displayTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a flight from the table first.", "No Flight Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int flightId = (int) tableModel.getValueAt(selectedRow, 0);
            Date selectedDate = dateChooser.getDate(); // Use the search date
            
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Date is missing. Please search again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Open Seat Selection
            new SeatSelectionView(flightId, userId, selectedDate);
        });

        // 4. View My Bookings
        viewMyBookingsButton.addActionListener(e -> {
            List<Object[]> results = bookingDAO.getUserBookingsForTable(this.userId);
            
            tableModel.setColumnIdentifiers(bookingColumns);
            tableModel.setRowCount(0);
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You have no bookings.", "No Bookings", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Object[] row : results) {
                    tableModel.addRow(row);
                }
            }
        });

        // 5. Cancel Booking
        cancelBookingButton.addActionListener(e -> {
            if (!displayTable.getColumnName(0).equals("Trans ID")) {
                JOptionPane.showMessageDialog(this, "Please click 'View My Bookings' first.", "Action Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int selectedRow = displayTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a booking to cancel.", "No Booking Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to cancel Booking ID " + transactionId + "?\nThis will cancel ALL seats in this transaction.", 
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = bookingDAO.cancelBooking(this.userId, transactionId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Booking Cancelled Successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Refresh list
                    viewMyBookingsButton.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Cancellation Failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 6. My Account
        myAccountButton.addActionListener(e -> {
            new MyAccountView(this.userId);
        });

        // 7. Logout
        logoutButton.addActionListener(e -> {
            new LoginView();
            dispose();
        });
    }

    // Helper: Updates City Dropdowns to prevent selecting same source/dest
    private void updateCityComboBoxes(JComboBox<String> changedComboBox) {
        isUpdatingComboBoxes = true;
        JComboBox<String> otherComboBox = (changedComboBox == sourceComboBox) ? destinationComboBox : sourceComboBox;
        String selectedCity = (String) changedComboBox.getSelectedItem();
        String otherSelectedCity = (String) otherComboBox.getSelectedItem();
        
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) otherComboBox.getModel();
        model.removeAllElements();
        model.addElement("Select City");
        
        for (String city : CITIES_LIST) {
            // Add city if it's not the "Select City" default AND not the city selected in the other box
            if (!city.equals("Select City") && (selectedCity == null || selectedCity.equals("Select City") || !city.equals(selectedCity))) {
                model.addElement(city);
            }
        }
        
        // Try to keep the previous selection if valid
        if (otherSelectedCity != null && model.getIndexOf(otherSelectedCity) != -1) {
            otherComboBox.setSelectedItem(otherSelectedCity);
        } else {
            otherComboBox.setSelectedIndex(0);
        }
        isUpdatingComboBoxes = false;
    }
}