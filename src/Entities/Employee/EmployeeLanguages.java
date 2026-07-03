/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities.Employee;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "employee_languages")
public class EmployeeLanguages implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_languages_id")
    private Integer employeeLanguagesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "languages")
    private String languages;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified")
    private Date lastModified;

    @Column(name = "user")
    private String user;

    @Column(name = "status")
    private Integer status;

    public Integer getEmployeeLanguagesId() {
        return employeeLanguagesId;
    }

    public void setEmployeeLanguagesId(Integer employeeLanguagesId) {
        this.employeeLanguagesId = employeeLanguagesId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
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
    
    

    @Override
    public int hashCode() {
        return employeeLanguagesId != null
                ? employeeLanguagesId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof EmployeeLanguages)) {
            return false;
        }

        EmployeeLanguages other = (EmployeeLanguages) obj;

        return employeeLanguagesId != null
                && employeeLanguagesId.equals(other.employeeLanguagesId);
    }

    @Override
    public String toString() {
        return "EmployeeLanguages[ id=" + employeeLanguagesId + "]";
    }

}