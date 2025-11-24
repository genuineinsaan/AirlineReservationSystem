package gui;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;

public class CompleteProfileView extends JFrame {

    private JPanel panel;
    private JLabel titleLabel, dobLabel, genderLabel, addressLabel, zipLabel, countryLabel;
    private JTextField dobText, addressText, zipText;
    private JRadioButton maleRadio, femaleRadio, otherRadio;
    private ButtonGroup genderGroup;
    private JComboBox<String> countryBox;
    private JButton saveButton;
    
    private UserDAO userDAO;
    private int userId;

    public CompleteProfileView(int userId) {
        this.userId = userId;
        userDAO = new UserDAO();

        setTitle("Complete Your Profile");
        setSize(450, 450);
        // This window acts as a dialog; closing it shouldn't close the whole app,
        // but usually we want them to finish this.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true); // Keep it on top

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 255, 240)); // HoneyDew background
        add(panel);

        titleLabel = new JLabel("Please complete your profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBounds(100, 20, 300, 30);
        panel.add(titleLabel);

        int y = 70;
        int labelX = 30;
        int fieldX = 150;
        int fieldW = 200;
        int gap = 40;

        // Date of Birth
        dobLabel = new JLabel("DOB (YYYY-MM-DD):");
        dobLabel.setBounds(labelX, y, 120, 25);
        panel.add(dobLabel);
        dobText = new JTextField();
        dobText.setBounds(fieldX, y, fieldW, 25);
        panel.add(dobText);
        y += gap;

        // Gender (Radio Buttons)
        genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(labelX, y, 100, 25);
        panel.add(genderLabel);
        
        maleRadio = new JRadioButton("Male");
        maleRadio.setBounds(fieldX, y, 60, 25);
        maleRadio.setBackground(new Color(240, 255, 240));
        
        femaleRadio = new JRadioButton("Female");
        femaleRadio.setBounds(fieldX + 60, y, 70, 25);
        femaleRadio.setBackground(new Color(240, 255, 240));
        
        otherRadio = new JRadioButton("Other");
        otherRadio.setBounds(fieldX + 130, y, 60, 25);
        otherRadio.setBackground(new Color(240, 255, 240));

        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        maleRadio.setSelected(true);

        panel.add(maleRadio);
        panel.add(femaleRadio);
        panel.add(otherRadio);
        y += gap;

        // Address
        addressLabel = new JLabel("Address:");
        addressLabel.setBounds(labelX, y, 100, 25);
        panel.add(addressLabel);
        addressText = new JTextField();
        addressText.setBounds(fieldX, y, fieldW, 25);
        panel.add(addressText);
        y += gap;
        
        // Zip Code (New Requirement)
        zipLabel = new JLabel("Zip Code:");
        zipLabel.setBounds(labelX, y, 100, 25);
        panel.add(zipLabel);
        zipText = new JTextField();
        zipText.setBounds(fieldX, y, fieldW, 25);
        panel.add(zipText);
        y += gap;

        // Country (Dropdown)
        countryLabel = new JLabel("Country:");
        countryLabel.setBounds(labelX, y, 100, 25);
        panel.add(countryLabel);
        
        String[] countries = {"India", "USA", "UK", "Canada", "Australia", "Germany", "France", "Japan", "Other"};
        countryBox = new JComboBox<>(countries);
        countryBox.setBounds(fieldX, y, fieldW, 25);
        panel.add(countryBox);
        y += 60;

        // Save Button
        saveButton = new JButton("Save & Continue");
        saveButton.setBounds(120, y, 180, 35);
        saveButton.setBackground(new Color(0, 128, 128)); // Teal
        saveButton.setForeground(Color.WHITE);
        
        saveButton.addActionListener(e -> saveDetails());
        panel.add(saveButton);

        setVisible(true);
    }

    private void saveDetails() {
        String dob = dobText.getText().trim();
        String address = addressText.getText().trim();
        String zip = zipText.getText().trim();
        String country = (String) countryBox.getSelectedItem();
        
        String gender = "Male";
        if (femaleRadio.isSelected()) gender = "Female";
        if (otherRadio.isSelected()) gender = "Other";

        if (dob.isEmpty() || address.isEmpty() || zip.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save to DB
        boolean success = userDAO.completeProfile(userId, dob, gender, address, zip, country);

        if (success) {
            JOptionPane.showMessageDialog(panel, "Profile Completed! Welcome to your dashboard.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Open User Dashboard
            new UserDashboardView(userId);
            
            // Close this popup
            dispose();
        } else {
            JOptionPane.showMessageDialog(panel, "Error saving profile. Check Date format (YYYY-MM-DD).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}