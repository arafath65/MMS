
package Entities.Student_Management;

import java.util.Date;

public class StudentAdditionalFeePayment {

    private int studentAdditionalFeePaymentsId;
    private int studentAdditionalFeesId;
    private int roundMasterId;
    private Date paidDate;
    private double amountPaid;
    private String paymentMethod;
    private String user;
    private int status;

    public int getStudentAdditionalFeePaymentsId() {
        return studentAdditionalFeePaymentsId;
    }

    public void setStudentAdditionalFeePaymentsId(int studentAdditionalFeePaymentsId) {
        this.studentAdditionalFeePaymentsId = studentAdditionalFeePaymentsId;
    }

    public int getStudentAdditionalFeesId() {
        return studentAdditionalFeesId;
    }

    public void setStudentAdditionalFeesId(int studentAdditionalFeesId) {
        this.studentAdditionalFeesId = studentAdditionalFeesId;
    }
    
    public int getRoundMasterId() {
        return roundMasterId;
    }

    public void setRoundMasterId(int roundMasterId) {
        this.roundMasterId = roundMasterId;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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
}