package JPA_DAO.Student_Management;

import Classes.HibernateConfig;
import Entities.Student_Management.StudentFeeInstallments;
import Entities.Student_Management.StudentFeePayments;
import Entities.Student_Management.StudentFeeRoundPaymentMaster;
import Entities.Student_Management.StudentFeeRoundPaymentMasterDetails;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.swing.JOptionPane;

public class StudentFeeInstallmentsDAO {

    public List<Object[]> getInstallments(int enrollmentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        List<Object[]> list = em.createQuery(
                "SELECT i.installmentNo, i.paymentDate, i.amountPaid "
                + "FROM StudentFeeInstallments i "
                + "WHERE i.enrollmentId = :enrollmentId "
                + "AND i.status = true "
                + "ORDER BY i.installmentNo ASC",
                Object[].class
        )
                .setParameter("enrollmentId", enrollmentId)
                .getResultList();

        em.close();
        return list;
    }

    // Get next installment number
    public int getNextInstallmentNo(int paymentId) {
        EntityManager em = HibernateConfig.getEntityManager();

        Integer maxNo = em.createQuery(
                "SELECT MAX(i.installmentNo) FROM StudentFeeInstallments i WHERE i.studentFeePaymentsId = :paymentId",
                Integer.class
        ).setParameter("paymentId", paymentId)
                .getSingleResult();

        em.close();
        return (maxNo == null) ? 1 : maxNo + 1;
    }

    public int getLastInstallmentNo(int paymentId) {
        EntityManager em = HibernateConfig.getEntityManager();

        Integer maxNo = em.createQuery(
                "SELECT MAX(i.installmentNo) FROM StudentFeeInstallments i WHERE i.studentFeePaymentsId = :paymentId",
                Integer.class
        ).setParameter("paymentId", paymentId)
                .getSingleResult();

        em.close();

        return (maxNo == null) ? 1 : maxNo;
    }

    // Check duplicate payment for same date & amount
    public boolean isDuplicatePayment(int paymentId, Date paymentDate, int amount) {
        EntityManager em = HibernateConfig.getEntityManager();

        Long count = em.createQuery(
                "SELECT COUNT(i) FROM StudentFeeInstallments i "
                + "WHERE i.studentFeePaymentsId = :paymentId "
                + "AND i.paymentDate = :paymentDate "
                + "AND i.amountPaid = :amount "
                + "AND i.status = true",
                Long.class
        ).setParameter("paymentId", paymentId)
                .setParameter("paymentDate", paymentDate)
                .setParameter("amount", amount)
                .getSingleResult();

        em.close();
        return count > 0;
    }

