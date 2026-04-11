/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entities.Inventory;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "suppliers")

@NamedQueries({
    @NamedQuery(
            name = "Supplier.findAll",
            query = "SELECT s FROM Supplier s WHERE s.status = 1"
    ),

    @NamedQuery(
            name = "Supplier.search",
            query = "SELECT s FROM Supplier s WHERE s.status = 1 AND s.supplierName LIKE :name"
    ),

    @NamedQuery(
            name = "Supplier.findByName",
            query = "SELECT s FROM Supplier s WHERE s.supplierName LIKE :supplierName AND s.status = :status"
    ),

    @NamedQuery(
            name = "Supplier.findById",
            query = "SELECT s FROM Supplier s WHERE s.suppliersId = :id"
    ),

    @NamedQuery(
            name = "Supplier.findByStatus",
            query = "SELECT s FROM Supplier s WHERE s.status = :status"
    )
})
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suppliers_id")
    private Integer suppliersId;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "company")
    private String company;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "user")
    private String user;

    @Column(name = "status")
    private int status;

    // ------------------------
    // Getters & Setters
    // ------------------------
    public Integer getSuppliersId() {
        return suppliersId;
    }

    public void setSuppliersId(Integer suppliersId) {
        this.suppliersId = suppliersId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // ------------------------
    // Override Methods
    // ------------------------
    @Override
    public int hashCode() {
        return suppliersId != null ? suppliersId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Supplier)) {
            return false;
        }
        Supplier other = (Supplier) obj;
        return suppliersId != null && suppliersId.equals(other.suppliersId);
    }

    @Override
    public String toString() {
        return supplierName;
    }
}
