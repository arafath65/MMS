/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
@Table(name = "student_class")
@NamedQueries({
    @NamedQuery(name = "StudentClass.findAll", query = "SELECT s FROM StudentClass s"),
    @NamedQuery(name = "StudentClass.findByClassId", query = "SELECT s FROM StudentClass s WHERE s.classId = :classId"),
    @NamedQuery(name = "StudentClass.findByClassName", query = "SELECT s FROM StudentClass s WHERE s.className = :className"),
    @NamedQuery(name = "StudentClass.findByStatus", query = "SELECT s FROM StudentClass s WHERE s.status = :status")})
public class StudentClass implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "class_id")
    private Integer classId;
    @Column(name = "class_name")
    private String className;
    @Column(name = "status")
    private Boolean status;

    public StudentClass() {
    }

    public StudentClass(Integer classId) {
        this.classId = classId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (classId != null ? classId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudentClass)) {
            return false;
        }
        StudentClass other = (StudentClass) object;
        if ((this.classId == null && other.classId != null) || (this.classId != null && !this.classId.equals(other.classId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return className;   // VERY IMPORTANT
    }

//    @Override
//    public String toString() {
//        return "Entities.Settings.StudentClass[ classId=" + classId + " ]";
//    }
}
