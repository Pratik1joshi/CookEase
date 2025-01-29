package org.example.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.model.MySQLConnector;

public class CartDAO {
    private final Connection connection;
    private PreparedStatement statement;

    public CartDAO() throws SQLException {
        connection = MySQLConnector.getConnection();
    }

    public void addToCart(String userId, String[] product) throws SQLException {
        // First, get or create cart for user
        String cartQuery = "INSERT INTO Cart (user_id) SELECT ? WHERE NOT EXISTS (SELECT 1 FROM Cart WHERE user_id = ?)";
        statement = connection.prepareStatement(cartQuery);
        statement.setString(1, userId);
        statement.setString(2, userId);
        statement.executeUpdate();

        // Get cart id
        String getCartQuery = "SELECT id FROM Cart WHERE user_id = ?";
        statement = connection.prepareStatement(getCartQuery);
        statement.setString(1, userId);
        ResultSet rs = statement.executeQuery();
        
        if (rs.next()) {
            int cartId = rs.getInt("id");
            // Add item to CartItems
            String itemQuery = "INSERT INTO CartItems (cart_id, product_id) VALUES (?, ?)";
            statement = connection.prepareStatement(itemQuery);
            statement.setInt(1, cartId);
            statement.setInt(2, Integer.parseInt(product[0]));
            statement.executeUpdate();
        }
    }

    public List<String[]> getCartItems(String userId) throws SQLException {
        List<String[]> items = new ArrayList<>();
        
        String query = "SELECT p.id, p.name, p.category, p.price, p.image " +
                      "FROM Cart c " +
                      "JOIN CartItems ci ON c.id = ci.cart_id " +
                      "JOIN Product p ON ci.product_id = p.id " +
                      "WHERE c.user_id = ?";
        
        statement = connection.prepareStatement(query);
        statement.setString(1, userId);
        ResultSet rs = statement.executeQuery();
        
        while (rs.next()) {
            String[] item = new String[5];
            item[0] = rs.getString("id");
            item[1] = rs.getString("name");
            item[2] = rs.getString("category");
            item[3] = rs.getString("price");
            item[4] = rs.getString("image");
            items.add(item);
        }
        
        return items;
    }

    public void removeFromCart(String userId, int productId) throws SQLException {
        String query = "DELETE ci FROM CartItems ci " +
                      "JOIN Cart c ON c.id = ci.cart_id " +
                      "WHERE c.user_id = ? AND ci.product_id = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, userId);
        statement.setInt(2, productId);
        statement.executeUpdate();
    }

    public void clearUserCart(String userId) throws SQLException {
        // First delete cart items
        String deleteItemsQuery = "DELETE ci FROM CartItems ci " +
                                "JOIN Cart c ON c.id = ci.cart_id " +
                                "WHERE c.user_id = ?";
        statement = connection.prepareStatement(deleteItemsQuery);
        statement.setString(1, userId);
        statement.executeUpdate();

        // Then delete cart
        String deleteCartQuery = "DELETE FROM Cart WHERE user_id = ?";
        statement = connection.prepareStatement(deleteCartQuery);
        statement.setString(1, userId);
        statement.executeUpdate();
    }

    public void close() throws SQLException {
        connection.close();
    }
}
