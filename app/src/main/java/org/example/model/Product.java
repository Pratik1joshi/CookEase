package org.example.model;

public class Product {
    private int id;
    private String name;
    private String category;
    private String price;
    private String image;

    public Product(int id, String name, String category, String price, String image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.image = image;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    // Setters
    public void setImage(String image) {
        this.image = image;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    // toString Method
    @Override
    public String toString() {
        return "Product{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", category='" + category + '\'' +
               ", price='" + price + '\'' +
               ", image='" + image + '\'' +
               '}';
    }
}
