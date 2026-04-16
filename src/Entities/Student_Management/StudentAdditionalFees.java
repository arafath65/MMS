/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities.Student_Management;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "student_additional_fees")
public class StudentAdditionalFees implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_additional_fees_id")
    private Integer studentAdditionalFeesId;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "enrollment_id")
    private Integer enrollmentId;

    @Column(name = "fee_type_id")
    private Integer feeTypeId;
    
    @Column(name = "qty")
    private Double qty;

    @Column(name = "amount")
    private Double amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "issued_date")
    private Date issuedDate;

    @Column(name = "user")
    private String user;

    @Column(name = "status")
    private Integer status;

    // =========================
    // GETTERS & SETTERS
    // =========================
    public Integer getStudentAdditionalFeesId() {
        return studentAdditionalFeesId;
    }

    public void setStudentAdditionalFeesId(Integer studentAdditionalFeesId) {
        this.studentAdditionalFeesId = studentAdditionalFeesId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Integer enrollmentId) {
        this.enrollmentId = enrollmentId;
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
    
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
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
