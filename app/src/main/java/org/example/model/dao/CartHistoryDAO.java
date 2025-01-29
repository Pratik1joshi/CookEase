package org.example.model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.model.MySQLConnector;

public class CartHistoryDAO {
    private final Connection connection;
    private PreparedStatement statement;

    public CartHistoryDAO() throws SQLException {
        connection = MySQLConnector.getConnection();
    }

    public String[][] getCartHistory() throws SQLException {
        List<String[]> history = new ArrayList<>();
        
        String query = "SELECT u.username, p.name, p.category, p.price, p.image " +
                      "FROM Cart c " +
                      "JOIN CartItems ci ON c.id = ci.cart_id " +
                      "JOIN Product p ON ci.product_id = p.id " +
                      "JOIN User u ON c.user_id = u.username " +
                      "ORDER BY c.id DESC";
        
        statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        
        while (rs.next()) {
            String[] row = new String[5];
            row[0] = rs.getString("username");  // User who added to cart
            row[1] = rs.getString("name");      // Product name
            row[2] = rs.getString("category");  // Product category
            row[3] = rs.getString("price");     // Product price
            row[4] = rs.getString("image");     // Product image
            history.add(row);
        }
        
        return history.toArray(new String[0][]);
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
