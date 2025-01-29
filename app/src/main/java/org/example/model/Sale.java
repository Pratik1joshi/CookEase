package org.example.model;

public class Sale {
    private int id;
    private float totalCost;
    private String sellerUsername;
    private String date;

    public Sale(int id, float totalCost, String sellerUsername, String date) {
        this.id = id;
        this.totalCost = totalCost;
        this.sellerUsername = sellerUsername;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // toString method
    @Override
    public String toString() {
        return "Sale{" +
               "id=" + id +
               ", totalCost=" + totalCost +
               ", sellerUsername='" + sellerUsername + '\'' +
               ", date='" + date + '\'' +
               '}';
    }
}
