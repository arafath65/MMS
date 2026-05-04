package Entities.Student_Management;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "student_eliminates")
public class StudentEliminates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_eliminates_id")
    private Integer studentEliminatesId;

    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "eliminate_type")
    private String eliminateType;

    @Column(name = "note")
    private String note;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "eliminate_date")
    private Date eliminateDate;

    @Column(name = "user")
    private String user;

    @Column(name = "status")
    private Integer status;

    public Integer getStudentEliminatesId() {
        return studentEliminatesId;
    }

    public void setStudentEliminatesId(Integer studentEliminatesId) {
        this.studentEliminatesId = studentEliminatesId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getEliminateType() {
        return eliminateType;
    }

    public void setEliminateType(String eliminateType) {
        this.eliminateType = eliminateType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getEliminateDate() {
        return eliminateDate;
    }

    public void setEliminateDate(Date eliminateDate) {
        this.eliminateDate = eliminateDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
