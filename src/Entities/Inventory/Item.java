
package Entities.Inventory;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "items")

@NamedQueries({

//    @NamedQuery(
    //            name = "Item.findAll",
    //            query = "SELECT i FROM Item i WHERE i.status = 1"
    //    ),
    
    @NamedQuery(
    name = "Item.findAll",
    query = "SELECT i FROM Item i WHERE i.status = 1"
),

    @NamedQuery(
            name = "Item.findById",
            query = "SELECT i FROM Item i WHERE i.itemId = :id"
    ),

    @NamedQuery(
            name = "Item.findByName",
            query = "SELECT i FROM Item i WHERE i.itemName LIKE :name AND i.status = 1"
    )
})
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "category")
    private String category;

    @Column(name = "units")
    private String units;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "description")
    private String description;

    @Column(name = "user")
    private String user;

    @Column(name = "status")
    private Integer status = 1;

    // ---------------- GETTERS & SETTERS ----------------

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    // ---------------- OVERRIDE ----------------

    @Override
    public int hashCode() {
        return itemId != null ? itemId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) return false;
        Item other = (Item) obj;
        return itemId != null && itemId.equals(other.itemId);
    }

    @Override
    public String toString() {
        return itemName;
    }
}
