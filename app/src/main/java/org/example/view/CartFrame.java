package org.example.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;  // Add this import
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.example.model.Cart;
import org.example.model.Sale;
import org.example.model.dao.ProductDAO;
import org.example.model.dao.SaleDAO;
import org.example.model.dao.UserDAO;

public class CartFrame extends JFrame implements ActionListener {
    private final JPanel bottomPanel, inputsPanel, tablesPanel;
    private final JLabel sellerUsernameLabel, totalCostLabel;
    private final JTextField totalCostTextField;
    private final JComboBox<String> sellerUsernameComboBox;
    private final JButton backButton, removeItemButton, checkoutButton;
    private final Dimension labelDimension = new Dimension(65, 20), 
            inputBoxDimension = new Dimension(180, 20),
            inputPanelDimension = new Dimension(265, 0),
            tableDimension = new Dimension(0, 600),
            buttonsDimension = new Dimension(100, 25);
    private final Color mainColor = Color.white, inputColor = Color.black;
    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    private final JScrollPane cartScrollPane;
    private String[][] cartData;
    private final String[] tableColumns = {"Image", "Name", "Category", "Price"}; // Changed columns
    private float totalCost = 0.0f;
    private final SaleDAO saleDAO;
    private final UserDAO userDAO;
    private final String currentUserId;

    public CartFrame(String userId) throws SQLException {
        this.currentUserId = userId;
        saleDAO = new SaleDAO();
        userDAO = new UserDAO();

        // Initialize cart table and model first
        Cart cart = Cart.getInstance();
        if (cart.getUserId().equals(currentUserId)) {
            List<String[]> items = cart.getItems();
            cartData = new String[items.size()][4];
            for (int i = 0; i < items.size(); i++) {
                String[] item = items.get(i);
                cartData[i][0] = item[4];  // Image path
                cartData[i][1] = item[1];  // Name
                cartData[i][2] = item[2];  // Category
                cartData[i][3] = item[3];  // Price
            }
            totalCost = cart.getTotalCost();
        } else {
            cartData = new String[0][0];
            totalCost = 0.0f;
        }

        cartTableModel = new DefaultTableModel(cartData, tableColumns);
        cartTable = new JTable(cartTableModel) {
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 0) ? ImageIcon.class : String.class;
            }
        };
        
        setupTableAppearance(); // Call this immediately after table creation

        setTitle("Shopping Cart");
        setSize(1000, 720);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Setup panels
        inputsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        inputsPanel.setPreferredSize(inputPanelDimension);
        inputsPanel.setBackground(mainColor);
        add(inputsPanel, BorderLayout.WEST);

        tablesPanel = new JPanel(new BorderLayout());
        tablesPanel.setBackground(mainColor);
        add(tablesPanel, BorderLayout.CENTER);

        // Setup input fields
        sellerUsernameLabel = new JLabel("Attendant");
        sellerUsernameLabel.setPreferredSize(labelDimension);
        sellerUsernameLabel.setFont(new Font("Calibri", Font.BOLD, 14));
        inputsPanel.add(sellerUsernameLabel);

        sellerUsernameComboBox = new JComboBox<>(userDAO.readAllAttendants());
        sellerUsernameComboBox.setPreferredSize(inputBoxDimension);
        sellerUsernameComboBox.setFocusable(false);
        inputsPanel.add(sellerUsernameComboBox);

        totalCostLabel = new JLabel("Total");
        totalCostLabel.setPreferredSize(labelDimension);
        totalCostLabel.setFont(new Font("Calibri", Font.BOLD, 14));
        inputsPanel.add(totalCostLabel);

        totalCostTextField = new JTextField();
        totalCostTextField.setPreferredSize(inputBoxDimension);
        totalCostTextField.setEditable(false);
        inputsPanel.add(totalCostTextField);

        // Setup buttons
        removeItemButton = new JButton("Remove");
        setButtonDesign(removeItemButton);
        inputsPanel.add(removeItemButton);

        checkoutButton = new JButton("Checkout");
        setButtonDesign(checkoutButton);
        inputsPanel.add(checkoutButton);

        cartScrollPane = new JScrollPane(cartTable);
        cartScrollPane.setPreferredSize(tableDimension);
        tablesPanel.add(cartScrollPane, BorderLayout.CENTER);

        // Setup bottom panel
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 10));
        bottomPanel.setPreferredSize(new Dimension(0, 50));
        bottomPanel.setBackground(mainColor);
        add(bottomPanel, BorderLayout.SOUTH);

        backButton = new JButton("Back");
        setButtonDesign(backButton);
        bottomPanel.add(backButton);

        totalCostTextField.setText(String.format("RS %.2f", totalCost));

        setVisible(true);
    }

    private void setButtonDesign(JButton button) {
        button.setPreferredSize(buttonsDimension);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(inputColor));
        button.setBackground(mainColor);
        button.setForeground(inputColor);
        button.addActionListener(this);
    }

    private void updateCartTable() {
        Cart cart = Cart.getInstance();
        if (cart.getUserId().equals(currentUserId)) {
            List<String[]> items = cart.getItems();
            cartData = new String[items.size()][4];
            for (int i = 0; i < items.size(); i++) {
                String[] item = items.get(i);
                cartData[i][0] = item[4];
                cartData[i][1] = item[1];
                cartData[i][2] = item[2];
                cartData[i][3] = item[3];
            }
            totalCost = cart.getTotalCost();
        }
        cartTableModel.setDataVector(cartData, tableColumns);
        setupTableAppearance();
    }

    private void setupTableAppearance() {
        cartTable.setRowHeight(150);  // Increased from 100 to 150
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(150);  // Increased width for image column
        
        cartTable.setDefaultRenderer(ImageIcon.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(JLabel.CENTER);
                if (value != null) {
                    String imagePath = (String) value;
                    java.net.URL imgURL = getClass().getResource("/" + imagePath);
                    if (imgURL != null) {
                        ImageIcon icon = new ImageIcon(imgURL);
                        // Increased image size from 80x80 to 120x120
                        Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                        label.setIcon(new ImageIcon(img));
                    }
                }
                return label;
            }
        });

        // Set column widths
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Name
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Category
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Price
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == removeItemButton) {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow != -1) {
                Cart.getInstance().removeItem(selectedRow);
                updateCartTable();  // Use updateCartTable instead
                totalCostTextField.setText(String.format("RS %.2f", totalCost));
            }
        } else if (event.getSource() == checkoutButton) {
            if (sellerUsernameComboBox.getSelectedItem() != null) {
                try {
                    Sale sale = new Sale(0, totalCost, 
                        (String)sellerUsernameComboBox.getSelectedItem(), 
                        java.time.LocalDateTime.now().toString());
                    if (saleDAO.createSale(sale)) {
                        Cart.getInstance().clear();
                        JOptionPane.showMessageDialog(this, "Sale completed successfully!");
                        new MenuFrame();
                        dispose();
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (event.getSource() == backButton) {
            try {
                new MenuFrame();
                dispose();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
