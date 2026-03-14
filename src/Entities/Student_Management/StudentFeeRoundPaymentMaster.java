/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities.Student_Management;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author UNKNOWN_UN
 */
@Entity
@Table(name = "student_fee_round_payment_master")
@NamedQueries({
    @NamedQuery(name = "StudentFeeRoundPaymentMaster.findAll", query = "SELECT s FROM StudentFeeRoundPaymentMaster s"),
    @NamedQuery(name = "StudentFeeRoundPaymentMaster.findByStudentFeeRoundPaymentMasterId", query = "SELECT s FROM StudentFeeRoundPaymentMaster s WHERE s.studentFeeRoundPaymentMasterId = :studentFeeRoundPaymentMasterId"),
    @NamedQuery(name = "StudentFeeRoundPaymentMaster.findByPaymentDate", query = "SELECT s FROM StudentFeeRoundPaymentMaster s WHERE s.paymentDate = :paymentDate"),
    @NamedQuery(name = "StudentFeeRoundPaymentMaster.findByPaymentMode", query = "SELECT s FROM StudentFeeRoundPaymentMaster s WHERE s.paymentMode = :paymentMode"),
    @NamedQuery(name = "StudentFeeRoundPaymentMaster.findByTotalPaid", query = "SELECT s FROM StudentFeeRoundPaymentMaster s WHERE s.totalPaid = :totalPaid"),
    @NamedQuery(name = "StudentFeeRoundPaymentMaster.findByRoundingAdjustment", query = "SELECT s FROM StudentFeeRoundPaymentMaster s WHERE s.roundingAdjustment = :roundingAdjustment"),
    @NamedQuery(name = "StudentFeeRoundPaymentMaster.findByRemarks", query = "SELECT s FROM StudentFeeRoundPaymentMaster s WHERE s.remarks = :remarks"),
    @NamedQuery(name = "StudentFeeRoundPaymentMaster.findByUser", query = "SELECT s FROM StudentFeeRoundPaymentMaster s WHERE s.user = :user"),
    @NamedQuery(name = "StudentFeeRoundPaymentMaster.findByStatus", query = "SELECT s FROM StudentFeeRoundPaymentMaster s WHERE s.status = :status")})
public class StudentFeeRoundPaymentMaster implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
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
    private Integer totalPaid;
    @Column(name = "rounding_adjustment")
    private Integer roundingAdjustment;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "user")
    private String user;
    @Column(name = "status")
    private int status;
    

    @OneToMany(mappedBy = "studentFeeRoundPaymentMasterId")
    private List<StudentFeeRoundPaymentMasterDetails> studentFeeRoundPaymentMasterDetailsList;

    public StudentFeeRoundPaymentMaster() {
    }

    public StudentFeeRoundPaymentMaster(Integer studentFeeRoundPaymentMasterId) {
        this.studentFeeRoundPaymentMasterId = studentFeeRoundPaymentMasterId;
    }

    public Integer getStudentFeeRoundPaymentMasterId() {
        return studentFeeRoundPaymentMasterId;
    }

    public void setStudentFeeRoundPaymentMasterId(Integer studentFeeRoundPaymentMasterId) {
        this.studentFeeRoundPaymentMasterId = studentFeeRoundPaymentMasterId;
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

    public Integer getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(Integer totalPaid) {
        this.totalPaid = totalPaid;
    }

    public Integer getRoundingAdjustment() {
        return roundingAdjustment;
    }

    public void setRoundingAdjustment(Integer roundingAdjustment) {
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
    
    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public List<StudentFeeRoundPaymentMasterDetails> getStudentFeeRoundPaymentMasterDetailsList() {
        return studentFeeRoundPaymentMasterDetailsList;
    }

    public void setStudentFeeRoundPaymentMasterDetailsList(List<StudentFeeRoundPaymentMasterDetails> studentFeeRoundPaymentMasterDetailsList) {
        this.studentFeeRoundPaymentMasterDetailsList = studentFeeRoundPaymentMasterDetailsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studentFeeRoundPaymentMasterId != null ? studentFeeRoundPaymentMasterId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudentFeeRoundPaymentMaster)) {
            return false;
        }
        StudentFeeRoundPaymentMaster other = (StudentFeeRoundPaymentMaster) object;
        if ((this.studentFeeRoundPaymentMasterId == null && other.studentFeeRoundPaymentMasterId != null) || (this.studentFeeRoundPaymentMasterId != null && !this.studentFeeRoundPaymentMasterId.equals(other.studentFeeRoundPaymentMasterId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Student_Management.StudentFeeRoundPaymentMaster[ studentFeeRoundPaymentMasterId=" + studentFeeRoundPaymentMasterId + " ]";
    }

}
