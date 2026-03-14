/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities.Student_Management;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author UNKNOWN_UN
 */
@Entity
@Table(name = "student_fee_installments")
@NamedQueries({
    @NamedQuery(name = "StudentFeeInstallments.findAll", query = "SELECT s FROM StudentFeeInstallments s"),
    @NamedQuery(name = "StudentFeeInstallments.findByStudentFeeInstallmentsId", query = "SELECT s FROM StudentFeeInstallments s WHERE s.studentFeeInstallmentsId = :studentFeeInstallmentsId"),
    @NamedQuery(name = "StudentFeeInstallments.findByInstallmentNo", query = "SELECT s FROM StudentFeeInstallments s WHERE s.installmentNo = :installmentNo"),
    @NamedQuery(name = "StudentFeeInstallments.findByAmountPaid", query = "SELECT s FROM StudentFeeInstallments s WHERE s.amountPaid = :amountPaid"),
    @NamedQuery(name = "StudentFeeInstallments.findByPaymentDate", query = "SELECT s FROM StudentFeeInstallments s WHERE s.paymentDate = :paymentDate"),
    @NamedQuery(name = "StudentFeeInstallments.findByPaymentMethod", query = "SELECT s FROM StudentFeeInstallments s WHERE s.paymentMethod = :paymentMethod"),
    @NamedQuery(name = "StudentFeeInstallments.findByMonthFor", query = "SELECT s FROM StudentFeeInstallments s WHERE s.monthFor = :monthFor"),
    @NamedQuery(name = "StudentFeeInstallments.findByStatus", query = "SELECT s FROM StudentFeeInstallments s WHERE s.status = :status")})
public class StudentFeeInstallments implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "student_fee_installments_id")
    private Integer studentFeeInstallmentsId;
    @Column(name = "installment_no")
    private int installmentNo;
    @Column(name = "amount_paid")
    private Integer amountPaid;
    @Column(name = "payment_date")
    @Temporal(TemporalType.DATE)
    private Date paymentDate;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "month_for")
    private String monthFor;
    @Column(name = "status")
    private int status;

    @Column(name = "student_fee_payments_id")
    private Integer studentFeePaymentsId;

    @Column(name = "enrollment_id")
    private Integer enrollmentId;

    @Column(name = "payment_type")   // <-- NEW
    private String paymentType;

    public StudentFeeInstallments() {
    }

    public StudentFeeInstallments(Integer studentFeeInstallmentsId) {
        this.studentFeeInstallmentsId = studentFeeInstallmentsId;
    }

    public Integer getStudentFeeInstallmentsId() {
        return studentFeeInstallmentsId;
    }

    public void setStudentFeeInstallmentsId(Integer studentFeeInstallmentsId) {
        this.studentFeeInstallmentsId = studentFeeInstallmentsId;
    }

    public Integer getInstallmentNo() {
        return installmentNo;
    }

    public void setInstallmentNo(Integer installmentNo) {
        this.installmentNo = installmentNo;
    }

    public Integer getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Integer amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getMonthFor() {
        return monthFor;
    }

    public void setMonthFor(String monthFor) {
        this.monthFor = monthFor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getStudentFeePaymentsId() {
        return studentFeePaymentsId;
    }

    public void setStudentFeePaymentsId(Integer studentFeePaymentsId) {
        this.studentFeePaymentsId = studentFeePaymentsId;
    }

    public Integer getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Integer enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getPaymentType() {
        return paymentType;
    }       // <-- NEW

    public void setPaymentType(String paymentType) {             // <-- NEW
        this.paymentType = paymentType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studentFeeInstallmentsId != null ? studentFeeInstallmentsId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudentFeeInstallments)) {
            return false;
        }
        StudentFeeInstallments other = (StudentFeeInstallments) object;
        if ((this.studentFeeInstallmentsId == null && other.studentFeeInstallmentsId != null) || (this.studentFeeInstallmentsId != null && !this.studentFeeInstallmentsId.equals(other.studentFeeInstallmentsId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Student_Management.StudentFeeInstallments[ studentFeeInstallmentsId=" + studentFeeInstallmentsId + " ]";
    }

}
