package Entities.Settings;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author UNKNOWN_UN
 */
@Entity
@Table(name = "course")
@NamedQueries({
   // @NamedQuery(name = "Course.findAll", query = "SELECT c FROM Course c"),
    @NamedQuery(name = "Course.findAll", query = "SELECT c FROM Course c WHERE c.status = 1"),
    @NamedQuery(name = "Course.findByCourseId", query = "SELECT c FROM Course c WHERE c.courseId = :courseId"),
    @NamedQuery(name = "Course.findByBatch", query = "SELECT c FROM Course c WHERE c.batch = :batch"),
    @NamedQuery(name = "Course.findByCourseName", query = "SELECT c FROM Course c WHERE c.courseName = :courseName"),
    @NamedQuery(name = "Course.findByEnrolYear", query = "SELECT c FROM Course c WHERE c.enrolYear = :enrolYear"),
    @NamedQuery(name = "Course.findByEnrolMonth", query = "SELECT c FROM Course c WHERE c.enrolMonth = :enrolMonth"),
    @NamedQuery(name = "Course.findByCompYear", query = "SELECT c FROM Course c WHERE c.compYear = :compYear"),
    @NamedQuery(name = "Course.findByCompMonth", query = "SELECT c FROM Course c WHERE c.compMonth = :compMonth"),
    @NamedQuery(name = "Course.findByPaymentMode", query = "SELECT c FROM Course c WHERE c.paymentMode = :paymentMode"),
    @NamedQuery(name = "Course.findByAdmissionFee", query = "SELECT c FROM Course c WHERE c.admissionFee = :admissionFee"),
    @NamedQuery(name = "Course.findByFee", query = "SELECT c FROM Course c WHERE c.fee = :fee"),
    @NamedQuery(name = "Course.findByStatus", query = "SELECT c FROM Course c WHERE c.status = :status")})
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "course_id")
    private Integer courseId;
    @Column(name = "batch")
    private String batch;
    @Column(name = "course_name")
    private String courseName;
    @Column(name = "enrol_year")
    private Integer enrolYear;
    @Column(name = "enrol_month")
    private Integer enrolMonth;
    @Column(name = "comp_year")
    private Integer compYear;
    @Column(name = "comp_month")
    private Integer compMonth;
    @Column(name = "payment_mode")
    private String paymentMode;
    @Column(name = "admission_fee")
    private Integer admissionFee;
    @Column(name = "fee")
    private Integer fee;
    @Column(name = "status")
    private Integer status;

    public Course() {
    }

    public Course(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getEnrolYear() {
        return enrolYear;
    }

    public void setEnrolYear(Integer enrolYear) {
        this.enrolYear = enrolYear;
    }

    public Integer getEnrolMonth() {
        return enrolMonth;
    }

    public void setEnrolMonth(Integer enrolMonth) {
        this.enrolMonth = enrolMonth;
    }

    public Integer getCompYear() {
        return compYear;
    }

    public void setCompYear(Integer compYear) {
        this.compYear = compYear;
    }

    public Integer getCompMonth() {
        return compMonth;
    }

    public void setCompMonth(Integer compMonth) {
        this.compMonth = compMonth;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
    
    public Integer getAdmissionFee() {
        return admissionFee;
    }

    public void setAdmissionFee(Integer admissionFee) {
        this.admissionFee = admissionFee;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (courseId != null ? courseId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Course)) {
            return false;
        }
        Course other = (Course) object;
        if ((this.courseId == null && other.courseId != null) || (this.courseId != null && !this.courseId.equals(other.courseId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Settings.Course[ courseId=" + courseId + " ]";
    }
    
}
///
//////*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package Entities.Settings;
//
//import Entities.Student_Management.CourseEnrollment;
//import java.io.Serializable;
//import java.util.Collection;
//import javax.persistence.Basic;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.NamedQueries;
//import javax.persistence.NamedQuery;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//
///**
// *
// * @author UNKNOWN_UN
// */
//@Entity
//@Table(name = "course")
//@NamedQueries({
//   // @NamedQuery(name = "Course.findAll", query = "SELECT c FROM Course c"),
//    @NamedQuery(name = "Course.findAll", query = "SELECT c FROM Course c WHERE c.status = 1"),
//    @NamedQuery(name = "Course.findByCourseId", query = "SELECT c FROM Course c WHERE c.courseId = :courseId"),
//    @NamedQuery(name = "Course.findByBatch", query = "SELECT c FROM Course c WHERE c.batch = :batch"),
//    @NamedQuery(name = "Course.findByCourseName", query = "SELECT c FROM Course c WHERE c.courseName = :courseName"),
//    @NamedQuery(name = "Course.findByEnrolYear", query = "SELECT c FROM Course c WHERE c.enrolYear = :enrolYear"),
//    @NamedQuery(name = "Course.findByEnrolMonth", query = "SELECT c FROM Course c WHERE c.enrolMonth = :enrolMonth"),
//    @NamedQuery(name = "Course.findByCompYear", query = "SELECT c FROM Course c WHERE c.compYear = :compYear"),
//    @NamedQuery(name = "Course.findByCompMonth", query = "SELECT c FROM Course c WHERE c.compMonth = :compMonth"),
//    @NamedQuery(name = "Course.findByPaymentMode", query = "SELECT c FROM Course c WHERE c.paymentMode = :paymentMode"),
//    @NamedQuery(name = "Course.findByAdmissionFee", query = "SELECT c FROM Course c WHERE c.admissionFee = :admissionFee"),
//    @NamedQuery(name = "Course.findByFee", query = "SELECT c FROM Course c WHERE c.fee = :fee"),
//    @NamedQuery(name = "Course.findByStatus", query = "SELECT c FROM Course c WHERE c.status = :status")})
//public class Course implements Serializable {
//
////    @OneToMany(mappedBy = "courseId")
//    @OneToMany(mappedBy = "courseId", fetch = FetchType.LAZY)
//    private Collection<CourseEnrollment> courseEnrollmentCollection;
//
//    private static final long serialVersionUID = 1L;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Basic(optional = false)
//    @Column(name = "course_id")
//    private Integer courseId;
//    @Column(name = "batch")
//    private String batch;
//    @Column(name = "course_name")
//    private String courseName;
//    @Column(name = "enrol_year")
//    private Integer enrolYear;
//    @Column(name = "enrol_month")
//    private Integer enrolMonth;
//    @Column(name = "comp_year")
//    private Integer compYear;
//    @Column(name = "comp_month")
//    private Integer compMonth;
//    @Column(name = "payment_mode")
//    private String paymentMode;
//    @Column(name = "admission_fee")
//    private Integer admissionFee;
//    @Column(name = "fee")
//    private Integer fee;
//    @Column(name = "status")
//    private Integer status;
//
//    public Course() {
//    }
//
//    public Course(Integer courseId) {
//        this.courseId = courseId;
//    }
//
//    public Integer getCourseId() {
//        return courseId;
//    }
//
//    public void setCourseId(Integer courseId) {
//        this.courseId = courseId;
//    }
//
//    public String getBatch() {
//        return batch;
//    }
//
//    public void setBatch(String batch) {
//        this.batch = batch;
//    }
//
//    public String getCourseName() {
//        return courseName;
//    }
//
//    public void setCourseName(String courseName) {
//        this.courseName = courseName;
//    }
//
//    public Integer getEnrolYear() {
//        return enrolYear;
//    }
//
//    public void setEnrolYear(Integer enrolYear) {
//        this.enrolYear = enrolYear;
//    }
//
//    public Integer getEnrolMonth() {
//        return enrolMonth;
//    }
//
//    public void setEnrolMonth(Integer enrolMonth) {
//        this.enrolMonth = enrolMonth;
//    }
//
//    public Integer getCompYear() {
//        return compYear;
//    }
//
//    public void setCompYear(Integer compYear) {
//        this.compYear = compYear;
//    }
//
//    public Integer getCompMonth() {
//        return compMonth;
//    }
//
//    public void setCompMonth(Integer compMonth) {
//        this.compMonth = compMonth;
//    }
//
//    public String getPaymentMode() {
//        return paymentMode;
//    }
//
//    public void setPaymentMode(String paymentMode) {
//        this.paymentMode = paymentMode;
//    }
//    
//    public Integer getAdmissionFee() {
//        return admissionFee;
//    }
//
//    public void setAdmissionFee(Integer admissionFee) {
//        this.admissionFee = admissionFee;
//    }
//
//    public Integer getFee() {
//        return fee;
//    }
//
//    public void setFee(Integer fee) {
//        this.fee = fee;
//    }
//
//    public Integer getStatus() {
//        return status;
//    }
//
//    public void setStatus(Integer status) {
//        this.status = status;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (courseId != null ? courseId.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof Course)) {
//            return false;
//        }
//        Course other = (Course) object;
//        if ((this.courseId == null && other.courseId != null) || (this.courseId != null && !this.courseId.equals(other.courseId))) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "Entities.Settings.Course[ courseId=" + courseId + " ]";
//    }
//
//    public Collection<CourseEnrollment> getCourseEnrollmentCollection() {
//        return courseEnrollmentCollection;
//    }
//
//    public void setCourseEnrollmentCollection(Collection<CourseEnrollment> courseEnrollmentCollection) {
//        this.courseEnrollmentCollection = courseEnrollmentCollection;
//    }
//    
//}
