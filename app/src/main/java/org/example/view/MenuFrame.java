package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import org.example.model.User;
import org.example.model.dao.UserDAO;
import org.example.model.dao.ProductDAO;
import org.example.model.Cart;  // Add this import

public class MenuFrame extends JFrame implements ActionListener {
    private final Color THEME_COLOR = new Color(153, 92, 91); // Slightly lighter
    private final Color BUTTON_HOVER_COLOR = new Color(173, 112, 111);
    private final Color TEXT_COLOR = Color.WHITE;
    private JPanel mainPanel, productPanel;
    private JButton newSaleButton, searchSaleButton, settingsButton, logoutButton;
    private JButton manageUsersButton, manageProductsButton;
    private JScrollPane scrollPane;
    
    private final Dimension buttonsDimension = new Dimension(200, 25);
    private final Color mainPanelColor = Color.white, buttonColor = Color.black;
    private String loggedUserName = "Unknown user", loggedUserAccessLevel = "User";
    private final User currentlyLoggedUser;
    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final File settingsFile;
    private FileReader fileReader;
    private final HashMap<String, String> settingsMap = new HashMap<>();

    public MenuFrame() throws SQLException, IOException {
        userDAO = new UserDAO();
        productDAO = new ProductDAO();
        settingsFile = new File("app_local_settings.txt");

        if (settingsFile.exists() && settingsFile.isFile()) {
            fileReader = new FileReader(settingsFile);
            StringBuilder fileData = new StringBuilder();
            int data;
            while ((data = fileReader.read()) != -1) {
                fileData.append((char) data);
            }
            fileReader.close();
            parseSettings(fileData.toString());
        }

        currentlyLoggedUser = userDAO.readUser(settingsMap.get("currentlyLoggedUser"));
        if (currentlyLoggedUser != null) {
            loggedUserName = currentlyLoggedUser.getFullName();
            loggedUserAccessLevel = currentlyLoggedUser.getAccessLevel();
            Cart.getInstance().setUserId(currentlyLoggedUser.getUsername());
        }

        setupUI();
    }

    private void parseSettings(String fileData) {
        String[] settingsStrings = fileData.split(",");
        for (String part : settingsStrings) {
            String[] partArray = part.split(":");
            settingsMap.put(partArray[0].trim(), partArray[1].trim());
        }
    }

    private void setupUI() throws SQLException {
        setTitle("Menu");
        setSize(1000, 720);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));
        mainPanel.setPreferredSize(new Dimension(250, 0));
        mainPanel.setBackground(THEME_COLOR);
        add(mainPanel, BorderLayout.WEST);

        productPanel = new JPanel();
        productPanel.setBackground(Color.WHITE);
        scrollPane = new JScrollPane(productPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        System.out.println("Image file exists: " + new File("images/icons/username_icon.png").exists());

        // Create user profile panel
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(THEME_COLOR);
        
        // Load profile icon from resources
        java.net.URL imgURL = getClass().getResource("/images/icons/profile_user.png");
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel profileIcon = new JLabel(new ImageIcon(img));
            profileIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            profilePanel.add(profileIcon);
        }

        // Add username label
        JLabel nameLabel = new JLabel(loggedUserName);
        nameLabel.setFont(new Font("Montserrat", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.add(Box.createVerticalStrut(5));
        profilePanel.add(nameLabel);

        mainPanel.add(profilePanel);

        addMenuOptions();
        createProductGrid();

        setVisible(true);
    }

    private void createProductGrid() throws SQLException {
        String[][] products = productDAO.readProductsTableData();
        
        // Calculate grid layout dimensions
        int columns = 3; // Number of products per row
        int rows = (int) Math.ceil(products.length / (double)columns);
        
        productPanel.setLayout(new GridLayout(rows, columns, 20, 20));
        productPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (String[] product : products) {
            JPanel productCard = createProductCard(product);
            productPanel.add(productCard);
        }
    }

    private JPanel createProductCard(String[] product) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);

        // Create image label
        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Load image from resources
        java.net.URL imgURL = getClass().getResource("/" + product[4]);
        if (imgURL != null) {
            System.out.println("Resource URL: " + imgURL);
            ImageIcon imageIcon = new ImageIcon(imgURL);
            Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        } else {
            System.err.println("Couldn't find resource: " + product[4]);
        }

        // Product Details
        JLabel nameLabel = new JLabel(product[1]); // Name
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel categoryLabel = new JLabel(product[2]); // Category
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel priceLabel = new JLabel("RS" + product[3]); // Price
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 100, 0));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add to Cart button - only for non-admin users
        if (!loggedUserAccessLevel.equals("Admin")) {
            JButton addToCartButton = new JButton("Add to Cart");
            addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            styleButton(addToCartButton);
            addToCartButton.addActionListener(e -> {
                Cart.getInstance().addItem(product);
                JOptionPane.showMessageDialog(this, "Added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
            });
            card.add(Box.createVerticalStrut(5));
            card.add(addToCartButton);
        }

        card.add(Box.createVerticalStrut(10));

        // Add components to card
        card.add(Box.createVerticalStrut(10));
        card.add(imageLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(categoryLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(priceLabel);
        card.add(Box.createVerticalStrut(10));

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2));
            }

            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        });

        return card;
    }

    private void styleButton(JButton button) {
        button.setBackground(THEME_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(THEME_COLOR);
            }
        });
    }

    private void addMenuOptions() {
        // Only show Cart button for non-admin users
        if (!loggedUserAccessLevel.equals("Admin")) {
            newSaleButton = createButton("Cart", "images/icons/shopping_cart_icon.png");
            styleButton(newSaleButton);
            mainPanel.add(newSaleButton);
        }

        // Admin-only buttons
        if (loggedUserAccessLevel.equals("Admin")) {
            searchSaleButton = createButton("Sales", "images/icons/search_icon.png");
            manageUsersButton = createButton("Manage Users", "images/icons/user_icon.png");
            manageProductsButton = createButton("Manage Products", "images/icons/product_icon.png");
            
            styleButton(searchSaleButton);
            styleButton(manageUsersButton);
            styleButton(manageProductsButton);
            
            mainPanel.add(searchSaleButton);
            mainPanel.add(manageUsersButton);
            mainPanel.add(manageProductsButton);
        }

        settingsButton = createButton("Settings", "images/icons/settings_icon.png");
        logoutButton = createButton("Logout", "images/icons/logout_icon.png");
        
        styleButton(settingsButton);
        styleButton(logoutButton);
        
        mainPanel.add(settingsButton);
        mainPanel.add(logoutButton);
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        java.net.URL imgURL = getClass().getResource("/" + iconPath);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } else {
            System.err.println("Couldn't find icon: " + iconPath);
        }
        button.setPreferredSize(buttonsDimension);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(buttonColor));
        button.setBackground(mainPanelColor);
        button.setForeground(buttonColor);
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            if (event.getSource() == newSaleButton && !loggedUserAccessLevel.equals("Admin")) {
                new CartFrame(currentlyLoggedUser.getUsername());
                dispose();
            } else if (event.getSource() == searchSaleButton) {
                new SearchSaleFrame();
                dispose();
            } else if (event.getSource() == manageUsersButton) {
                new ManageUsersFrame();
                dispose();
            } else if (event.getSource() == manageProductsButton) {
                new ManageProductsFrame();
                dispose();
            } else if (event.getSource() == logoutButton) {
                int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    new LoginFrame();
                    dispose();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}