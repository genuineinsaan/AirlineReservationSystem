package gui;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;

public class RegisterView extends JFrame {

    private JPanel panel;
    private JLabel titleLabel, fNameLabel, lNameLabel, emailLabel, phoneLabel, passLabel, confPassLabel;
    private JTextField fNameText, lNameText, emailText, phoneText;
    private JPasswordField passText, confPassText;
    private JCheckBox termsCheck;
    private JButton registerButton;
    private UserDAO userDAO;

    public RegisterView() {
        userDAO = new UserDAO();

        setTitle("Sign Up - Airline System");
        setSize(450, 500); // Taller for more fields
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window
        setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        add(panel);

        // Title
        titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(110, 20, 250, 30);
        panel.add(titleLabel);

        int y = 70;
        int labelX = 30;
        int fieldX = 160;
        int fieldW = 230;
        int h = 25;
        int gap = 40;

        // First Name
        fNameLabel = new JLabel("First Name:");
        fNameLabel.setBounds(labelX, y, 100, h);
        panel.add(fNameLabel);
        fNameText = new JTextField();
        fNameText.setBounds(fieldX, y, fieldW, h);
        panel.add(fNameText);
        y += gap;

        // Last Name
        lNameLabel = new JLabel("Last Name:");
        lNameLabel.setBounds(labelX, y, 100, h);
        panel.add(lNameLabel);
        lNameText = new JTextField();
        lNameText.setBounds(fieldX, y, fieldW, h);
        panel.add(lNameText);
        y += gap;

        // Email
        emailLabel = new JLabel("Email ID:");
        emailLabel.setBounds(labelX, y, 100, h);
        panel.add(emailLabel);
        emailText = new JTextField();
        emailText.setBounds(fieldX, y, fieldW, h);
        panel.add(emailText);
        y += gap;

        // Phone
        phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(labelX, y, 100, h);
        panel.add(phoneLabel);
        phoneText = new JTextField();
        phoneText.setBounds(fieldX, y, fieldW, h);
        panel.add(phoneText);
        y += gap;

        // Password
        passLabel = new JLabel("Password:");
        passLabel.setBounds(labelX, y, 100, h);
        panel.add(passLabel);
        passText = new JPasswordField();
        passText.setBounds(fieldX, y, fieldW, h);
        panel.add(passText);
        y += gap;

        // Confirm Password
        confPassLabel = new JLabel("Confirm Pass:");
        confPassLabel.setBounds(labelX, y, 100, h);
        panel.add(confPassLabel);
        confPassText = new JPasswordField();
        confPassText.setBounds(fieldX, y, fieldW, h);
        panel.add(confPassText);
        y += gap;

        // Terms Checkbox
        termsCheck = new JCheckBox("I agree to the Terms and Conditions");
        termsCheck.setBounds(fieldX, y, 250, h);
        termsCheck.setBackground(Color.WHITE);
        panel.add(termsCheck);
        y += gap;

        // Register Button
        registerButton = new JButton("Register & Login");
        registerButton.setBounds(130, y, 180, 35);
        registerButton.setBackground(new Color(34, 139, 34)); // Forest Green
        registerButton.setForeground(Color.WHITE);
        
        registerButton.addActionListener(e -> performRegistration());
        panel.add(registerButton);

        setVisible(true);
    }

    private void performRegistration() {
        String fName = fNameText.getText().trim();
        String lName = lNameText.getText().trim();
        String email = emailText.getText().trim();
        String phone = phoneText.getText().trim();
        String pass = new String(passText.getPassword());
        String confPass = new String(confPassText.getPassword());

        // Validation
        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!pass.equals(confPass)) {
            JOptionPane.showMessageDialog(panel, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!termsCheck.isSelected()) {
            JOptionPane.showMessageDialog(panel, "You must agree to the terms.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Save to DB
        boolean success = userDAO.register(fName, lName, email, phone, pass);

        if (success) {
            JOptionPane.showMessageDialog(panel, "Registration Successful! Logging you in...", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // --- AUTO LOGIN LOGIC ---
            int userId = userDAO.getUserIdByEmail(email);
            
            // Close Register Window
            dispose();
            
            // Close any open Login Window (we need to find it and close it if it's open)
            // For simplicity, we assume the user will just proceed forward.
            
            // Open Complete Profile Window immediately
            new CompleteProfileView(userId); 
        } else {
            JOptionPane.showMessageDialog(panel, "Registration Failed. Email might exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}