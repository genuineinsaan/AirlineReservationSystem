package gui;

import dao.FlightDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddFlightView extends JFrame {

    private JPanel panel;
    private JLabel titleLabel, companyLabel, flightNoLabel, sourceLabel, destinationLabel, daysLabel, depTimeLabel, arrTimeLabel, priceLabel;
    private JTextField companyText, flightNoText, sourceText, destinationText, depTimeText, arrTimeText, priceText;
    private JCheckBox monCheck, tueCheck, wedCheck, thuCheck, friCheck, satCheck, sunCheck;
    private JButton submitButton;
    private FlightDAO flightDAO;

    public AddFlightView() {
        flightDAO = new FlightDAO();

        setTitle("Add New Flight Schedule");
        setSize(450, 520); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        add(panel);

        titleLabel = new JLabel("Add Flight Schedule");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(130, 15, 250, 30);
        panel.add(titleLabel);

        int y = 60;
        int labelX = 30;
        int fieldX = 160;
        int fieldW = 240;
        int h = 25;
        int gap = 35;

        // Company
        companyLabel = new JLabel("Flight Company:");
        companyLabel.setBounds(labelX, y, 120, h); panel.add(companyLabel);
        companyText = new JTextField();
        companyText.setBounds(fieldX, y, fieldW, h); panel.add(companyText);
        y += gap;

        // Flight No
        flightNoLabel = new JLabel("Flight No:");
        flightNoLabel.setBounds(labelX, y, 120, h); panel.add(flightNoLabel);
        flightNoText = new JTextField();
        flightNoText.setBounds(fieldX, y, fieldW, h); panel.add(flightNoText);
        y += gap;

        // Source
        sourceLabel = new JLabel("Source:");
        sourceLabel.setBounds(labelX, y, 120, h); panel.add(sourceLabel);
        sourceText = new JTextField();
        sourceText.setBounds(fieldX, y, fieldW, h); panel.add(sourceText);
        y += gap;

        // Destination
        destinationLabel = new JLabel("Destination:");
        destinationLabel.setBounds(labelX, y, 120, h); panel.add(destinationLabel);
        destinationText = new JTextField();
        destinationText.setBounds(fieldX, y, fieldW, h); panel.add(destinationText);
        y += gap;

        // Days of Week
        daysLabel = new JLabel("Operates On:");
        daysLabel.setBounds(labelX, y, 120, h); panel.add(daysLabel);
        
        JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        daysPanel.setBackground(Color.WHITE);
        daysPanel.setBounds(fieldX - 5, y, 260, 30);
        
        monCheck = new JCheckBox("M"); monCheck.setBackground(Color.WHITE);
        tueCheck = new JCheckBox("T"); tueCheck.setBackground(Color.WHITE);
        wedCheck = new JCheckBox("W"); wedCheck.setBackground(Color.WHITE);
        thuCheck = new JCheckBox("Th"); thuCheck.setBackground(Color.WHITE);
        friCheck = new JCheckBox("F"); friCheck.setBackground(Color.WHITE);
        satCheck = new JCheckBox("Sa"); satCheck.setBackground(Color.WHITE);
        sunCheck = new JCheckBox("Su"); sunCheck.setBackground(Color.WHITE);
        
        daysPanel.add(monCheck); daysPanel.add(tueCheck); daysPanel.add(wedCheck); 
        daysPanel.add(thuCheck); daysPanel.add(friCheck); daysPanel.add(satCheck); daysPanel.add(sunCheck);
        panel.add(daysPanel);
        y += gap;

        // Departure Time
        depTimeLabel = new JLabel("Dep Time (HH:MM:SS):");
        depTimeLabel.setBounds(labelX, y, 140, h); panel.add(depTimeLabel);
        depTimeText = new JTextField();
        depTimeText.setBounds(fieldX + 10, y, fieldW - 10, h); panel.add(depTimeText);
        y += gap;

        // Arrival Time
        arrTimeLabel = new JLabel("Arr Time (HH:MM:SS):");
        arrTimeLabel.setBounds(labelX, y, 140, h); panel.add(arrTimeLabel);
        arrTimeText = new JTextField();
        arrTimeText.setBounds(fieldX + 10, y, fieldW - 10, h); panel.add(arrTimeText);
        y += gap;

        // Base Price
        priceLabel = new JLabel("Base Price (₹):");
        priceLabel.setBounds(labelX, y, 120, h); panel.add(priceLabel);
        priceText = new JTextField();
        priceText.setBounds(fieldX, y, fieldW, h); panel.add(priceText);
        y += 50;

        // Submit Button
        submitButton = new JButton("Save Schedule");
        submitButton.setBounds(130, y, 180, 35);
        submitButton.setBackground(new Color(0, 128, 128));
        submitButton.setForeground(Color.WHITE);
        panel.add(submitButton);

        submitButton.addActionListener(e -> addFlightSchedule());

        setVisible(true);
    }

    private void addFlightSchedule() {
        String company = companyText.getText().trim();
        String flightNo = flightNoText.getText().trim();
        String source = sourceText.getText().trim();
        String destination = destinationText.getText().trim();
        String depTimeStr = depTimeText.getText().trim();
        String arrTimeStr = arrTimeText.getText().trim();
        String priceStr = priceText.getText().trim();

        boolean[] operatesOn = {
            monCheck.isSelected(), tueCheck.isSelected(), wedCheck.isSelected(),
            thuCheck.isSelected(), friCheck.isSelected(), satCheck.isSelected(), sunCheck.isSelected()
        };

        // 1. Basic Validation
        if (company.isEmpty() || flightNo.isEmpty() || source.isEmpty() || destination.isEmpty() ||
            depTimeStr.isEmpty() || arrTimeStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "All fields must be filled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Check if at least one day is selected
        boolean daySelected = false;
        for (boolean day : operatesOn) {
            if (day) { daySelected = true; break; }
        }
        if (!daySelected) {
            JOptionPane.showMessageDialog(panel, "Please select at least one day of operation.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Time and Price Validation
        Time depTime, arrTime;
        double price;
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setLenient(false);

        try {
            depTime = new Time(timeFormat.parse(depTimeStr).getTime());
            arrTime = new Time(timeFormat.parse(arrTimeStr).getTime());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(panel, "Invalid time format. Use HH:MM:SS (e.g., 14:30:00).", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!arrTime.after(depTime)) {
            JOptionPane.showMessageDialog(panel, "Arrival time must be after departure time.", "Logic Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "Price must be a positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Call DAO
        boolean success = flightDAO.addFlightSchedule(company, flightNo, source, destination, depTime, arrTime, price, operatesOn);

        if (success) {
            JOptionPane.showMessageDialog(panel, "Flight Schedule Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            if (flightDAO.isFlightNumberExists(flightNo)) {
                JOptionPane.showMessageDialog(panel, "Flight Number '" + flightNo + "' already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel, "Database Error. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}