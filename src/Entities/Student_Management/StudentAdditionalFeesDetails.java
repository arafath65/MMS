
package Entities.Student_Management;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="student_additional_fees_details")
public class StudentAdditionalFeesDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="st_additional_fees_details_id")
    private Integer id;

    @Column(name="st_additional_fees_master_id")
    private Integer masterId;

    @Column(name="feetype_id")
    private Integer feeTypeId;

    @Column(name="qty")
    private Double qty;

    @Column(name="unit_price")
    private Double unitPrice;

    @Column(name="line_total")
    private Double lineTotal;

    @Column(name="line_discount")
    private Double lineDiscount;

    @Column(name="line_net_amount")
    private Double lineNetAmount;

    @Column(name="user")
    private String user;

    @Column(name="status")
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public Integer getFeeTypeId() {
        return feeTypeId;
    }

    public void setFeeTypeId(Integer feeTypeId) {
        this.feeTypeId = feeTypeId;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }

    public Double getLineDiscount() {
        return lineDiscount;
    }

    public void setLineDiscount(Double lineDiscount) {
        this.lineDiscount = lineDiscount;
    }

    public Double getLineNetAmount() {
        return lineNetAmount;
    }

    public void setLineNetAmount(Double lineNetAmount) {
        this.lineNetAmount = lineNetAmount;
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
