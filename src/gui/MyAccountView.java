package gui;

import dao.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MyAccountView extends JFrame {

    private JPanel panel;
    private JLabel titleLabel, firstNameLabel, lastNameLabel, emailLabel, phoneLabel, dobLabel, genderLabel, addressLabel, zipCodeLabel, countryLabel;
    private JTextField firstNameText, lastNameText, emailText, phoneText, dobText, genderText, addressText, zipCodeText, countryText;
    private JLabel sectionLabel, currentPassLabel, newPassLabel, confirmPassLabel;
    private JPasswordField currentPassText, newPassText, confirmPassText;
    private JButton updatePasswordButton;

    private UserDAO userDAO;
    private int userId;

    public MyAccountView(int userId) {
        this.userId = userId;
        this.userDAO = new UserDAO();

        setTitle("My Account");
        setSize(500, 650); // Taller window to fit all fields
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        add(panel);

        titleLabel = new JLabel("Your Profile Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(140, 15, 250, 30);
        panel.add(titleLabel);

        int y = 60;
        int labelX = 30;
        int fieldX = 150;
        int fieldW = 280;
        int h = 25;
        int gap = 35;

        // --- READ-ONLY PROFILE FIELDS ---
        firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setBounds(labelX, y, 100, h); panel.add(firstNameLabel);
        firstNameText = createReadOnlyField();
        firstNameText.setBounds(fieldX, y, fieldW, h); panel.add(firstNameText);
        y += gap;

        lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setBounds(labelX, y, 100, h); panel.add(lastNameLabel);
        lastNameText = createReadOnlyField();
        lastNameText.setBounds(fieldX, y, fieldW, h); panel.add(lastNameText);
        y += gap;

        emailLabel = new JLabel("Email:");
        emailLabel.setBounds(labelX, y, 100, h); panel.add(emailLabel);
        emailText = createReadOnlyField();
        emailText.setBounds(fieldX, y, fieldW, h); panel.add(emailText);
        y += gap;

        phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(labelX, y, 100, h); panel.add(phoneLabel);
        phoneText = createReadOnlyField();
        phoneText.setBounds(fieldX, y, fieldW, h); panel.add(phoneText);
        y += gap;

        dobLabel = new JLabel("Date of Birth:");
        dobLabel.setBounds(labelX, y, 100, h); panel.add(dobLabel);
        dobText = createReadOnlyField();
        dobText.setBounds(fieldX, y, fieldW, h); panel.add(dobText);
        y += gap;

        genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(labelX, y, 100, h); panel.add(genderLabel);
        genderText = createReadOnlyField();
        genderText.setBounds(fieldX, y, fieldW, h); panel.add(genderText);
        y += gap;

        addressLabel = new JLabel("Address:");
        addressLabel.setBounds(labelX, y, 100, h); panel.add(addressLabel);
        addressText = createReadOnlyField();
        addressText.setBounds(fieldX, y, fieldW, h); panel.add(addressText);
        y += gap;

        zipCodeLabel = new JLabel("Zip Code:");
        zipCodeLabel.setBounds(labelX, y, 100, h); panel.add(zipCodeLabel);
        zipCodeText = createReadOnlyField();
        zipCodeText.setBounds(fieldX, y, fieldW, h); panel.add(zipCodeText);
        y += gap;

        countryLabel = new JLabel("Country:");
        countryLabel.setBounds(labelX, y, 100, h); panel.add(countryLabel);
        countryText = createReadOnlyField();
        countryText.setBounds(fieldX, y, fieldW, h); panel.add(countryText);
        y += 45; // Extra gap

        // --- CHANGE PASSWORD SECTION ---
        JSeparator sep = new JSeparator();
        sep.setBounds(30, y - 10, 400, 10);
        panel.add(sep);

        sectionLabel = new JLabel("Change Password");
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sectionLabel.setBounds(30, y - 5, 200, 25);
        panel.add(sectionLabel);
        y += 30;

        currentPassLabel = new JLabel("Current Password:");
        currentPassLabel.setBounds(30, y, 120, h); panel.add(currentPassLabel);
        currentPassText = new JPasswordField();
        currentPassText.setBounds(160, y, 270, h); panel.add(currentPassText);
        y += gap;

        newPassLabel = new JLabel("New Password:");
        newPassLabel.setBounds(30, y, 120, h); panel.add(newPassLabel);
        newPassText = new JPasswordField();
        newPassText.setBounds(160, y, 270, h); panel.add(newPassText);
        y += gap;

        confirmPassLabel = new JLabel("Confirm New:");
        confirmPassLabel.setBounds(30, y, 120, h); panel.add(confirmPassLabel);
        confirmPassText = new JPasswordField();
        confirmPassText.setBounds(160, y, 270, h); panel.add(confirmPassText);
        y += 45;

        updatePasswordButton = new JButton("Update Password");
        updatePasswordButton.setBounds(150, y, 180, 30);
        updatePasswordButton.setBackground(new Color(255, 140, 0)); // Dark Orange
        updatePasswordButton.setForeground(Color.WHITE);
        panel.add(updatePasswordButton);

        // Listeners
        updatePasswordButton.addActionListener(e -> changePassword());
        
        // Load Data
        loadUserDetails();

        setVisible(true);
    }

    private JTextField createReadOnlyField() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setBackground(new Color(245, 245, 245)); // Light gray to show it's read-only
        return tf;
    }

    private void loadUserDetails() {
        Map<String, String> details = userDAO.getUserDetails(this.userId);
        if (!details.isEmpty()) {
            firstNameText.setText(details.getOrDefault("firstName", ""));
            lastNameText.setText(details.getOrDefault("lastName", ""));
            emailText.setText(details.getOrDefault("email", ""));
            phoneText.setText(details.getOrDefault("phone", ""));
            dobText.setText(details.getOrDefault("dob", ""));
            genderText.setText(details.getOrDefault("gender", ""));
            addressText.setText(details.getOrDefault("address", ""));
            zipCodeText.setText(details.getOrDefault("zipCode", ""));
            countryText.setText(details.getOrDefault("country", ""));
        }
    }

    private void changePassword() {
        String currentPass = new String(currentPassText.getPassword());
        String newPass = new String(newPassText.getPassword());
        String confirmPass = new String(confirmPassText.getPassword());

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Please fill all password fields.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(panel, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = userDAO.updatePassword(this.userId, currentPass, newPass);

        if (success) {
            JOptionPane.showMessageDialog(panel, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            currentPassText.setText("");
            newPassText.setText("");
            confirmPassText.setText("");
        } else {
            JOptionPane.showMessageDialog(panel, "Failed. Check your current password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}