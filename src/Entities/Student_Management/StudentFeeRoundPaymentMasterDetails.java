/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities.Student_Management;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author UNKNOWN_UN
 */
@Entity
@Table(name = "student_fee_round_payment_master_details")
@NamedQueries({
    @NamedQuery(name = "StudentFeeRoundPaymentMasterDetails.findAll", query = "SELECT s FROM StudentFeeRoundPaymentMasterDetails s"),
    @NamedQuery(name = "StudentFeeRoundPaymentMasterDetails.findByStudentFeeRoundPaymentMasterDetailsId", query = "SELECT s FROM StudentFeeRoundPaymentMasterDetails s WHERE s.studentFeeRoundPaymentMasterDetailsId = :studentFeeRoundPaymentMasterDetailsId"),
    @NamedQuery(name = "StudentFeeRoundPaymentMasterDetails.findByPaidAmount", query = "SELECT s FROM StudentFeeRoundPaymentMasterDetails s WHERE s.paidAmount = :paidAmount"),
    @NamedQuery(name = "StudentFeeRoundPaymentMasterDetails.findByStatus", query = "SELECT s FROM StudentFeeRoundPaymentMasterDetails s WHERE s.status = :status")})
public class StudentFeeRoundPaymentMasterDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "student_fee_round_payment_master_details_id")
    private Integer studentFeeRoundPaymentMasterDetailsId;
    @Column(name = "enrollment_id")
    private Integer enrollmentId;
    @Column(name = "paid_amount")
    private Integer paidAmount;
    @Column(name = "status")
    private int status;
    @JoinColumn(name = "student_fee_round_payment_master_id", referencedColumnName = "student_fee_round_payment_master_id")
    @ManyToOne
    private StudentFeeRoundPaymentMaster studentFeeRoundPaymentMasterId;

    public StudentFeeRoundPaymentMasterDetails() {
    }

    public StudentFeeRoundPaymentMasterDetails(Integer studentFeeRoundPaymentMasterDetailsId) {
        this.studentFeeRoundPaymentMasterDetailsId = studentFeeRoundPaymentMasterDetailsId;
    }

    public Integer getStudentFeeRoundPaymentMasterDetailsId() {
        return studentFeeRoundPaymentMasterDetailsId;
    }

    public void setStudentFeeRoundPaymentMasterDetailsId(Integer studentFeeRoundPaymentMasterDetailsId) {
        this.studentFeeRoundPaymentMasterDetailsId = studentFeeRoundPaymentMasterDetailsId;
    }

    public Integer getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Integer paidAmount) {
        this.paidAmount = paidAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Integer enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public StudentFeeRoundPaymentMaster getStudentFeeRoundPaymentMasterId() {
        return studentFeeRoundPaymentMasterId;
    }

    public void setStudentFeeRoundPaymentMasterId(StudentFeeRoundPaymentMaster studentFeeRoundPaymentMasterId) {
        this.studentFeeRoundPaymentMasterId = studentFeeRoundPaymentMasterId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studentFeeRoundPaymentMasterDetailsId != null ? studentFeeRoundPaymentMasterDetailsId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudentFeeRoundPaymentMasterDetails)) {
            return false;
        }
        StudentFeeRoundPaymentMasterDetails other = (StudentFeeRoundPaymentMasterDetails) object;
        if ((this.studentFeeRoundPaymentMasterDetailsId == null && other.studentFeeRoundPaymentMasterDetailsId != null) || (this.studentFeeRoundPaymentMasterDetailsId != null && !this.studentFeeRoundPaymentMasterDetailsId.equals(other.studentFeeRoundPaymentMasterDetailsId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Student_Management.StudentFeeRoundPaymentMasterDetails[ studentFeeRoundPaymentMasterDetailsId=" + studentFeeRoundPaymentMasterDetailsId + " ]";
    }

}