    // Save installment and update main payment
    public void saveInstallment(int studentId, int enrollmentId, int amountPaid, Date paymentDate, String paymentMethod, String monthFor, String user) {
        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Fetch StudentFeePayments record for this student & enrollment
            TypedQuery<StudentFeePayments> query = em.createQuery(
                    "SELECT p FROM StudentFeePayments p "
                    + "WHERE p.student.studentId = :studentId "
                    + "AND p.enrollment.enrollmentId = :enrollmentId "
                    + "AND p.status = true",
                    StudentFeePayments.class
            );
            query.setParameter("studentId", studentId);
            query.setParameter("enrollmentId", enrollmentId);

            StudentFeePayments payment = query.getSingleResult();
            int paymentId = payment.getStudentFeePaymentsId();

            // Check duplicate
            if (isDuplicatePayment(paymentId, paymentDate, amountPaid)) {
                JOptionPane.showMessageDialog(null, "Duplicate payment detected.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Next installment number
            int nextInstallment = getNextInstallmentNo(paymentId);

            // Create installment
            StudentFeeInstallments ins = new StudentFeeInstallments();
            ins.setStudentFeePaymentsId(paymentId);
            ins.setEnrollmentId(enrollmentId);
            ins.setInstallmentNo(nextInstallment);
            ins.setAmountPaid(amountPaid);
            ins.setPaymentDate(paymentDate);
            ins.setPaymentMethod(paymentMethod);
            ins.setMonthFor(monthFor);
            ins.setStatus(1);

            em.persist(ins);

            // Update main payment totals
            int newPaid = payment.getTotalPaid() + amountPaid;
            int newBalance = payment.getTotalFee() - newPaid;

            payment.setTotalPaid(newPaid);
            payment.setTotalBalance(newBalance);
            payment.setPaymentType("FULL"); // always mark as installment
            payment.setLastMofidied(new Date());

            // Update payment status
            if (newBalance == 0) {
                payment.setPaymentStatus("COMPLETE");
            } else {
                payment.setPaymentStatus("ACTIVE");
            }

            em.merge(payment);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public int getPaymentIdByStudentAndEnrollment(int studentId, int enrollmentId) {
        EntityManager em = HibernateConfig.getEntityManager();
        StudentFeePayments payment = em.createQuery(
                "SELECT p FROM StudentFeePayments p "
                + "WHERE p.student.studentId = :studentId "
                + "AND p.enrollment.enrollmentId = :enrollmentId "
                + "AND p.status = true", StudentFeePayments.class)
                .setParameter("studentId", studentId)
                .setParameter("enrollmentId", enrollmentId)
                .getSingleResult();
        em.close();
        return payment.getStudentFeePaymentsId();
    }

    public void processRoundPayment(int studentId, int startEnrollmentId, int paidAmount,
            Date paymentDate, String paymentMethod, String user) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            int remainingAmount = paidAmount;

            // =====================================================
            // CREATE ROUND PAYMENT MASTER
            // =====================================================
            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
            master.setStudentId(studentId);
            master.setPaymentDate(paymentDate);
            master.setPaymentMode(paymentMethod);
            master.setTotalPaid(paidAmount);
            master.setRoundingAdjustment(0);
            master.setRemarks("ROUND PAYMENT");
            master.setUser(user);
            master.setStatus(1);

            em.persist(master);
            em.flush();

            // =====================================================
            // 1️⃣ PAY SELECTED COURSE FIRST
            // =====================================================
            StudentFeePayments selectedPayment = em.createQuery(
                    "SELECT p FROM StudentFeePayments p "
                    + "WHERE p.student.studentId = :sid "
                    + "AND p.enrollment.enrollmentId = :eid "
                    + "AND p.status = true",
                    StudentFeePayments.class)
                    .setParameter("sid", studentId)
                    .setParameter("eid", startEnrollmentId)
                    .getSingleResult();

            int selectedBalance = selectedPayment.getTotalBalance();
            int deductSelected = Math.min(selectedBalance, remainingAmount);

            selectedPayment.setTotalPaid(selectedPayment.getTotalPaid() + deductSelected);
            selectedPayment.setTotalBalance(selectedPayment.getTotalBalance() - deductSelected);
            selectedPayment.setPaymentType("ROUND");

            if (selectedPayment.getTotalBalance() <= 0) {
                selectedPayment.setPaymentStatus("COMPLETE");
            }

            em.merge(selectedPayment);

            // ROUND DETAIL
            StudentFeeRoundPaymentMasterDetails detailFirst = new StudentFeeRoundPaymentMasterDetails();
            detailFirst.setStudentFeeRoundPaymentMasterId(master);
            detailFirst.setEnrollmentId(startEnrollmentId);
            detailFirst.setPaidAmount(deductSelected);
            detailFirst.setStatus(1);

            em.persist(detailFirst);

            // INSTALLMENT RECORD
            StudentFeeInstallments installmentFirst = new StudentFeeInstallments();
            installmentFirst.setStudentFeePaymentsId(selectedPayment.getStudentFeePaymentsId());
            installmentFirst.setEnrollmentId(selectedPayment.getEnrollment().getEnrollmentId());
            installmentFirst.setAmountPaid(deductSelected);
            installmentFirst.setPaymentDate(paymentDate);
            installmentFirst.setPaymentMethod(paymentMethod);
            installmentFirst.setPaymentType("ROUND");
            installmentFirst.setInstallmentNo(getNextInstallmentNo(selectedPayment.getStudentFeePaymentsId()));
            installmentFirst.setStatus(1);

            em.persist(installmentFirst);

            remainingAmount -= deductSelected;

            // =====================================================
            // 2️⃣ PAY OTHER COURSES (OLDEST FIRST)
            // =====================================================
            List<StudentFeePayments> payments = em.createQuery(
                    "SELECT p FROM StudentFeePayments p "
                    + "WHERE p.student.studentId = :sid "
                    + "AND p.status = true "
                    + "AND p.totalBalance > 0 "
                    + "AND p.enrollment.enrollmentId <> :eid "
                    + "ORDER BY p.createdAt ASC",
                    StudentFeePayments.class)
                    .setParameter("sid", studentId)
                    .setParameter("eid", startEnrollmentId)
                    .getResultList();

            for (StudentFeePayments payment : payments) {

                if (remainingAmount <= 0) {
                    break;
                }

                int balance = payment.getTotalBalance();
                int deductAmount = Math.min(balance, remainingAmount);

                payment.setTotalPaid(payment.getTotalPaid() + deductAmount);
                payment.setTotalBalance(payment.getTotalBalance() - deductAmount);
                payment.setPaymentType("ROUND");

                if (payment.getTotalBalance() <= 0) {
                    payment.setPaymentStatus("COMPLETE");
                }

                em.merge(payment);

                // ROUND DETAIL
                StudentFeeRoundPaymentMasterDetails detail = new StudentFeeRoundPaymentMasterDetails();
                detail.setStudentFeeRoundPaymentMasterId(master);
                detail.setEnrollmentId(payment.getEnrollment().getEnrollmentId());
                detail.setPaidAmount(deductAmount);
                detail.setStatus(1);

                em.persist(detail);

                // INSTALLMENT RECORD
                StudentFeeInstallments installment = new StudentFeeInstallments();
                installment.setStudentFeePaymentsId(payment.getStudentFeePaymentsId());
                installment.setEnrollmentId(payment.getEnrollment().getEnrollmentId());
                installment.setAmountPaid(deductAmount);
                installment.setPaymentDate(paymentDate);
                installment.setPaymentMethod(paymentMethod);
                installment.setPaymentType("ROUND");
                installment.setInstallmentNo(getNextInstallmentNo(payment.getStudentFeePaymentsId()));
                installment.setStatus(1);

                em.persist(installment);

                remainingAmount -= deductAmount;
            }

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            e.printStackTrace();

        } finally {

            em.close();
        }
    }

//    public void processRoundPayment(int studentId, int startEnrollmentId, int paidAmount,
//            Date paymentDate, String paymentMethod, String user) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//
//            tx.begin();
//
//            int remainingAmount = paidAmount;
//
//            // =====================================================
//            // CREATE ROUND PAYMENT MASTER
//            // =====================================================
//            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
//            master.setStudentId(studentId);
//            master.setPaymentDate(paymentDate);
//            master.setPaymentMode(paymentMethod);
//            master.setTotalPaid(paidAmount);
//            master.setRoundingAdjustment(0);
//            master.setRemarks("ROUND PAYMENT");
//            master.setUser(user);
//            master.setStatus(1);
//
//            em.persist(master);
//            em.flush();
//
//            // =====================================================
//            // 1️⃣ PAY SELECTED COURSE FIRST
//            // =====================================================
//            StudentFeePayments selectedPayment = em.createQuery(
//                    "SELECT p FROM StudentFeePayments p "
//                    + "WHERE p.student.studentId = :sid "
//                    + "AND p.enrollment.enrollmentId = :eid "
//                    + "AND p.status = true",
//                    StudentFeePayments.class)
//                    .setParameter("sid", studentId)
//                    .setParameter("eid", startEnrollmentId)
//                    .getSingleResult();
//
//            int selectedBalance = selectedPayment.getTotalBalance();
//
//            int deductSelected = Math.min(selectedBalance, remainingAmount);
//
//            // UPDATE MAIN PAYMENT TABLE
//            selectedPayment.setTotalPaid(selectedPayment.getTotalPaid() + deductSelected);
//            selectedPayment.setTotalBalance(selectedPayment.getTotalBalance() - deductSelected);
//            selectedPayment.setPaymentType("ROUND");
//
//            if (selectedPayment.getTotalBalance() <= 0) {
//                selectedPayment.setPaymentStatus("COMPLETE");
//            }
//
//            em.merge(selectedPayment);
//
//            // SAVE ROUND PAYMENT DETAIL
//            StudentFeeRoundPaymentMasterDetails detailFirst = new StudentFeeRoundPaymentMasterDetails();
//            detailFirst.setStudentFeeRoundPaymentMasterId(master);
//            detailFirst.setEnrollmentId(startEnrollmentId);
//            detailFirst.setPaidAmount(deductSelected);
//            detailFirst.setStatus(1);
//
//            em.persist(detailFirst);
//
//            // SAVE INSTALLMENT RECORD
//            StudentFeeInstallments installmentFirst = new StudentFeeInstallments();
//            installmentFirst.setStudentFeePaymentsId(selectedPayment.getStudentFeePaymentsId());
//            installmentFirst.setEnrollmentId(selectedPayment.getEnrollment().getEnrollmentId());
//            installmentFirst.setAmountPaid(deductSelected);
//            installmentFirst.setPaymentDate(paymentDate);
//            installmentFirst.setPaymentMethod(paymentMethod);
//            installmentFirst.setInstallmentNo(getNextInstallmentNo(selectedPayment.getStudentFeePaymentsId()));
//            installmentFirst.setStatus(1);
//

////            installmentFirst.setStudentFeePaymentsId(selectedPayment.getStudentFeePaymentsId());
////            installmentFirst.setEnrollmentId(startEnrollmentId);
////            installmentFirst.setAmountPaid(deductSelected);
////            installmentFirst.setPaymentDate(paymentDate);
////            installmentFirst.setPaymentMethod(paymentMethod);
////            installmentFirst.setInstallmentNo(getNextInstallmentNo(selectedPayment.getStudentFeePaymentsId()));
////            installmentFirst.setStatus(1);
////
////            em.persist(installmentFirst);
//            remainingAmount -= deductSelected;
//
//            // =====================================================
//            // 2️⃣ PAY OTHER COURSES (OLDEST FIRST)
//            // =====================================================
//            List<StudentFeePayments> payments = em.createQuery(
//                    "SELECT p FROM StudentFeePayments p "
//                    + "WHERE p.student.studentId = :sid "
//                    + "AND p.status = true "
//                    + "AND p.totalBalance > 0 "
//                    + "AND p.enrollment.enrollmentId <> :eid "
//                    + "ORDER BY p.createdAt ASC",
//                    StudentFeePayments.class)
//                    .setParameter("sid", studentId)
//                    .setParameter("eid", startEnrollmentId)
//                    .getResultList();
//
//            for (StudentFeePayments payment : payments) {
//
//                if (remainingAmount <= 0) {
//                    break;
//                }
//
//                int balance = payment.getTotalBalance();
//
//                int deductAmount = Math.min(balance, remainingAmount);
//
//                // UPDATE PAYMENT TABLE
//                payment.setTotalPaid(payment.getTotalPaid() + deductAmount);
//                payment.setTotalBalance(payment.getTotalBalance() - deductAmount);
//                payment.setPaymentType("ROUND");
//
//                if (payment.getTotalBalance() <= 0) {
//                    payment.setPaymentStatus("COMPLETE");
//                }
//
//                em.merge(payment);
//
//                // SAVE ROUND PAYMENT DETAIL
//                StudentFeeRoundPaymentMasterDetails detail = new StudentFeeRoundPaymentMasterDetails();
//                detail.setStudentFeeRoundPaymentMasterId(master);
//                detail.setEnrollmentId(payment.getEnrollment().getEnrollmentId());
//                detail.setPaidAmount(deductAmount);
//                detail.setStatus(1);
//
//                em.persist(detail);
//
//                // SAVE INSTALLMENT RECORD
//                StudentFeeInstallments installment = new StudentFeeInstallments();
////                installment.setStudentFeePaymentsId(selectedPayment.getStudentFeePaymentsId());
////                installment.setEnrollmentId(startEnrollmentId);
////                installment.setAmountPaid(deductSelected);
////                installment.setPaymentDate(paymentDate);
////                installment.setPaymentMethod(paymentMethod);
////                installment.setInstallmentNo(getNextInstallmentNo(selectedPayment.getStudentFeePaymentsId()));
//                installment.setStudentFeePaymentsId(selectedPayment.getStudentFeePaymentsId());
//                installment.setEnrollmentId(selectedPayment.getEnrollment().getEnrollmentId());
//                installment.setAmountPaid(deductSelected);
//                installment.setPaymentDate(paymentDate);
//                installment.setPaymentMethod(paymentMethod);
//                installment.setInstallmentNo(getNextInstallmentNo(selectedPayment.getStudentFeePaymentsId()));
//                installment.setStatus(1);
//
//                em.persist(installment);
//
//                remainingAmount -= deductAmount;
//            }
//
//            tx.commit();
//
//        } catch (Exception e) {
//
//            if (tx.isActive()) {
//                tx.rollback();
//            }
//
//            e.printStackTrace();
//
//        } finally {
//
//            em.close();
//        }
//    }


}
