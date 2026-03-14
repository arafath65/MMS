/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Additional;

import javax.persistence.*;

@Entity
@Table(name = "bank_names_srilanka")
public class BankNamesSriLanka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_names_srilanka_id")
    private Integer bankNamesSriLankaId;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "bank_names")
    private String bankNames;

    @Column(name = "status")
    private Integer status;

    // ===== Getters and Setters =====

    public Integer getBankNamesSriLankaId() {
        return bankNamesSriLankaId;
    }

    public void setBankNamesSriLankaId(Integer bankNamesSriLankaId) {
        this.bankNamesSriLankaId = bankNamesSriLankaId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankNames() {
        return bankNames;
    }

    public void setBankNames(String bankNames) {
        this.bankNames = bankNames;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}