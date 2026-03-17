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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author UNKNOWN_UN
 */
@Entity
@Table(name = "student_fee_payments")
@NamedQueries({
    @NamedQuery(name = "StudentFeePayments.findAll", query = "SELECT s FROM StudentFeePayments s"),
    @NamedQuery(name = "StudentFeePayments.findByStudentFeePaymentsId", query = "SELECT s FROM StudentFeePayments s WHERE s.studentFeePaymentsId = :studentFeePaymentsId"),
    @NamedQuery(name = "StudentFeePayments.findByTotalFee", query = "SELECT s FROM StudentFeePayments s WHERE s.totalFee = :totalFee"),
    @NamedQuery(name = "StudentFeePayments.findByTotalPaid", query = "SELECT s FROM StudentFeePayments s WHERE s.totalPaid = :totalPaid"),
    @NamedQuery(name = "StudentFeePayments.findByTotalBalance", query = "SELECT s FROM StudentFeePayments s WHERE s.totalBalance = :totalBalance"),
    @NamedQuery(name = "StudentFeePayments.findByPaymentType", query = "SELECT s FROM StudentFeePayments s WHERE s.paymentType = :paymentType"),
    @NamedQuery(name = "StudentFeePayments.findByCourseType", query = "SELECT s FROM StudentFeePayments s WHERE s.courseType = :courseType"),
    @NamedQuery(name = "StudentFeePayments.findByPaymentStatus", query = "SELECT s FROM StudentFeePayments s WHERE s.paymentStatus = :paymentStatus"),
    @NamedQuery(name = "StudentFeePayments.findByRemarks", query = "SELECT s FROM StudentFeePayments s WHERE s.remarks = :remarks"),
    @NamedQuery(name = "StudentFeePayments.findByCreatedAt", query = "SELECT s FROM StudentFeePayments s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "StudentFeePayments.findByLastMofidied", query = "SELECT s FROM StudentFeePayments s WHERE s.lastMofidied = :lastMofidied"),
    @NamedQuery(name = "StudentFeePayments.findByUser", query = "SELECT s FROM StudentFeePayments s WHERE s.user = :user"),
    @NamedQuery(name = "StudentFeePayments.findByStatus", query = "SELECT s FROM StudentFeePayments s WHERE s.status = :status")})
public class StudentFeePayments implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "student_fee_payments_id")
    private Integer studentFeePaymentsId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")

//    @OneToOne
//    @JoinColumn(name = "enrollment_id")
    private CourseEnrollment enrollment;

    @Column(name = "total_fee")
    private Integer totalFee;
    @Column(name = "total_paid")
    private Integer totalPaid;
    @Column(name = "total_balance")
    private Integer totalBalance;
    @Column(name = "payment_type")
    private String paymentType;
    @Column(name = "course_type")
    private String courseType;
    @Column(name = "payment_status")
    private String paymentStatus;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "last_mofidied")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastMofidied;
    @Column(name = "user")
    private String user;
    @Column(name = "status")
    private Boolean status;

    public StudentFeePayments() {
    }

    public StudentFeePayments(Integer studentFeePaymentsId) {
        this.studentFeePaymentsId = studentFeePaymentsId;
    }

    public Integer getStudentFeePaymentsId() {
        return studentFeePaymentsId;
    }

    public void setStudentFeePaymentsId(Integer studentFeePaymentsId) {
        this.studentFeePaymentsId = studentFeePaymentsId;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(Integer totalPaid) {
        this.totalPaid = totalPaid;
    }

    public Integer getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Integer totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastMofidied() {
        return lastMofidied;
    }

    public void setLastMofidied(Date lastMofidied) {
        this.lastMofidied = lastMofidied;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CourseEnrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(CourseEnrollment enrollment) {
        this.enrollment = enrollment;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studentFeePaymentsId != null ? studentFeePaymentsId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudentFeePayments)) {
            return false;
        }
        StudentFeePayments other = (StudentFeePayments) object;
        if ((this.studentFeePaymentsId == null && other.studentFeePaymentsId != null) || (this.studentFeePaymentsId != null && !this.studentFeePaymentsId.equals(other.studentFeePaymentsId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Student_Management.StudentFeePayments[ studentFeePaymentsId=" + studentFeePaymentsId + " ]";
    }

}
