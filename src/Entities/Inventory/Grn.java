
package Entities.Inventory;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "grn")

@NamedQueries({
    @NamedQuery(
        name = "Grn.findAll",
        query = "SELECT g FROM Grn g WHERE g.status = 1"
    )
})
public class Grn implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grn_id")
    private Integer grnId;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "suppliers_id")
    private Integer suppliersId;

    @Column(name = "invoice_no")
    private String invoiceNo;

    @Temporal(TemporalType.DATE)
    @Column(name = "grn_date")
    private Date grnDate;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "user")
    private String user;

    @Column(name = "status")
    private Integer status = 1;

    // Getters & Setters
    public Integer getGrnId() { return grnId; }
    public void setGrnId(Integer grnId) { this.grnId = grnId; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public Integer getSuppliersId() { return suppliersId; }
    public void setSuppliersId(Integer suppliersId) { this.suppliersId = suppliersId; }

    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }

    public Date getGrnDate() { return grnDate; }
    public void setGrnDate(Date grnDate) { this.grnDate = grnDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}