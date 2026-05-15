//Package
package it.northwind.gruppo2.models;

//Import vari
import jakarta.persistence.*;

//Entity
@Entity
@Table(name = "Categories")

//Costruttore
public class Category {
    //Valori da DBeaver
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID")
    private int categoryId;

    @Column(name = "CategoryName")
    private String categoryName;

    @Column(name = "Description")
    private String description;

    // Picture e' un BLOB non necessario al CRUD prodotti; lo escludiamo dal mapping Hibernate.
    @Transient
    private byte[] picture;

    // Costruttore vuoto (Obbligatorio per Hibernate a quanto pare)
    public Category() {
    }

    // Costruttore con parametri
    public Category(String categoryName, String description, byte[] picture) {
        this.categoryName = categoryName;
        this.description = description;
        this.picture = picture;
    }

    //Getter e Setter
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public byte[] getPicture() { return picture; }
    public void setPicture(byte[] picture) { this.picture = picture; }
}
