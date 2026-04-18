 ///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package Entities.Student_Management;

package Entities.Student_Management;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "student_fee_round_payment_master")
@NamedQueries({
    @NamedQuery(name = "StudentFeeRoundPaymentMaster.findAll", query = "SELECT s FROM StudentFeeRoundPaymentMaster s")
})
public class StudentFeeRoundPaymentMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_fee_round_payment_master_id")
    private Integer studentFeeRoundPaymentMasterId;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "payment_date")
    @Temporal(TemporalType.DATE)
    private Date paymentDate;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "total_paid")
    private double totalPaid;

    @Column(name = "rounding_adjustment")
    private double roundingAdjustment;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "user")
    private String user;

    @Column(name = "status")
    private int status;

    @OneToMany(mappedBy = "studentFeeRoundPaymentMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentFeeRoundPaymentMasterDetails> studentFeeRoundPaymentMasterDetailsList;

    // ===== Getters & Setters =====
    public Integer getStudentFeeRoundPaymentMasterId() {
        return studentFeeRoundPaymentMasterId;
    }

    public void setStudentFeeRoundPaymentMasterId(Integer studentFeeRoundPaymentMasterId) {
        this.studentFeeRoundPaymentMasterId = studentFeeRoundPaymentMasterId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public double getRoundingAdjustment() {
        return roundingAdjustment;
    }

    public void setRoundingAdjustment(double roundingAdjustment) {
        this.roundingAdjustment = roundingAdjustment;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<StudentFeeRoundPaymentMasterDetails> getStudentFeeRoundPaymentMasterDetailsList() {
        return studentFeeRoundPaymentMasterDetailsList;
    }

    public void setStudentFeeRoundPaymentMasterDetailsList(List<StudentFeeRoundPaymentMasterDetails> list) {
        this.studentFeeRoundPaymentMasterDetailsList = list;
    }

}
