 ///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package Entities.Student_Management;

package Entities.Student_Management;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "student_fee_round_payment_master_details")
public class StudentFeeRoundPaymentMasterDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_fee_round_payment_master_details_id")
    private Integer studentFeeRoundPaymentMasterDetailsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_round_payment_master_id")
    private StudentFeeRoundPaymentMaster studentFeeRoundPaymentMaster;

    @Column(name = "enrollment_id")
    private Integer enrollmentId;

    @Column(name = "reference_id")
    private Integer referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "paid_amount")
    private double paidAmount;

    @Column(name = "status")
    private Integer status;

    // ===== Getters & Setters =====
    public Integer getStudentFeeRoundPaymentMasterDetailsId() {
        return studentFeeRoundPaymentMasterDetailsId;
    }

    public void setStudentFeeRoundPaymentMasterDetailsId(Integer id) {
        this.studentFeeRoundPaymentMasterDetailsId = id;
    }

    public StudentFeeRoundPaymentMaster getStudentFeeRoundPaymentMaster() {
        return studentFeeRoundPaymentMaster;
    }

    public void setStudentFeeRoundPaymentMaster(StudentFeeRoundPaymentMaster master) {
        this.studentFeeRoundPaymentMaster = master;
    }

    public Integer getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Integer enrollmentId) {
        this.enrollmentId = enrollmentId;

    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;

    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;

    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
