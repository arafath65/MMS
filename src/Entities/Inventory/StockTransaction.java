
package Entities.Inventory;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "stock_transactions")
public class StockTransaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_transactions_id")
    private Integer stockTransactionsId;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "suppliers_id")
    private Integer suppliersId;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "invoice_no")
    private String invoiceNo;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "transaction_type")
    private String transactionType; // IN / OUT

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "user")
    private String user;

    @Column(name = "status")
    private Integer status = 1;

    // Getters & Setters
    public Integer getStockTransactionsId() { return stockTransactionsId; }
    public void setStockTransactionsId(Integer stockTransactionsId) { this.stockTransactionsId = stockTransactionsId; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public Integer getSuppliersId() { return suppliersId; }
    public void setSuppliersId(Integer suppliersId) { this.suppliersId = suppliersId; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
