package Entities.Student_Management;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "student")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_parents_id")
    private StudentParents studentParents;

    @OneToOne(mappedBy = "student",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private StudentLanguages studentLanguages;

    @Column(name = "admission_no")
    private String admissionNo;

    @Column(name = "form_no")
    private String formNo;

    @Temporal(TemporalType.DATE)
    @Column(name = "admission_date")
    private Date admissionDate;

    @Column(name = "bc_no")
    private String bcNo;

    @Column(name = "name_initials")
    private String nameInitials;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "nic")
    private String nic;

    @Column(name = "Gender")
    private String gender;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    private Date dob;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "religion")
    private String religion;

    @Column(name = "district")
    private String district;

    @Column(name = "area")
    private String area;

    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "email")
    private String email;

    @Column(name = "medium")
    private String medium;

    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "permanent_address")
    private String permanentAddress;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "medical_info")
    private String medicalInfo;

    @Column(name = "current_status")
    private String currentStatus;

    @Column(name = "status")
    private Boolean status = true;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<CourseEnrollment> courseEnrollments;

    // =========================
    // GETTERS & SETTERS
    // =========================
    public StudentLanguages getStudentLanguages() {
        return studentLanguages;
    }

    public void setStudentLanguages(StudentLanguages studentLanguages) {
        this.studentLanguages = studentLanguages;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public StudentParents getStudentParents() {
        return studentParents;
    }

    public void setStudentParents(StudentParents studentParents) {
        this.studentParents = studentParents;
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

    public String getBcNo() {
        return bcNo;
    }

    public void setBcNo(String bcNo) {
        this.bcNo = bcNo;
    }

    public String getNameInitials() {
        return nameInitials;
    }

    public void setNameInitials(String nameInitials) {
        this.nameInitials = nameInitials;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
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
