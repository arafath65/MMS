package Entities.Student_Management;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "student_fee_cheque_details")
public class StudentFeeChequeDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_fee_cheque_details_id")
    private Integer studentFeeChequeDetailsId;

    @Column(name = "student_fee_installments_id")
    private Integer studentFeeInstallmentsId;
    
    @Column(name = "reference_id")
    private int referenceId;
    
    @Column(name = "reference_type")
    private String referenceType;
    
    @Column(name = "category")
    private String category;

    @Column(name = "cheque_no")
    private String chequeNo;

    @Column(name = "bank")
    private String bank;

    @Column(name = "branch")
    private String branch;

    @Temporal(TemporalType.DATE)
    @Column(name = "cheque_date")
    private Date chequeDate;

    @Column(name = "cheque_amount")
    private double chequeAmount;

    @Column(name = "cheque_status")
    private String chequeStatus;

    @Column(name = "status")
    private Integer status;

    // ===== GETTERS AND SETTERS =====
    public Integer getStudentFeeChequeDetailsId() {
        return studentFeeChequeDetailsId;
    }

    public void setStudentFeeChequeDetailsId(Integer studentFeeChequeDetailsId) {
        this.studentFeeChequeDetailsId = studentFeeChequeDetailsId;
    }

    public Integer getStudentFeeInstallmentsId() {
        return studentFeeInstallmentsId;
    }

    public void setStudentFeeInstallmentsId(Integer studentFeeInstallmentsId) {
        this.studentFeeInstallmentsId = studentFeeInstallmentsId;
    }
    
    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Date getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(Date chequeDate) {
        this.chequeDate = chequeDate;
    }

    public double getChequeAmount() {
        return chequeAmount;
    }

    public void setChequeAmount(double chequeAmount) {
        this.chequeAmount = chequeAmount;
    }

    public String getChequeStatus() {
        return chequeStatus;
    }

    public void setChequeStatus(String chequeStatus) {
        this.chequeStatus = chequeStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
