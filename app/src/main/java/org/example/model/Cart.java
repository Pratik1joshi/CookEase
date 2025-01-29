package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import org.example.model.dao.CartDAO;  // Add this import

public class Cart {
    private static Cart instance;
    private final List<String[]> items;
    private float totalCost;
    private String userId;
    private CartDAO cartDAO;
    private String currentUserId;

    private Cart() {
        items = new ArrayList<>();
        totalCost = 0.0f;
        try {
            cartDAO = new CartDAO();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void setUserId(String userId) {
        this.currentUserId = userId;
        this.userId = userId;  // Set both IDs
        loadCartItems();
    }

    public String getUserId() {
        return currentUserId;  // Return currentUserId instead of userId
    }

    public void addItem(String[] product) {
        if (currentUserId != null) {
            try {
                cartDAO.addToCart(currentUserId, product);
                loadCartItems();  // Reload items after adding
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeItem(int index) {
        if (currentUserId != null && index >= 0 && index < items.size()) {
            try {
                String[] product = items.get(index);
                cartDAO.removeFromCart(currentUserId, Integer.parseInt(product[0]));
                loadCartItems();  // Reload items after removing
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void clear() {
        if (currentUserId != null) {
            try {
                cartDAO.clearUserCart(currentUserId);
                items.clear();
                totalCost = 0.0f;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadCartItems() {
        try {
            items.clear();
            totalCost = 0.0f;
            List<String[]> userItems = cartDAO.getCartItems(currentUserId);
            items.addAll(userItems);
            for (String[] item : items) {
                totalCost += Float.parseFloat(item[3].replace("RS", ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getItems() {
        return items;
    }

    public float getTotalCost() {
        return totalCost;
    }
}
