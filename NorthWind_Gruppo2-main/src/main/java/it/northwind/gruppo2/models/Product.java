//Gruppo 2
package it.northwind.gruppo2.models;

//Import vari
import jakarta.persistence.*;

//Entità
@Entity
@Table(name = "Products")

//Costruttore
public class Product {

    //Valori di Product confrontati da DBeaver
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private int productId;

    @Column(name = "ProductName")
    private String productName;

    //Ai fini d'esempio, e come concordato col prof Iannì, gestiamo la FK solo di Categories (Il progetto prevede Products + Categories), lascio quindi Suppliers come semplice numero
    @Column(name = "SupplierID")
    private Integer supplierId;

    //Oggetto Category
    @ManyToOne
    @JoinColumn(name = "CategoryID")
    private Category category;

    @Column(name = "QuantityPerUnit")
    private String quantityPerUnit;

    @Column(name = "UnitPrice")
    private Double unitPrice;

    @Column(name = "UnitsInStock")
    private Integer unitsInStock;

    @Column(name = "UnitsOnOrder")
    private Integer unitsOnOrder;

    @Column(name = "ReorderLevel")
    private Integer reorderLevel;

    @Column(name = "Discontinued")
    private String discontinued;

    // Costruttore vuoto (Obbligatorio per Hibernate a quanto pare)
    public Product() {
    }

    //Getter e Setter
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getQuantityPerUnit() { return quantityPerUnit; }
    public void setQuantityPerUnit(String quantityPerUnit) { this.quantityPerUnit = quantityPerUnit; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Integer getUnitsInStock() { return unitsInStock; }
    public void setUnitsInStock(Integer unitsInStock) { this.unitsInStock = unitsInStock; }

    public Integer getUnitsOnOrder() { return unitsOnOrder; }
    public void setUnitsOnOrder(Integer unitsOnOrder) { this.unitsOnOrder = unitsOnOrder; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public String getDiscontinued() { return discontinued; }
    public void setDiscontinued(String discontinued) { this.discontinued = discontinued; }
}