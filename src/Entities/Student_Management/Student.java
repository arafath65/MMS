package Entities.Student_Management;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "student")
@NamedQueries({
    @NamedQuery(name = "Student.findAll", query = "SELECT s FROM Student s")
})
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "admission_no")
    private String admissionNo;

    @Column(name = "form_no")
    private String formNo;

    @Temporal(TemporalType.DATE)
    @Column(name = "admission_date")
    private Date admissionDate;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "nic")
    private String nic;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    private Date dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "address")
    private String address;

    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "medical_info")
    private String medicalInfo;

    @Column(name = "current_status")
    private String currentStatus;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_parents_id")
    private StudentParents studentParents;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<CourseEnrollment> courseEnrollments;

    // Getters and Setters
    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    public String getFormNo() {
        return formNo;
    }

    public void setFormNo(String formNo) {
        this.formNo = formNo;
    }

    public Date getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getMedicalInfo() {
        return medicalInfo;
    }

    public void setMedicalInfo(String medicalInfo) {
        this.medicalInfo = medicalInfo;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public StudentParents getStudentParents() {
        return studentParents;
    }

    public void setStudentParents(StudentParents studentParents) {
        this.studentParents = studentParents;
    }

    public List<CourseEnrollment> getCourseEnrollments() {
        return courseEnrollments;
    }

    public void setCourseEnrollments(List<CourseEnrollment> courseEnrollments) {
        this.courseEnrollments = courseEnrollments;
    }

    @Override
    public int hashCode() {
        return studentId != null ? studentId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Student)) {
            return false;
        }
        Student other = (Student) obj;
        return studentId != null && studentId.equals(other.studentId);
    }

    @Override
    public String toString() {
        return admissionNo + " - " + fullName;
    }
}
//package Entities.Student_Management;
//
//import java.io.Serializable;
//import java.util.Collection;
//import java.util.Date;
//import java.util.List;
//import javax.persistence.Basic;
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.NamedQueries;
//import javax.persistence.NamedQuery;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//
///**
// *
// * @author UNKNOWN_UN
// */
//@Entity
//@Table(name = "student")
//@NamedQueries({
//    @NamedQuery(name = "Student.findAll", query = "SELECT s FROM Student s"),
//    @NamedQuery(name = "Student.findByStudentId", query = "SELECT s FROM Student s WHERE s.studentId = :studentId"),
////    @NamedQuery(name = "Student.findByAdmissionNo", query = "SELECT s FROM Student s WHERE s.admissionNo = :admissionNo"),
//    @NamedQuery(name = "Student.findByAdmissionNo", query = "SELECT s FROM Student s WHERE s.admissionNo = :admissionNo AND s.currentStatus = :currentStatus"),
//    @NamedQuery(name = "Student.findByFormNo", query = "SELECT s FROM Student s WHERE s.formNo = :formNo"),
//    @NamedQuery(name = "Student.findByAdmissionDate", query = "SELECT s FROM Student s WHERE s.admissionDate = :admissionDate"),
//    @NamedQuery(name = "Student.findByFullName", query = "SELECT s FROM Student s WHERE s.fullName = :fullName"),
//    @NamedQuery(name = "Student.findByNic", query = "SELECT s FROM Student s WHERE s.nic = :nic"),
//    @NamedQuery(name = "Student.findByDob", query = "SELECT s FROM Student s WHERE s.dob = :dob"),
//    @NamedQuery(name = "Student.findByGender", query = "SELECT s FROM Student s WHERE s.gender = :gender"),
//    @NamedQuery(name = "Student.findByAddress", query = "SELECT s FROM Student s WHERE s.address = :address"),
//    @NamedQuery(name = "Student.findByContactNo", query = "SELECT s FROM Student s WHERE s.contactNo = :contactNo"),
//    @NamedQuery(name = "Student.findByRemarks", query = "SELECT s FROM Student s WHERE s.remarks = :remarks"),
//    @NamedQuery(name = "Student.findByCurrentStatus", query = "SELECT s FROM Student s WHERE s.currentStatus = :currentStatus"),
//    @NamedQuery(name = "Student.findByStatus", query = "SELECT s FROM Student s WHERE s.status = :status")})
//public class Student implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Basic(optional = false)
//    @Column(name = "student_id")
//    private Integer studentId;
//
//    @Column(name = "admission_no")
//    private String admissionNo;
//
//    @Column(name = "form_no")
//    private String formNo;
//
//    @Temporal(TemporalType.DATE)
//    @Column(name = "admission_date")
//    private Date admissionDate;
//
//    @Column(name = "full_name")
//    private String fullName;
//
//    @Column(name = "nic")
//    private String nic;
//
//    @Temporal(TemporalType.DATE)
//    @Column(name = "dob")
//    private Date dob;
//
//    @Column(name = "gender")
//    private String gender;
//
//    @Column(name = "address")
//    private String address;
//
//    @Column(name = "contact_no")
//    private String contactNo;
//
//    @Column(name = "remarks")
//    private String remarks;
//
//    @Column(name = "medical_info")
//    private String medicalInfo;
//
//    @Column(name = "current_status")
//    private String currentStatus;
//
//    @Column(name = "status", nullable = false)
//    private Boolean status = true;
//
////    @OneToMany(
////            mappedBy = "student",
////            fetch = FetchType.LAZY,
////            cascade = CascadeType.ALL,
////            orphanRemoval = true
////    )
////    private List<StudentParents> studentParentsList;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "student_parents_id")
//    private StudentParents studentParents;
//    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
//    private List<CourseEnrollment> courseEnrollments;
//
//    public Student() {
//    }
//
//    public Integer getStudentId() {
//        return studentId;
//    }
//
//    public void setStudentId(Integer studentId) {
//        this.studentId = studentId;
//    }
//
//    public StudentParents getStudentParents() {
//        return studentParents;
//    }
//
//    public void setStudentParents(StudentParents studentParents) {
//        this.studentParents = studentParents;
//    }
//
//    public String getAdmissionNo() {
//        return admissionNo;
//    }
//
//    public void setAdmissionNo(String admissionNo) {
//        this.admissionNo = admissionNo;
//    }
//
//    public String getFormNo() {
//        return formNo;
//    }
//
//    public void setFormNo(String formNo) {
//        this.formNo = formNo;
//    }
//
//    public Date getAdmissionDate() {
//        return admissionDate;
//    }
//
//    public void setAdmissionDate(Date admissionDate) {
//        this.admissionDate = admissionDate;
//    }
//
//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public String getNic() {
//        return nic;
//    }
//
//    public void setNic(String nic) {
//        this.nic = nic;
//    }
//
//    public Date getDob() {
//        return dob;
//    }
//
//    public void setDob(Date dob) {
//        this.dob = dob;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getContactNo() {
//        return contactNo;
//    }
//
//    public void setContactNo(String contactNo) {
//        this.contactNo = contactNo;
//    }
//
//    public String getremarks() {
//        return remarks;
//    }
//
//    public void setremarks(String remarks) {
//        this.remarks = remarks;
//    }
//
//    public String getmedicalInfo() {
//        return medicalInfo;
//    }
//
//    public void setmedicalInfo(String medicalInfo) {
//        this.medicalInfo = medicalInfo;
//    }
//
//    public String getCurrentStatus() {
//        return currentStatus;
//    }
//
//    public void setCurrentStatus(String currentStatus) {
//        this.currentStatus = currentStatus;
//    }
//
//    public Boolean getStatus() {
//        return status;
//    }
//
//    public void setStatus(Boolean status) {
//        this.status = status;
//    }
//
//    @Override
//    public int hashCode() {
//        return studentId != null ? studentId.hashCode() : 0;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof Student)) {
//            return false;
//        }
//        Student other = (Student) obj;
//        return studentId != null && studentId.equals(other.studentId);
//    }
//
////    @Override
////    public String toString() {
////        return "Student[ studentId=" + studentId + " ]";
////    }
//    @Override
//    public String toString() {
//        return admissionNo + " - " + fullName;
//    }
//}
