package Entities.Student_Management;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "course_enrollment")
public class CourseEnrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Integer enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany(mappedBy = "enrollment", fetch = FetchType.LAZY)
    private List<StudentFeePayments> payments;

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StudentFeePayments feePayments;

    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "class_name")
    private String className;

    @Column(name = "admission_fee")
    private Integer admissionFee;

    @Column(name = "fee")
    private Integer fee;

    @Column(name = "course_status")
    private String courseStatus;

    @Column(name = "status")
    private int status;

    // Getters and Setters
    public Integer getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Integer enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public StudentFeePayments getFeePayments() {
        return feePayments;
    }

    public void setFeePayments(StudentFeePayments feePayments) {
        this.feePayments = feePayments;
    }

    @Override
    public int hashCode() {
        return enrollmentId != null ? enrollmentId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CourseEnrollment)) {
            return false;
        }
        CourseEnrollment other = (CourseEnrollment) obj;
        return enrollmentId != null && enrollmentId.equals(other.enrollmentId);
    }

    @Override
    public String toString() {
        return "CourseEnrollment[ enrollmentId=" + enrollmentId + " ]";
    }
}
