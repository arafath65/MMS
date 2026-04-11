
package Entities.Student_Management;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "fee_types")
public class FeeTypes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_type_id")
    private Integer feeTypeId;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "fee_name")
    private String feeName;

    @Column(name = "fee_category")
    private String feeCategory;

    @Column(name = "default_amount")
    private Double defaultAmount;

    @Column(name = "user")
    private String user;

    @Column(name = "status")
    private Integer status;

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Integer getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Integer feeTypeId) { this.feeTypeId = feeTypeId; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public String getFeeName() { return feeName; }
    public void setFeeName(String feeName) { this.feeName = feeName; }

    public String getFeeCategory() { return feeCategory; }
    public void setFeeCategory(String feeCategory) { this.feeCategory = feeCategory; }

    public Double getDefaultAmount() { return defaultAmount; }
    public void setDefaultAmount(Double defaultAmount) { this.defaultAmount = defaultAmount; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}