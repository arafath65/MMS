package JPA_DAO.Student_Management;

import Classes.HibernateConfig;
import Entities.Student_Management.StudentFeeChequeDetails;
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

//    public List<Object[]> getInstallments(int enrollmentId) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        List<Object[]> list = em.createQuery(
//                "SELECT i.installmentNo, i.paymentDate, i.amountPaid, i.paymentMethod, c.chequeStatus "
//                + "FROM StudentFeeInstallments i "
//                + "LEFT JOIN StudentFeeChequeDetails c "
//                + "ON i.studentFeeInstallmentsId = c.studentFeeInstallmentsId "
//                + "WHERE i.enrollmentId = :enrollmentId "
//                + "AND i.status = true "
//                + "ORDER BY i.installmentNo ASC",
//                Object[].class
//        )
//                .setParameter("enrollmentId", enrollmentId)
//                .getResultList();
//
//        em.close();
//        return list;
//    }
    public List<Object[]> getInstallments(int enrollmentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        List<Object[]> list = em.createQuery(
                "SELECT i.installmentNo, i.paymentDate, i.amountPaid, i.paymentMethod, c.chequeStatus "
                + "FROM StudentFeeInstallments i "
                + "LEFT JOIN StudentFeeChequeDetails c "
                + "ON i.studentFeeInstallmentsId = c.studentFeeInstallmentsId "
                + "WHERE i.enrollmentId = :enrollmentId "
                + "AND i.status = true "
                + "AND (c.chequeStatus IS NULL OR c.chequeStatus NOT IN ('BOUNCED','RETURNED')) "
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
    public void saveInstallment(int studentId, int enrollmentId, int amountPaid,
            Date paymentDate, String paymentMethod, String paymentType, String monthFor, String user) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            // Fetch StudentFeePayments
            TypedQuery<StudentFeePayments> query = em.createQuery(
                    "SELECT p FROM StudentFeePayments p "
                    + "WHERE p.student.studentId = :studentId "
                    + "AND p.enrollment.enrollmentId = :enrollmentId "
                    + "AND p.status = true",
                    StudentFeePayments.class);

            query.setParameter("studentId", studentId);
            query.setParameter("enrollmentId", enrollmentId);

            StudentFeePayments payment = query.getSingleResult();
            int paymentId = payment.getStudentFeePaymentsId();

            // Duplicate check
            if (isDuplicatePayment(paymentId, paymentDate, amountPaid)) {
                JOptionPane.showMessageDialog(null,
                        "Duplicate payment detected.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Next installment number
            int nextInstallment = getNextInstallmentNo(paymentId);

            // ===== INSERT INSTALLMENT =====
            StudentFeeInstallments ins = new StudentFeeInstallments();

            ins.setStudentFeePaymentsId(paymentId);
            ins.setEnrollmentId(enrollmentId);
            ins.setInstallmentNo(nextInstallment);
            ins.setAmountPaid(amountPaid);
            ins.setPaymentDate(paymentDate);
            ins.setPaymentMethod(paymentMethod);
            ins.setPaymentType(paymentType);
            ins.setMonthFor(monthFor);
            ins.setStatus(1);

            em.persist(ins);

            // ===== ONLY UPDATE MASTER IF NOT CHEQUE =====
            if (!paymentMethod.equalsIgnoreCase("CHEQUE")) {

                int newPaid = payment.getTotalPaid() + amountPaid;
                int newBalance = payment.getTotalFee() - newPaid;

                payment.setTotalPaid(newPaid);
                payment.setTotalBalance(newBalance);
                payment.setPaymentType("FULL");
                payment.setLastMofidied(new Date());

                if (newBalance == 0) {
                    payment.setPaymentStatus("COMPLETE");
                } else {
                    payment.setPaymentStatus("ACTIVE");
                }

                em.merge(payment);
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
//    public void saveInstallment(int studentId, int enrollmentId, int amountPaid, Date paymentDate, String paymentMethod, String paymentType, String monthFor, String user) {
//        EntityManager em = HibernateConfig.getEntityManager();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//            tx.begin();
//
//            // Fetch StudentFeePayments record for this student & enrollment
//            TypedQuery<StudentFeePayments> query = em.createQuery(
//                    "SELECT p FROM StudentFeePayments p "
//                    + "WHERE p.student.studentId = :studentId "
//                    + "AND p.enrollment.enrollmentId = :enrollmentId "
//                    + "AND p.status = true",
//                    StudentFeePayments.class
//            );
//            query.setParameter("studentId", studentId);
//            query.setParameter("enrollmentId", enrollmentId);
//
//            StudentFeePayments payment = query.getSingleResult();
//            int paymentId = payment.getStudentFeePaymentsId();
//
//            // Check duplicate
//            if (isDuplicatePayment(paymentId, paymentDate, amountPaid)) {
//                JOptionPane.showMessageDialog(null, "Duplicate payment detected.", "Warning", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//            // Next installment number
//            int nextInstallment = getNextInstallmentNo(paymentId);
//
//            // Create installment
//            StudentFeeInstallments ins = new StudentFeeInstallments();
//            ins.setStudentFeePaymentsId(paymentId);
//            ins.setEnrollmentId(enrollmentId);
//            ins.setInstallmentNo(nextInstallment);
//            ins.setAmountPaid(amountPaid);
//            ins.setPaymentDate(paymentDate);
//            ins.setPaymentMethod(paymentMethod);
//            ins.setPaymentMethod(paymentType);
//            ins.setMonthFor(monthFor);
//            ins.setStatus(1);
//
//            em.persist(ins);
//
//            // Update main payment totals
//            int newPaid = payment.getTotalPaid() + amountPaid;
//            int newBalance = payment.getTotalFee() - newPaid;
//
//            payment.setTotalPaid(newPaid);
//            payment.setTotalBalance(newBalance);
//            payment.setPaymentType("FULL"); // always mark as installment
//            payment.setLastMofidied(new Date());
//
//            // Update payment status
//            if (newBalance == 0) {
//                payment.setPaymentStatus("COMPLETE");
//            } else {
//                payment.setPaymentStatus("ACTIVE");
//            }
//
//            em.merge(payment);
//
//            tx.commit();
//        } catch (Exception e) {
//            if (tx.isActive()) {
//                tx.rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }

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
//            boolean isCheque = paymentMethod.equalsIgnoreCase("CHEQUE");
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
    ////            int selectedBalance = selectedPayment.getTotalBalance();
////            int deductSelected = Math.min(selectedBalance, remainingAmount);
//            int selectedBalance = selectedPayment.getTotalBalance();
//
//            int pendingCheque = getPendingChequeAmountForCourse(
//                    em,
//                    selectedPayment.getEnrollment().getEnrollmentId()
//            );
//
//            int usableBalance = selectedBalance - pendingCheque;
//
//            if (usableBalance < 0) {
//                usableBalance = 0;
//            }
//
//            int deductSelected = Math.min(usableBalance, remainingAmount);
//
//            // Update balances only if NOT cheque
//            if (!isCheque) {
//
//                selectedPayment.setTotalPaid(selectedPayment.getTotalPaid() + deductSelected);
//                selectedPayment.setTotalBalance(selectedPayment.getTotalBalance() - deductSelected);
//                selectedPayment.setPaymentType("ROUND");
//
//                if (selectedPayment.getTotalBalance() <= 0) {
//                    selectedPayment.setPaymentStatus("COMPLETE");
//                }
//
//                em.merge(selectedPayment);
//            }
//
//            // ROUND DETAIL
//            StudentFeeRoundPaymentMasterDetails detailFirst = new StudentFeeRoundPaymentMasterDetails();
//            detailFirst.setStudentFeeRoundPaymentMasterId(master);
//            detailFirst.setEnrollmentId(startEnrollmentId);
//            detailFirst.setPaidAmount(deductSelected);
//            detailFirst.setStatus(1);
//
//            em.persist(detailFirst);
//
//            // INSTALLMENT RECORD
//            StudentFeeInstallments installmentFirst = new StudentFeeInstallments();
//            installmentFirst.setStudentFeePaymentsId(selectedPayment.getStudentFeePaymentsId());
//            installmentFirst.setEnrollmentId(selectedPayment.getEnrollment().getEnrollmentId());
//            installmentFirst.setAmountPaid(deductSelected);
//            installmentFirst.setPaymentDate(paymentDate);
//            installmentFirst.setPaymentMethod(paymentMethod);
//            installmentFirst.setPaymentType("ROUND");
//            installmentFirst.setInstallmentNo(getNextInstallmentNo(selectedPayment.getStudentFeePaymentsId()));
//            installmentFirst.setStatus(1);
//
//            em.persist(installmentFirst);
//
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
////                int balance = payment.getTotalBalance();
////                int deductAmount = Math.min(balance, remainingAmount);
//                int balance = payment.getTotalBalance();
//
//                int pendingCheques = getPendingChequeAmountForCourse(
//                        em,
//                        payment.getEnrollment().getEnrollmentId()
//                );
//
//                int usableBalances = balance - pendingCheques;
//
//                if (usableBalances <= 0) {
//                    continue;
//                }
//
//                int deductAmount = Math.min(usableBalances, remainingAmount);
//
//                // Update balances only if NOT cheque
//                if (!isCheque) {
//
//                    payment.setTotalPaid(payment.getTotalPaid() + deductAmount);
//                    payment.setTotalBalance(payment.getTotalBalance() - deductAmount);
//                    payment.setPaymentType("ROUND");
//
//                    if (payment.getTotalBalance() <= 0) {
//                        payment.setPaymentStatus("COMPLETE");
//                    }
//
//                    em.merge(payment);
//                }
//
//                // ROUND DETAIL
//                StudentFeeRoundPaymentMasterDetails detail = new StudentFeeRoundPaymentMasterDetails();
//                detail.setStudentFeeRoundPaymentMasterId(master);
//                detail.setEnrollmentId(payment.getEnrollment().getEnrollmentId());
//                detail.setPaidAmount(deductAmount);
//                detail.setStatus(1);
//
//                em.persist(detail);
//
//                // INSTALLMENT RECORD
//                StudentFeeInstallments installment = new StudentFeeInstallments();
//                installment.setStudentFeePaymentsId(payment.getStudentFeePaymentsId());
//                installment.setEnrollmentId(payment.getEnrollment().getEnrollmentId());
//                installment.setAmountPaid(deductAmount);
//                installment.setPaymentDate(paymentDate);
//                installment.setPaymentMethod(paymentMethod);
//                installment.setPaymentType("ROUND");
//                installment.setInstallmentNo(getNextInstallmentNo(payment.getStudentFeePaymentsId()));
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
    
    public void processRoundPayment(int studentId, int startEnrollmentId, int paidAmount,
            Date paymentDate, String paymentMethod,
            String chequeNo, String bank, String branch, Date chequeDate,
            String user) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            boolean isCheque = paymentMethod.equalsIgnoreCase("CHEQUE");
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

//            int selectedBalance = selectedPayment.getTotalBalance();
//            int deductSelected = Math.min(selectedBalance, remainingAmount);
            int selectedBalance = selectedPayment.getTotalBalance();

            int pendingCheque = getPendingChequeAmountForCourse(
                    em,
                    selectedPayment.getEnrollment().getEnrollmentId()
            );

            int usableBalance = selectedBalance - pendingCheque;

            if (usableBalance < 0) {
                usableBalance = 0;
            }

            int deductSelected = Math.min(usableBalance, remainingAmount);

            // Update balances only if NOT cheque
            if (!isCheque) {

                selectedPayment.setTotalPaid(selectedPayment.getTotalPaid() + deductSelected);
                selectedPayment.setTotalBalance(selectedPayment.getTotalBalance() - deductSelected);
                selectedPayment.setPaymentType("ROUND");

                if (selectedPayment.getTotalBalance() <= 0) {
                    selectedPayment.setPaymentStatus("COMPLETE");
                }

                em.merge(selectedPayment);
            }

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
            em.flush();
            
            if (isCheque) {
                StudentFeeChequeDetails cheque = new StudentFeeChequeDetails();
                cheque.setStudentFeeInstallmentsId(installmentFirst.getStudentFeeInstallmentsId());
                cheque.setChequeNo(chequeNo);
                cheque.setBank(bank);
                cheque.setBranch(branch);
                cheque.setChequeDate(chequeDate);
                cheque.setChequeAmount(deductSelected);
                cheque.setChequeStatus("PENDING");
                cheque.setStatus(1);

                em.persist(cheque);
            }

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

//                int balance = payment.getTotalBalance();
//                int deductAmount = Math.min(balance, remainingAmount);
                int balance = payment.getTotalBalance();

                int pendingCheques = getPendingChequeAmountForCourse(
                        em,
                        payment.getEnrollment().getEnrollmentId()
                );

                int usableBalances = balance - pendingCheques;

                if (usableBalances <= 0) {
                    continue;
                }

                int deductAmount = Math.min(usableBalances, remainingAmount);

                // Update balances only if NOT cheque
                if (!isCheque) {

                    payment.setTotalPaid(payment.getTotalPaid() + deductAmount);
                    payment.setTotalBalance(payment.getTotalBalance() - deductAmount);
                    payment.setPaymentType("ROUND");

                    if (payment.getTotalBalance() <= 0) {
                        payment.setPaymentStatus("COMPLETE");
                    }

                    em.merge(payment);
                }

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
                em.flush();
                
                if (isCheque) {
                    StudentFeeChequeDetails cheque = new StudentFeeChequeDetails();
                    cheque.setStudentFeeInstallmentsId(installment.getStudentFeeInstallmentsId());
                    cheque.setChequeNo(chequeNo);
                    cheque.setBank(bank);
                    cheque.setBranch(branch);
                    cheque.setChequeDate(chequeDate);
                    cheque.setChequeAmount(deductAmount);
                    cheque.setChequeStatus("PENDING");
                    cheque.setStatus(1);

                    em.persist(cheque);
                }

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

    public int getPendingChequeAmountForCourse(EntityManager em, int enrollmentId) {

        Long pending = em.createQuery(
                "SELECT SUM(c.chequeAmount) "
                + "FROM StudentFeeChequeDetails c, StudentFeeInstallments i "
                + "WHERE c.studentFeeInstallmentsId = i.studentFeeInstallmentsId "
                + "AND i.enrollmentId = :enrollmentId "
                + "AND c.chequeStatus = 'PENDING' "
                + "AND c.status = 1",
                Long.class
        )
                .setParameter("enrollmentId", enrollmentId)
                .getSingleResult();

        return pending == null ? 0 : pending.intValue();
    }

    public int getPendingChequeAmountForCourse(int enrollmentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        Long pending = em.createQuery(
                "SELECT SUM(c.chequeAmount) "
                + "FROM StudentFeeChequeDetails c, StudentFeeInstallments i "
                + "WHERE c.studentFeeInstallmentsId = i.studentFeeInstallmentsId "
                + "AND i.enrollmentId = :enrollmentId "
                + "AND c.chequeStatus = 'PENDING' "
                + "AND c.status = 1",
                Long.class
        )
                .setParameter("enrollmentId", enrollmentId)
                .getSingleResult();

        em.close();

        return pending == null ? 0 : pending.intValue();
    }

    public void saveChequePayment(int studentId, int enrollmentId, int amount,
            Date paymentDate, String chequeNo, String bank, String branch,
            Date chequeDate, String user) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            // get last installment created for this payment
            StudentFeeInstallments installment = em.createQuery(
                    "SELECT i FROM StudentFeeInstallments i "
                    + "WHERE i.enrollmentId = :eid "
                    + "ORDER BY i.studentFeeInstallmentsId DESC",
                    StudentFeeInstallments.class)
                    .setParameter("eid", enrollmentId)
                    .setMaxResults(1)
                    .getSingleResult();

            // ===== CHEQUE DETAILS =====
            StudentFeeChequeDetails cheque = new StudentFeeChequeDetails();

            cheque.setStudentFeeInstallmentsId(installment.getStudentFeeInstallmentsId());
            cheque.setChequeNo(chequeNo);
            cheque.setBank(bank);
            cheque.setBranch(branch);
            cheque.setChequeDate(chequeDate);
            cheque.setChequeAmount(amount);
            cheque.setChequeStatus("PENDING");
            cheque.setStatus(1);

            em.persist(cheque);

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

    public int getStudentPendingChequeTotal(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        Long pendingChequeTotal = em.createQuery(
                "SELECT SUM(c.chequeAmount) "
                + "FROM StudentFeeChequeDetails c, StudentFeeInstallments i, StudentFeePayments p "
                + "WHERE c.studentFeeInstallmentsId = i.studentFeeInstallmentsId "
                + "AND i.studentFeePaymentsId = p.studentFeePaymentsId "
                + "AND p.student.studentId = :studentId "
                + "AND c.chequeStatus = 'PENDING' "
                + "AND c.status = 1",
                Long.class
        )
                .setParameter("studentId", studentId)
                .getSingleResult();

        em.close();

        return pendingChequeTotal == null ? 0 : pendingChequeTotal.intValue();
    }
}
