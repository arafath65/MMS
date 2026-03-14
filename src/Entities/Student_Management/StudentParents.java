
package Entities.Student_Management;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "student_parents")
@NamedQueries({
    @NamedQuery(name = "StudentParents.findAll", query = "SELECT s FROM StudentParents s"),
    @NamedQuery(name = "StudentParents.findByStudentParentsId", query = "SELECT s FROM StudentParents s WHERE s.studentParentsId = :studentParentsId")
})
public class StudentParents implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_parents_id")
    private Integer studentParentsId;

    @Column(name = "mother_nic")
    private String motherNic;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "mother_occupation")
    private String motherOccupation;

    @Column(name = "mother_contact")
    private String motherContact;

    @Column(name = "mother_whatsapp")
    private String motherWhatsapp;

    @Column(name = "mother_living_with_child")
    private String motherLivingWithChild;

    @Column(name = "mother_reason")
    private String motherReason;

    @Column(name = "father_nic")
    private String fatherNic;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "father_occupation")
    private String fatherOccupation;

    @Column(name = "father_contact")
    private String fatherContact;

    @Column(name = "father_whatsapp")
    private String fatherWhatsapp;

    @Column(name = "father_living_with_child")
    private String fatherLivingWithChild;

    @Column(name = "father_reason")
    private String fatherReason;

    @Column(name = "guardian_nic")
    private String guardianNic;

    @Column(name = "guardian_name")
    private String guardianName;

    @Column(name = "guardian_relationship")
    private String guardianRelationship;

    @Column(name = "guardian_address")
    private String guardianAddress;

    @Column(name = "guardian_contact")
    private String guardianContact;

    @Column(name = "guardian_whatsapp")
    private String guardianWhatsapp;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @OneToMany(mappedBy = "studentParents", fetch = FetchType.LAZY)
    private List<Student> students;

    // Getters and Setters
    public Integer getStudentParentsId() { return studentParentsId; }
    public void setStudentParentsId(Integer studentParentsId) { this.studentParentsId = studentParentsId; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
    public String getMotherNic() { return motherNic; }
    public void setMotherNic(String motherNic) { this.motherNic = motherNic; }
    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }
    public String getMotherOccupation() { return motherOccupation; }
    public void setMotherOccupation(String motherOccupation) { this.motherOccupation = motherOccupation; }
    public String getMotherContact() { return motherContact; }
    public void setMotherContact(String motherContact) { this.motherContact = motherContact; }
    public String getMotherWhatsapp() { return motherWhatsapp; }
    public void setMotherWhatsapp(String motherWhatsapp) { this.motherWhatsapp = motherWhatsapp; }
    public String getMotherLivingWithChild() { return motherLivingWithChild; }
    public void setMotherLivingWithChild(String motherLivingWithChild) { this.motherLivingWithChild = motherLivingWithChild; }
    public String getMotherReason() { return motherReason; }
    public void setMotherReason(String motherReason) { this.motherReason = motherReason; }
    public String getFatherNic() { return fatherNic; }
    public void setFatherNic(String fatherNic) { this.fatherNic = fatherNic; }
    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }
    public String getFatherOccupation() { return fatherOccupation; }
    public void setFatherOccupation(String fatherOccupation) { this.fatherOccupation = fatherOccupation; }
    public String getFatherContact() { return fatherContact; }
    public void setFatherContact(String fatherContact) { this.fatherContact = fatherContact; }
    public String getFatherWhatsapp() { return fatherWhatsapp; }
    public void setFatherWhatsapp(String fatherWhatsapp) { this.fatherWhatsapp = fatherWhatsapp; }
    public String getFatherLivingWithChild() { return fatherLivingWithChild; }
    public void setFatherLivingWithChild(String fatherLivingWithChild) { this.fatherLivingWithChild = fatherLivingWithChild; }
    public String getFatherReason() { return fatherReason; }
    public void setFatherReason(String fatherReason) { this.fatherReason = fatherReason; }
    public String getGuardianNic() { return guardianNic; }
    public void setGuardianNic(String guardianNic) { this.guardianNic = guardianNic; }
    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
    public String getGuardianRelationship() { return guardianRelationship; }
    public void setGuardianRelationship(String guardianRelationship) { this.guardianRelationship = guardianRelationship; }
    public String getGuardianAddress() { return guardianAddress; }
    public void setGuardianAddress(String guardianAddress) { this.guardianAddress = guardianAddress; }
    public String getGuardianContact() { return guardianContact; }
    public void setGuardianContact(String guardianContact) { this.guardianContact = guardianContact; }
    public String getGuardianWhatsapp() { return guardianWhatsapp; }
    public void setGuardianWhatsapp(String guardianWhatsapp) { this.guardianWhatsapp = guardianWhatsapp; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    @Override
    public int hashCode() { return studentParentsId != null ? studentParentsId.hashCode() : 0; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StudentParents)) return false;
        StudentParents other = (StudentParents) obj;
        return studentParentsId != null && studentParentsId.equals(other.studentParentsId);
    }

    @Override
    public String toString() { return "StudentParents[ studentParentsId=" + studentParentsId + " ]"; }

    public StudentParents() {
    }
}
//package Entities.Student_Management;
//
//import java.io.Serializable;
//import java.util.List;
//import javax.persistence.Basic;
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
//
///**
// *
// * @author UNKNOWN_UN
// */
//@Entity
//@Table(name = "student_parents")
//@NamedQueries({
//    @NamedQuery(name = "StudentParents.findAll", query = "SELECT s FROM StudentParents s"),
//    @NamedQuery(name = "StudentParents.findByStudentParentsId", query = "SELECT s FROM StudentParents s WHERE s.studentParentsId = :studentParentsId"),
//    @NamedQuery(name = "StudentParents.findByMotherNic", query = "SELECT s FROM StudentParents s WHERE s.motherNic = :motherNic"),
//    @NamedQuery(name = "StudentParents.findByMotherName", query = "SELECT s FROM StudentParents s WHERE s.motherName = :motherName"),
//    @NamedQuery(name = "StudentParents.findByMotherOccupation", query = "SELECT s FROM StudentParents s WHERE s.motherOccupation = :motherOccupation"),
//    @NamedQuery(name = "StudentParents.findByMotherContact", query = "SELECT s FROM StudentParents s WHERE s.motherContact = :motherContact"),
//    @NamedQuery(name = "StudentParents.findByMotherWhatsapp", query = "SELECT s FROM StudentParents s WHERE s.motherWhatsapp = :motherWhatsapp"),
//    @NamedQuery(name = "StudentParents.findByMotherLivingWithChild", query = "SELECT s FROM StudentParents s WHERE s.motherLivingWithChild = :motherLivingWithChild"),
//    @NamedQuery(name = "StudentParents.findByMotherReason", query = "SELECT s FROM StudentParents s WHERE s.motherReason = :motherReason"),
//    @NamedQuery(name = "StudentParents.findByFatherNic", query = "SELECT s FROM StudentParents s WHERE s.fatherNic = :fatherNic"),
//    @NamedQuery(name = "StudentParents.findByFatherName", query = "SELECT s FROM StudentParents s WHERE s.fatherName = :fatherName"),
//    @NamedQuery(name = "StudentParents.findByFatherOccupation", query = "SELECT s FROM StudentParents s WHERE s.fatherOccupation = :fatherOccupation"),
//    @NamedQuery(name = "StudentParents.findByFatherContact", query = "SELECT s FROM StudentParents s WHERE s.fatherContact = :fatherContact"),
//    @NamedQuery(name = "StudentParents.findByFatherWhatsapp", query = "SELECT s FROM StudentParents s WHERE s.fatherWhatsapp = :fatherWhatsapp"),
//    @NamedQuery(name = "StudentParents.findByFatherLivingWithChild", query = "SELECT s FROM StudentParents s WHERE s.fatherLivingWithChild = :fatherLivingWithChild"),
//    @NamedQuery(name = "StudentParents.findByFatherReason", query = "SELECT s FROM StudentParents s WHERE s.fatherReason = :fatherReason"),
//    @NamedQuery(name = "StudentParents.findByGuardianNic", query = "SELECT s FROM StudentParents s WHERE s.guardianNic = :guardianNic"),
//    @NamedQuery(name = "StudentParents.findByGuardianName", query = "SELECT s FROM StudentParents s WHERE s.guardianName = :guardianName"),
//    @NamedQuery(name = "StudentParents.findByGuardianRelationship", query = "SELECT s FROM StudentParents s WHERE s.guardianRelationship = :guardianRelationship"),
//    @NamedQuery(name = "StudentParents.findByGuardianAddress", query = "SELECT s FROM StudentParents s WHERE s.guardianAddress = :guardianAddress"),
//    @NamedQuery(name = "StudentParents.findByGuardianContact", query = "SELECT s FROM StudentParents s WHERE s.guardianContact = :guardianContact"),
//    @NamedQuery(name = "StudentParents.findByGuardianWhatsapp", query = "SELECT s FROM StudentParents s WHERE s.guardianWhatsapp = :guardianWhatsapp"),
//    @NamedQuery(name = "StudentParents.findByStatus", query = "SELECT s FROM StudentParents s WHERE s.status = :status")})
//public class StudentParents implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Basic(optional = false)
//    @Column(name = "student_parents_id")
//    private Integer studentParentsId;
//
//    @Column(name = "mother_nic")
//    private String motherNic;
//
//    @Column(name = "mother_name")
//    private String motherName;
//
//    @Column(name = "mother_occupation")
//    private String motherOccupation;
//
//    @Column(name = "mother_contact")
//    private String motherContact;
//
//    @Column(name = "mother_whatsapp")
//    private String motherWhatsapp;
//
//    @Column(name = "mother_living_with_child")
//    private String motherLivingWithChild;
//
//    @Column(name = "mother_reason")
//    private String motherReason;
//
//    @Column(name = "father_nic")
//    private String fatherNic;
//
//    @Column(name = "father_name")
//    private String fatherName;
//
//    @Column(name = "father_occupation")
//    private String fatherOccupation;
//
//    @Column(name = "father_contact")
//    private String fatherContact;
//
//    @Column(name = "father_whatsapp")
//    private String fatherWhatsapp;
//
//    @Column(name = "father_living_with_child")
//    private String fatherLivingWithChild;
//
//    @Column(name = "father_reason")
//    private String fatherReason;
//
//    @Column(name = "guardian_nic")
//    private String guardianNic;
//
//    @Column(name = "guardian_name")
//    private String guardianName;
//
//    @Column(name = "guardian_relationship")
//    private String guardianRelationship;
//
//    @Column(name = "guardian_address")
//    private String guardianAddress;
//
//    @Column(name = "guardian_contact")
//    private String guardianContact;
//
//    @Column(name = "guardian_whatsapp")
//    private String guardianWhatsapp;
//
//    @Column(name = "status", nullable = false)
//    private Boolean status = true;
//
////    @ManyToOne(fetch = FetchType.LAZY)
////    @JoinColumn(name = "student_id", nullable = false)
////    private Student student;
//    @OneToMany(mappedBy = "studentParents", fetch = FetchType.LAZY)
//    private List<Student> students;
//
//    public StudentParents() {
//    }
//
//    public Integer getStudentParentsId() {
//        return studentParentsId;
//    }
//
//    public void setStudentParentsId(Integer studentParentsId) {
//        this.studentParentsId = studentParentsId;
//    }
//
//    public List<Student> getStudents() {
//        return students;
//    }
//
//    public void setStudents(List<Student> students) {
//        this.students = students;
//    }
//
////    public Student getStudent() {
////        return student;
////    }
////
////    public void setStudent(Student student) {
////        this.student = student;
////    }
//    // Inside StudentParents class, add these:
//    public String getMotherNic() {
//        return motherNic;
//    }
//
//    public void setMotherNic(String motherNic) {
//        this.motherNic = motherNic;
//    }
//
//    public String getMotherName() {
//        return motherName;
//    }
//
//    public void setMotherName(String motherName) {
//        this.motherName = motherName;
//    }
//
//    public String getMotherOccupation() {
//        return motherOccupation;
//    }
//
//    public void setMotherOccupation(String motherOccupation) {
//        this.motherOccupation = motherOccupation;
//    }
//
//    public String getMotherContact() {
//        return motherContact;
//    }
//
//    public void setMotherContact(String motherContact) {
//        this.motherContact = motherContact;
//    }
//
//    public String getMotherWhatsapp() {
//        return motherWhatsapp;
//    }
//
//    public void setMotherWhatsapp(String motherWhatsapp) {
//        this.motherWhatsapp = motherWhatsapp;
//    }
//
//    public String getMotherLivingWithChild() {
//        return motherLivingWithChild;
//    }
//
//    public void setMotherLivingWithChild(String motherLivingWithChild) {
//        this.motherLivingWithChild = motherLivingWithChild;
//    }
//
//    public String getMotherReason() {
//        return motherReason;
//    }
//
//    public void setMotherReason(String motherReason) {
//        this.motherReason = motherReason;
//    }
//
//    public String getFatherNic() {
//        return fatherNic;
//    }
//
//    public void setFatherNic(String fatherNic) {
//        this.fatherNic = fatherNic;
//    }
//
//    public String getFatherName() {
//        return fatherName;
//    }
//
//    public void setFatherName(String fatherName) {
//        this.fatherName = fatherName;
//    }
//
//    public String getFatherOccupation() {
//        return fatherOccupation;
//    }
//
//    public void setFatherOccupation(String fatherOccupation) {
//        this.fatherOccupation = fatherOccupation;
//    }
//
//    public String getFatherContact() {
//        return fatherContact;
//    }
//
//    public void setFatherContact(String fatherContact) {
//        this.fatherContact = fatherContact;
//    }
//
//    public String getFatherWhatsapp() {
//        return fatherWhatsapp;
//    }
//
//    public void setFatherWhatsapp(String fatherWhatsapp) {
//        this.fatherWhatsapp = fatherWhatsapp;
//    }
//
//    public String getFatherLivingWithChild() {
//        return fatherLivingWithChild;
//    }
//
//    public void setFatherLivingWithChild(String fatherLivingWithChild) {
//        this.fatherLivingWithChild = fatherLivingWithChild;
//    }
//
//    public String getFatherReason() {
//        return fatherReason;
//    }
//
//    public void setFatherReason(String fatherReason) {
//        this.fatherReason = fatherReason;
//    }
//
//    public String getGuardianNic() {
//        return guardianNic;
//    }
//
//    public void setGuardianNic(String guardianNic) {
//        this.guardianNic = guardianNic;
//    }
//
//    public String getGuardianName() {
//        return guardianName;
//    }
//
//    public void setGuardianName(String guardianName) {
//        this.guardianName = guardianName;
//    }
//
//    public String getGuardianRelationship() {
//        return guardianRelationship;
//    }
//
//    public void setGuardianRelationship(String guardianRelationship) {
//        this.guardianRelationship = guardianRelationship;
//    }
//
//    public String getGuardianAddress() {
//        return guardianAddress;
//    }
//
//    public void setGuardianAddress(String guardianAddress) {
//        this.guardianAddress = guardianAddress;
//    }
//
//    public String getGuardianContact() {
//        return guardianContact;
//    }
//
//    public void setGuardianContact(String guardianContact) {
//        this.guardianContact = guardianContact;
//    }
//
//    public String getGuardianWhatsapp() {
//        return guardianWhatsapp;
//    }
//
//    public void setGuardianWhatsapp(String guardianWhatsapp) {
//        this.guardianWhatsapp = guardianWhatsapp;
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
//        return studentParentsId != null ? studentParentsId.hashCode() : 0;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof StudentParents)) {
//            return false;
//        }
//        StudentParents other = (StudentParents) obj;
//        return studentParentsId != null && studentParentsId.equals(other.studentParentsId);
//    }
//
//    @Override
//    public String toString() {
//        return "StudentParents[ studentParentsId=" + studentParentsId + " ]";
//    }
//}
