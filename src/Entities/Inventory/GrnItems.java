package Entities.Inventory;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "grn_items")

@NamedQueries({
    @NamedQuery(
            name = "GrnItems.findAll",
            query = "SELECT g FROM GrnItems g WHERE g.status = 1"
    )
})
public class GrnItems implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grn_items_id")
    private Integer grnItemsId;

    @Column(name = "grn_id")
    private Integer grnId;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "units")
    private String units;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(name = "line_total")
    private Double lineTotal;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "user")
    private String user;

    @Column(name = "status")
    private Integer status = 1;

    // ---------------- GETTERS & SETTERS ----------------

    public Integer getGrnItemsId() {
        return grnItemsId;
    }

    public void setGrnItemsId(Integer grnItemsId) {
        this.grnItemsId = grnItemsId;
    }

    public Integer getGrnId() {
        return grnId;
    }

    public void setGrnId(Integer grnId) {
        this.grnId = grnId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
}