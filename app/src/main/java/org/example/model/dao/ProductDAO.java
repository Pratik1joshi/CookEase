package org.example.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.example.model.MySQLConnector;
import org.example.model.Product;

public class ProductDAO {
    private final Connection connection;
    private String query;
    private PreparedStatement statement;
    private int insertedLines = 0;

    public ProductDAO() throws SQLException {
        connection = MySQLConnector.getConnection();
    }

    public boolean createProduct(Product product) throws SQLException {
        query = "INSERT INTO Product (name, category, price, image) VALUES(?, ?, ?, ?)";
        statement = connection.prepareStatement(query);
        statement.setString(1, product.getName());
        statement.setString(2, product.getCategory());
        statement.setString(3, product.getPrice());
        statement.setString(4, product.getImage());
        insertedLines = statement.executeUpdate();
        return (insertedLines != 0);
    }


    public String[][] readProductsTableData() throws SQLException {
        query = "SELECT id, name, category, price, image FROM Product ORDER BY id";
        statement = connection.prepareStatement(query);
        ResultSet result = statement.executeQuery();

        List<String[]> productsList = new ArrayList<>();
        while(result.next()){
            String[] productRow = new String[5];
            productRow[0] = String.valueOf(result.getInt("id"));
            productRow[1] = result.getString("name");
            productRow[2] = result.getString("category");
            productRow[3] = result.getString("price");
            productRow[4] = result.getString("image");
            productsList.add(productRow);
        }

        return productsList.toArray(new String[0][]);
    }

    public boolean updateProduct(Product product) throws SQLException {
        query = "UPDATE Product SET name = ?, category = ?, price = ? WHERE id = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, product.getName());
        statement.setString(2, product.getCategory());
        statement.setString(3, product.getPrice());
        statement.setInt(4, product.getId());
        insertedLines = statement.executeUpdate();
        return(insertedLines != 0);
    }

    public boolean deleteProduct(int id) throws SQLException {
        query = "DELETE FROM Product WHERE id = ?";
        statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        insertedLines = statement.executeUpdate();
        return(insertedLines != 0);
    }

    public void close() throws SQLException {
        connection.close();
    }
}
