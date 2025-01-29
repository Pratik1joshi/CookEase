package org.example.view;

import org.example.model.User;
import org.example.model.dao.UserDAO;
// import org.example.view.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class SignupFrame extends JFrame implements ActionListener {
    private final Color THEME_COLOR = new Color(133, 72, 71);
    private final Color BUTTON_HOVER_COLOR = new Color(153, 92, 91);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color INPUT_BG_COLOR = new Color(245, 245, 245);
    
    private final JTextField usernameField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JComboBox<String> accessLevelBox;
    private final JButton signupButton, backButton;
    private final UserDAO userDAO;

    public SignupFrame() throws SQLException {
        userDAO = new UserDAO();
        
        setTitle("Signup - CookEase");
        setSize(800, 500);
        setLayout(new BorderLayout());
        
        // Create main panels
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(THEME_COLOR);
        leftPanel.setPreferredSize(new Dimension(300, 0));
        
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        
        // Add welcome image and text to left panel with error handling
        JLabel welcomeImage = new JLabel();
        java.net.URL imgURL = getClass().getResource("/images/icons/cooking_signup.png");
        if (imgURL != null) {
            welcomeImage.setIcon(new ImageIcon(imgURL));
        } else {
            // Fallback text if image not found
            JLabel fallbackText = new JLabel("Welcome");
            fallbackText.setForeground(Color.WHITE);
            fallbackText.setFont(new Font("Montserrat", Font.BOLD, 32));
            leftPanel.add(fallbackText);
        }
        
        JLabel welcomeText = new JLabel("Welcome to CookEase");
        welcomeText.setForeground(Color.WHITE);
        welcomeText.setFont(new Font("Montserrat", Font.BOLD, 24));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        if (imgURL != null) {
            leftPanel.add(welcomeImage, gbc);
        }
        
        gbc.gridy = 1;
        leftPanel.add(welcomeText, gbc);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        // Add form fields
        addFormField(formPanel, "Username", usernameField, 0);
        addFormField(formPanel, "Full Name", fullNameField, 1);
        addFormField(formPanel, "Password", passwordField, 2);
        addFormField(formPanel, "Email", emailField, 3);
        
        // Add access level combo box
        JLabel accessLabel = new JLabel("Access Level:");
        accessLevelBox = new JComboBox<>(new String[]{"User", "Admin"});
        styleComboBox(accessLevelBox);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 10);
        formPanel.add(accessLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(accessLevelBox, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        signupButton = new JButton("Sign Up");
        backButton = new JButton("Back to Login");
        
        styleButton(signupButton);
        styleButton(backButton);
        
        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(buttonPanel, gbc);

        // Add panels to frame
        add(leftPanel, BorderLayout.WEST);
        rightPanel.add(formPanel);
        add(rightPanel, BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, int row) {
        JLabel label = new JLabel(labelText + ":");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 10);
        panel.add(label, gbc);

        if (field instanceof JTextField) {
            styleTextField((JTextField)field);
        }
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(200, 30));
        field.setBackground(INPUT_BG_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(THEME_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setPreferredSize(new Dimension(200, 30));
        box.setBackground(INPUT_BG_COLOR);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void styleButton(JButton button) {
        button.setBackground(THEME_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(THEME_COLOR);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            try {
                new LoginFrame();
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error opening login screen!", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            return;
        }

        // Only runs if it's the signup button
        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();
        String accessLevel = (String) accessLevelBox.getSelectedItem();

        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User newUser = new User(username, fullName, password, email, accessLevel);
            userDAO.createUser(newUser);
            JOptionPane.showMessageDialog(this, "Signup successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame();
            dispose();
        } catch (SQLException sqlException) {
            JOptionPane.showMessageDialog(this, "Error registering user!", "Error", JOptionPane.ERROR_MESSAGE);
            sqlException.printStackTrace();
        }
    }
}

