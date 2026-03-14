package Entities.Student_Management;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "student")
//@NamedQueries({
//    @NamedQuery(name = "Student.findAll", query = "SELECT s FROM Student s")
//})
@NamedQueries({
    @NamedQuery(
            name = "Student.findAll",
            query = "SELECT s FROM Student s"
    ),

    @NamedQuery(
            name = "Student.findByAdmissionNo",
            query = "SELECT s FROM Student s WHERE s.admissionNo = :admissionNo AND s.currentStatus = :currentStatus"
    ),

    @NamedQuery(
            name = "Student.findByStatus",
            query = "SELECT s FROM Student s WHERE s.status = :status"
    ),

    @NamedQuery(
            name = "Student.findByCurrentStatus",
            query = "SELECT s FROM Student s WHERE s.currentStatus = :currentStatus"
    )
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
