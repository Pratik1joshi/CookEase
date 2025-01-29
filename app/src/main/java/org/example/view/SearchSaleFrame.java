package org.example.view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;  // Add this import
import javax.swing.table.TableCellRenderer;  // Add this import

import org.example.model.dao.SaleDAO;
import org.example.model.dao.CartHistoryDAO;

public class SearchSaleFrame extends JFrame implements ActionListener {
    private final JPanel bottomPanel, mainPanel;
    private final JButton backButton;
    private final Dimension buttonsDimension = new Dimension(100, 25);
    private final Color mainColor = Color.white, inputColor = Color.black;
    private JTable historyTable;
    private final CartHistoryDAO cartHistoryDAO;
    private final String[] columnNames = {"Username", "Product", "Category", "Price", "Image"};

    SearchSaleFrame() throws SQLException {
        cartHistoryDAO = new CartHistoryDAO();

        setTitle("Cart History");
        setSize(1000, 720);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Main panel for cart history
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(mainColor);
        add(mainPanel, BorderLayout.CENTER);

        // Setup history table
        setupHistoryTable();

        // Bottom panel with back button
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 15));
        bottomPanel.setPreferredSize(new Dimension(0, 50));
        bottomPanel.setBackground(mainColor);
        add(bottomPanel, BorderLayout.SOUTH);

        backButton = new JButton("Back");
        setButtonDesign(backButton);
        bottomPanel.add(backButton);

        setVisible(true);
    }

    private void setButtonDesign(JButton button) {
        button.setPreferredSize(buttonsDimension);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(inputColor));
        button.setBackground(mainColor);
        button.setForeground(inputColor);
        button.addActionListener(this);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent event) {
                button.setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    private void setupHistoryTable() {
        String[][] data = null;
        try {
            data = cartHistoryDAO.getCartHistory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        historyTable = new JTable(model);
        historyTable.setDefaultEditor(Object.class, null);
        
        // Set table appearance
        historyTable.setRowHeight(60);
        
        // Set column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Username
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Product
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Category
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Price
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Image

        // Setup image renderer
        historyTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(JLabel.CENTER);
                if (value != null) {
                    String imagePath = (String)value;
                    java.net.URL imgURL = getClass().getResource("/" + imagePath);
                    if (imgURL != null) {
                        ImageIcon icon = new ImageIcon(imgURL);
                        Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                        label.setIcon(new ImageIcon(img));
                    }
                }
                return label;
            }
        });

        // Add table to scroll pane and to main panel
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        mainPanel.add(historyScrollPane, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == backButton) {
            try {
                new MenuFrame();
            } catch (IOException | SQLException exception) {
                exception.printStackTrace();
            }
            dispose();
        }
    }
}

