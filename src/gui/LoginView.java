package gui;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;

public class LoginView extends JFrame {

    private JPanel panel;
    private JLabel titleLabel, userLabel, passwordLabel, noAccountLabel;
    private JTextField userText;
    private JPasswordField passwordText;
    private JButton loginButton, registerButton, forgotPasswordButton;
    private UserDAO userDAO;

    public LoginView() {
        userDAO = new UserDAO();

        setTitle("Airline Reservation System V2.0");
        setSize(450, 350); // Increased size for new buttons
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 248, 255)); // Light Alice Blue background
        add(panel);

        // Title
        titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(25, 25, 112)); // Midnight Blue
        titleLabel.setBounds(140, 20, 200, 30);
        panel.add(titleLabel);

        // Email Field
        userLabel = new JLabel("Email:");
        userLabel.setBounds(50, 80, 80, 25);
        panel.add(userLabel);
        userText = new JTextField(20);
        userText.setBounds(140, 80, 220, 25);
        panel.add(userText);

        // Password Field
        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 120, 80, 25);
        panel.add(passwordLabel);
        passwordText = new JPasswordField();
        passwordText.setBounds(140, 120, 220, 25);
        panel.add(passwordText);
        
        // Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(140, 160, 100, 30);
        loginButton.setBackground(new Color(30, 144, 255)); // Dodger Blue
        loginButton.setForeground(Color.WHITE);
        
        loginButton.addActionListener(e -> performLogin());
        panel.add(loginButton);
        
        // Forgot Password Button
        forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setBounds(250, 165, 140, 20);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(Color.BLUE);
        forgotPasswordButton.setFont(new Font("Arial", Font.PLAIN, 10));
        
        forgotPasswordButton.addActionListener(e -> {
            String email = JOptionPane.showInputDialog(this, "Enter your registered email to reset password:");
            if(email != null && !email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password reset link sent to " + email + " (Simulated)", "Reset Password", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panel.add(forgotPasswordButton);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setBounds(40, 210, 360, 10);
        panel.add(sep);

        // Sign Up Label & Button
        noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setBounds(80, 230, 150, 25);
        panel.add(noAccountLabel);

        registerButton = new JButton("Sign Up");
        registerButton.setBounds(230, 230, 100, 25);
        registerButton.addActionListener(e -> {
            new RegisterView(); // Open Register V2
            // We don't dispose here so user can come back easily, or we can.
            // For now, keep open.
        });
        panel.add(registerButton);
        
        setVisible(true);
    }

    private void performLogin() {
        String email = userText.getText().trim();
        String password = new String(passwordText.getPassword());
        
        String firstName = userDAO.login(email, password);

        if (firstName != null) {
            // Login successful
            String role = userDAO.getUserRole(email);
            int userId = userDAO.getUserIdByEmail(email);

            if (role.equals("admin")) {
                new AdminDashboardView();
                dispose(); // Close login
            } else {
                // Check if profile is complete (V2 requirement)
                if (userDAO.isProfileComplete(userId)) {
                    new UserDashboardView(userId);
                    dispose();
                } else {
                    // Force profile completion
                    JOptionPane.showMessageDialog(panel, "Welcome " + firstName + "! Please complete your profile to continue.", "Action Required", JOptionPane.INFORMATION_MESSAGE);
                    new CompleteProfileView(userId);
                    dispose();
                }
            }
        } else {
            JOptionPane.showMessageDialog(panel, "Invalid Email or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Use the Event Dispatch Thread for Swing thread safety
        SwingUtilities.invokeLater(() -> new LoginView());
    }
}