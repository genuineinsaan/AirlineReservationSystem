package gui;

import dao.BookingDAO;
import dao.FlightDAO;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat; // Explicitly imported to fix previous error
import javax.swing.*;
import java.awt.*;

public class SeatSelectionView extends JFrame {

    private int flightId;
    private int userId;
    private Date flightDate;
    private static final int ROWS = 10;
    private static final char LAST_COL = 'F';

    private BookingDAO bookingDAO;
    private FlightDAO flightDAO;
    private List<JToggleButton> seatButtons;

    public SeatSelectionView(int flightId, int userId, Date flightDate) {
        this.flightId = flightId;
        this.userId = userId;
        this.flightDate = flightDate;
        this.bookingDAO = new BookingDAO();
        this.flightDAO = new FlightDAO();
        this.seatButtons = new ArrayList<>();

        // Format date for title to verify we have the right day
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(flightDate);
        
        setTitle("Select Seats - Flight ID: " + flightId + " [" + formattedDate + "]");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Seat Grid Panel
        JPanel seatPanel = new JPanel(new GridLayout(ROWS, LAST_COL - 'A' + 1, 5, 5));
        
        // Fetch seats booked ONLY on this specific date
        Set<String> bookedSeats = bookingDAO.getBookedSeats(flightId, flightDate);

        for (int i = 1; i <= ROWS; i++) {
            for (char c = 'A'; c <= LAST_COL; c++) {
                String seatId = i + "" + c;
                JToggleButton seatButton = new JToggleButton(seatId);
                seatButton.setFont(new Font("Monospaced", Font.BOLD, 14));

                // If seat is booked on this date, disable it and turn it RED
                if (bookedSeats.contains(seatId)) {
                    seatButton.setEnabled(false);
                    seatButton.setBackground(Color.RED);
                    seatButton.setText("X"); // Mark as taken
                } else {
                    seatButton.setBackground(new Color(144, 238, 144)); // Light Green for available
                }
                
                seatButtons.add(seatButton);
                seatPanel.add(seatButton);
            }
        }
        mainPanel.add(seatPanel, BorderLayout.CENTER);

        // Legend Panel
        JPanel legendPanel = new JPanel();
        JLabel availableLabel = new JLabel(" Green = Available ");
        availableLabel.setOpaque(true);
        availableLabel.setBackground(new Color(144, 238, 144));
        JLabel bookedLabel = new JLabel(" Red = Booked ");
        bookedLabel.setOpaque(true);
        bookedLabel.setBackground(Color.RED);
        bookedLabel.setForeground(Color.WHITE);
        legendPanel.add(availableLabel);
        legendPanel.add(bookedLabel);
        mainPanel.add(legendPanel, BorderLayout.NORTH);

        // Bottom Panel with Button
        JPanel bottomPanel = new JPanel();
        JButton confirmButton = new JButton("Proceed to Payment");
        confirmButton.setPreferredSize(new Dimension(200, 40));
        confirmButton.setBackground(new Color(30, 144, 255));
        confirmButton.setForeground(Color.WHITE);

        confirmButton.addActionListener(e -> {
            List<String> selectedSeats = new ArrayList<>();
            for (JToggleButton button : seatButtons) {
                if (button.isSelected() && button.isEnabled()) {
                    selectedSeats.add(button.getText());
                }
            }

            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one seat.", "No Seats Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get base price to calculate total
            double basePrice = flightDAO.getBasePrice(this.flightId);
            if (basePrice < 0) {
                JOptionPane.showMessageDialog(this, "Error fetching flight price.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate Price Breakdown String
            StringBuilder breakdown = new StringBuilder("--- Price Breakdown ---\n");
            double totalAmount = 0;
            for (String seatId : selectedSeats) {
                int row = Integer.parseInt(seatId.replaceAll("[^0-9]", ""));
                double multiplier = (row <= 3) ? 1.5 : (row <= 7) ? 1.2 : 1.0;
                double seatPrice = basePrice * multiplier;
                totalAmount += seatPrice;
                breakdown.append(String.format("Seat %s: ₹%.2f (x%.1f)\n", seatId, seatPrice, multiplier));
            }
            breakdown.append("------------------------\n");
            breakdown.append(String.format("Total Amount: ₹%.2f", totalAmount));

            // Open Payment Window with the calculated data
            new PaymentView(this.userId, this.flightId, this.flightDate, selectedSeats, breakdown.toString());
            dispose(); // Close seat map
        });

        bottomPanel.add(confirmButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}