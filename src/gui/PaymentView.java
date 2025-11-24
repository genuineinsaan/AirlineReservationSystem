package gui;

import dao.BookingDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Date;

public class PaymentView extends JFrame {

    private int userId;
    private int flightId;
    private Date flightDate; // The specific date of the flight
    private List<String> selectedSeats;
    private BookingDAO bookingDAO;

    public PaymentView(int userId, int flightId, Date flightDate, List<String> selectedSeats, String priceBreakdown) {
        this.userId = userId;
        this.flightId = flightId;
        this.flightDate = flightDate;
        this.selectedSeats = selectedSeats;
        this.bookingDAO = new BookingDAO();

        setTitle("Confirm Payment");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Payment Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Price Breakdown Area
        JTextArea breakdownArea = new JTextArea(priceBreakdown);
        breakdownArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        breakdownArea.setEditable(false);
        breakdownArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane scrollPane = new JScrollPane(breakdownArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Payment Options Panel
        JPanel paymentOptionsPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        paymentOptionsPanel.setBorder(BorderFactory.createTitledBorder("Select Payment Mode"));
        paymentOptionsPanel.setBackground(Color.WHITE);

        JRadioButton creditCardRadio = new JRadioButton("Credit Card");
        creditCardRadio.setBackground(Color.WHITE);
        creditCardRadio.setSelected(true);
        
        JRadioButton debitCardRadio = new JRadioButton("Debit Card");
        debitCardRadio.setBackground(Color.WHITE);
        
        JRadioButton upiRadio = new JRadioButton("UPI / Net Banking");
        upiRadio.setBackground(Color.WHITE);

        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(creditCardRadio);
        paymentGroup.add(debitCardRadio);
        paymentGroup.add(upiRadio);

        paymentOptionsPanel.add(creditCardRadio);
        paymentOptionsPanel.add(debitCardRadio);
        paymentOptionsPanel.add(upiRadio);
        
        // Pay Button
        JButton payButton = new JButton("Pay Now & Book");
        payButton.setBackground(new Color(34, 139, 34)); // Forest Green
        payButton.setForeground(Color.WHITE);
        payButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        payButton.addActionListener(e -> {
            // 1. Call the DAO to save the transaction and seats
            String result = bookingDAO.bookMultipleSeats(this.userId, this.flightId, this.flightDate, this.selectedSeats);

            // 2. Check result and show the Teacher's specific message
            if (result.startsWith("✅")) {
                // Success!
                JOptionPane.showMessageDialog(this,
                    "Your ticket has been sent to your Email - Thank You.\n\n" + result,
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Failure (e.g., seat taken by someone else just now)
                JOptionPane.showMessageDialog(this,
                    result,
                    "Booking Failed",
                    JOptionPane.ERROR_MESSAGE);
            }

            // 3. Close window
            dispose();
        });
        
        paymentOptionsPanel.add(new JLabel()); // Spacer
        paymentOptionsPanel.add(payButton);

        panel.add(paymentOptionsPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}