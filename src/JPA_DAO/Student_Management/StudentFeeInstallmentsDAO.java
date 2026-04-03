package JPA_DAO.Student_Management;

import Classes.HibernateConfig;
import Classes.LedgerHelper;
import Classes.LogHelper;
import Entities.Student_Management.StudentFeeChequeDetails;
import Entities.Student_Management.StudentFeeInstallments;
import Entities.Student_Management.StudentFeePayments;
import Entities.Student_Management.StudentFeeRoundPaymentMaster;
import Entities.Student_Management.StudentFeeRoundPaymentMasterDetails;
import Panels.Fees_Management;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
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
            em.flush();

            // ===== LOG + LEDGER (ONLY CASH/CARD) =====
            if (!paymentMethod.equalsIgnoreCase("CHEQUE")) {
                // LOG
                LogHelper.saveLog(em,
                        "STUDENT_PAYMENT",
                        ins.getStudentFeeInstallmentsId(),
                        "INSERT",
                        amountPaid,
                        paymentMethod,
                        "Installment payment saved for month " + monthFor,
                        user
                );

                // LEDGER
                LedgerHelper.saveLedger(em,
                        amountPaid,
                        "CREDIT",
                        "Installment payment for month " + monthFor,
                        "STUDENT_PAYMENT",
                        ins.getStudentFeeInstallmentsId(),
                        paymentMethod,
                        "MONTHLY_FEE",
                        user
                );
            }

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
            // CREATE ROUND MASTER
            // =====================================================
            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
            master.setStudentId(studentId);
            master.setPaymentDate(paymentDate);
            master.setPaymentMode(paymentMethod);
            master.setTotalPaid(paidAmount);
            master.setRemarks("ROUND PAYMENT");
            master.setUser(user);
            master.setStatus(1);

            // 🔹 LOG - ROUND MASTER CREATED
            LogHelper.saveLog(em,
                    "ROUND_PAYMENT",
                    master.getStudentFeeRoundPaymentMasterId(),
                    "INSERT",
                    paidAmount,
                    paymentMethod,
                    "Round payment initiated",
                    user
            );

            // 🔹 LEDGER ONLY FOR CASH/CARD
            if (!isCheque) {
                LedgerHelper.saveLedger(em,
                        paidAmount,
                        "CREDIT",
                        "Round payment received",
                        "ROUND_PAYMENT",
                        master.getStudentFeeRoundPaymentMasterId(),
                        paymentMethod,
                        "ROUND_PAYMENT",
                        user
                );
            }

            em.persist(master);
            em.flush();

            // =====================================================
            // PAY SELECTED COURSE FIRST
            // =====================================================
            StudentFeePayments selectedPayment = em.createQuery(
                    "SELECT p FROM StudentFeePayments p WHERE p.student.studentId=:sid AND p.enrollment.enrollmentId=:eid AND p.status=true",
                    StudentFeePayments.class)
                    .setParameter("sid", studentId)
                    .setParameter("eid", startEnrollmentId)
                    .getSingleResult();

            int usable = selectedPayment.getTotalBalance()
                    - getPendingChequeAmountForCourse(em, startEnrollmentId);

            if (usable < 0) {
                usable = 0;
            }

            int deduct = Math.min(usable, remainingAmount);

            if (!isCheque) {
                selectedPayment.setTotalPaid(selectedPayment.getTotalPaid() + deduct);
                selectedPayment.setTotalBalance(selectedPayment.getTotalBalance() - deduct);
                if (selectedPayment.getTotalBalance() <= 0) {
                    selectedPayment.setPaymentStatus("COMPLETE");
                }
                em.merge(selectedPayment);
            }

            // INSTALLMENT FOR SELECTED COURSE
            StudentFeeInstallments inst1 = new StudentFeeInstallments();
            inst1.setStudentFeePaymentsId(selectedPayment.getStudentFeePaymentsId());
            inst1.setEnrollmentId(startEnrollmentId);
            inst1.setAmountPaid(deduct);
            inst1.setPaymentDate(paymentDate);
            inst1.setPaymentMethod(paymentMethod);
            inst1.setPaymentType("ROUND");
            inst1.setInstallmentNo(getNextInstallmentNo(selectedPayment.getStudentFeePaymentsId()));
            inst1.setStatus(1);
            em.persist(inst1);
            em.flush();

            // 🔹 LOG
            LogHelper.saveLog(em,
                    "STUDENT_PAYMENT",
                    inst1.getStudentFeeInstallmentsId(),
                    isCheque ? "CHEQUE_PENDING" : "INSERT",
                    deduct,
                    paymentMethod,
                    "Round payment applied (selected course)",
                    user
            );

            // 🔹 LEDGER (ONLY CASH/CARD)
            if (!isCheque && deduct > 0) {
                LedgerHelper.saveLedger(em,
                        deduct,
                        "CREDIT",
                        "Round payment - selected course",
                        "STUDENT_PAYMENT",
                        inst1.getStudentFeeInstallmentsId(),
                        paymentMethod,
                        "ROUND_PAYMENT",
                        user
                );
            }

            if (isCheque) {
                StudentFeeChequeDetails chq = new StudentFeeChequeDetails();
                chq.setStudentFeeInstallmentsId(inst1.getStudentFeeInstallmentsId());
                chq.setChequeNo(chequeNo);
                chq.setBank(bank);
                chq.setBranch(branch);
                chq.setChequeDate(chequeDate);
                chq.setChequeAmount(deduct);
                chq.setChequeStatus("PENDING");
                chq.setStatus(1);
                em.persist(chq);
            }

            // MASTER DETAILS FOR SELECTED COURSE
            StudentFeeRoundPaymentMasterDetails detail1 = new StudentFeeRoundPaymentMasterDetails();
            detail1.setStudentFeeRoundPaymentMaster(master);
            detail1.setEnrollmentId(startEnrollmentId);
            detail1.setPaidAmount(deduct);
            detail1.setStatus(1);
            em.persist(detail1);

            remainingAmount -= deduct;

            // =====================================================
            // OTHER COURSES
            // =====================================================
            List<StudentFeePayments> payments = em.createQuery(
                    "SELECT p FROM StudentFeePayments p WHERE p.student.studentId=:sid AND p.status=true AND p.totalBalance>0 AND p.enrollment.enrollmentId<>:eid ORDER BY p.createdAt ASC",
                    StudentFeePayments.class)
                    .setParameter("sid", studentId)
                    .setParameter("eid", startEnrollmentId)
                    .getResultList();

            for (StudentFeePayments payment : payments) {
                if (remainingAmount <= 0) {
                    break;
                }

                int enrollmentId = payment.getEnrollment().getEnrollmentId();
                int usableBal = payment.getTotalBalance() - getPendingChequeAmountForCourse(em, enrollmentId);
                if (usableBal <= 0) {
                    continue;
                }

                int deductAmount = Math.min(usableBal, remainingAmount);
                String courseType = payment.getCourseType();

                // =====================================================
                // MONTHLY COURSE (OPTIMIZED)
                // =====================================================
                if ("MONTHLY".equalsIgnoreCase(courseType)) {

                    Object[] courseData = (Object[]) em.createNativeQuery(
                            "SELECT c.enrol_year, c.enrol_month, c.comp_year, c.comp_month "
                            + "FROM course_enrollment ce "
                            + "JOIN course c ON ce.course_id = c.course_id "
                            + "WHERE ce.enrollment_id=?"
                    ).setParameter(1, enrollmentId).getSingleResult();

                    int enrolYear = Integer.parseInt(courseData[0].toString());
                    int enrolMonth = Integer.parseInt(courseData[1].toString());
                    int compYear = Integer.parseInt(courseData[2].toString());
                    int compMonth = Integer.parseInt(courseData[3].toString());

                    java.time.YearMonth start = java.time.YearMonth.of(enrolYear, enrolMonth);
                    java.time.YearMonth end = java.time.YearMonth.of(compYear, compMonth);
                    int totalMonths = (int) java.time.temporal.ChronoUnit.MONTHS.between(start, end) + 1;

                    int totalFee = payment.getTotalFee();
                    int monthlyFee = totalMonths > 0 ? totalFee / totalMonths : totalFee;

                    // GET PAID MONTHS
                    List<StudentFeeInstallments> installments = em.createQuery(
                            "SELECT i FROM StudentFeeInstallments i "
                            + "WHERE i.enrollmentId=:eid AND i.status=1 AND i.monthFor IS NOT NULL ORDER BY i.monthFor ASC",
                            StudentFeeInstallments.class)
                            .setParameter("eid", enrollmentId)
                            .getResultList();

                    java.time.YearMonth nextMonth = start;

                    for (StudentFeeInstallments inst : installments) {
                        java.time.YearMonth instMonth = java.time.YearMonth.parse(inst.getMonthFor());
                        int instBalance = monthlyFee - inst.getAmountPaid();
                        if (instBalance > 0) {
                            nextMonth = instMonth;
                            break;
                        }
                        nextMonth = instMonth.plusMonths(1);
                    }

                    int remainingMonthly = deductAmount;

                    while (remainingMonthly > 0 && !nextMonth.isAfter(end)) {
                        StudentFeeInstallments inst = null;
                        List<StudentFeeInstallments> existing = em.createQuery(
                                "SELECT i FROM StudentFeeInstallments i "
                                + "WHERE i.enrollmentId=:eid AND i.monthFor=:month",
                                StudentFeeInstallments.class)
                                .setParameter("eid", enrollmentId)
                                .setParameter("month", nextMonth.toString())
                                .getResultList();

                        int pay;
                        if (!existing.isEmpty()) {
                            inst = existing.get(0);
                            int instBalance = monthlyFee - inst.getAmountPaid();
                            pay = Math.min(instBalance, remainingMonthly);
                            inst.setAmountPaid(inst.getAmountPaid() + pay);
                            em.merge(inst);
                        } else {
                            pay = Math.min(monthlyFee, remainingMonthly);
                            inst = new StudentFeeInstallments();
                            inst.setStudentFeePaymentsId(payment.getStudentFeePaymentsId());
                            inst.setEnrollmentId(enrollmentId);
                            inst.setAmountPaid(pay);
                            inst.setPaymentDate(paymentDate);
                            inst.setPaymentMethod(paymentMethod);
                            inst.setPaymentType("MONTHLY");
                            inst.setMonthFor(nextMonth.toString());
                            inst.setInstallmentNo(getNextInstallmentNo(payment.getStudentFeePaymentsId()));
                            inst.setStatus(1);
                            em.persist(inst);
                            em.flush();
                        }

                        if (isCheque) {
                            StudentFeeChequeDetails chq2 = new StudentFeeChequeDetails();
                            chq2.setStudentFeeInstallmentsId(inst.getStudentFeeInstallmentsId());
                            chq2.setChequeNo(chequeNo);
                            chq2.setBank(bank);
                            chq2.setBranch(branch);
                            chq2.setChequeDate(chequeDate);
                            chq2.setChequeAmount(pay);
                            chq2.setChequeStatus("PENDING");
                            chq2.setStatus(1);
                            em.persist(chq2);
                        }

                        StudentFeeRoundPaymentMasterDetails detailOther = new StudentFeeRoundPaymentMasterDetails();
                        detailOther.setStudentFeeRoundPaymentMaster(master);
                        detailOther.setEnrollmentId(enrollmentId);
                        detailOther.setPaidAmount(pay);
                        detailOther.setStatus(1);
                        em.persist(detailOther);

                        // 🔹 LOG
                        LogHelper.saveLog(em,
                                "STUDENT_PAYMENT",
                                inst.getStudentFeeInstallmentsId(),
                                isCheque ? "CHEQUE_PENDING" : "INSERT",
                                pay,
                                paymentMethod,
                                "Round payment applied for month " + nextMonth,
                                user
                        );

                        // 🔹 LEDGER (ONLY CASH/CARD)
                        if (!isCheque && pay > 0) {
                            LedgerHelper.saveLedger(em,
                                    pay,
                                    "CREDIT",
                                    "Round payment - " + nextMonth,
                                    "STUDENT_PAYMENT",
                                    inst.getStudentFeeInstallmentsId(),
                                    paymentMethod,
                                    "ROUND_PAYMENT",
                                    user
                            );
                        }

                        remainingMonthly -= pay;
                        nextMonth = nextMonth.plusMonths(1);
                    }

                    // UPDATE TOTALS ONLY FOR CASH/CARD
                    if (!isCheque) {
                        int totalPaidThisRound = deductAmount - remainingMonthly;
                        payment.setTotalPaid(payment.getTotalPaid() + totalPaidThisRound);
                        payment.setTotalBalance(payment.getTotalBalance() - totalPaidThisRound);
                        if (payment.getTotalBalance() <= 0) {
                            payment.setPaymentStatus("COMPLETE");
                        }
                        em.merge(payment);
                    }

                } else {
                    // =====================================================
                    // NORMAL COURSE
                    // =====================================================
                    if (!isCheque) {
                        payment.setTotalPaid(payment.getTotalPaid() + deductAmount);
                        payment.setTotalBalance(payment.getTotalBalance() - deductAmount);
                        if (payment.getTotalBalance() <= 0) {
                            payment.setPaymentStatus("COMPLETE");
                        }
                        em.merge(payment);
                    }

                    StudentFeeInstallments inst = new StudentFeeInstallments();
                    inst.setStudentFeePaymentsId(payment.getStudentFeePaymentsId());
                    inst.setEnrollmentId(enrollmentId);
                    inst.setAmountPaid(deductAmount);
                    inst.setPaymentDate(paymentDate);
                    inst.setPaymentMethod(paymentMethod);
                    inst.setPaymentType("ROUND");
                    inst.setInstallmentNo(getNextInstallmentNo(payment.getStudentFeePaymentsId()));
                    inst.setStatus(1);
                    em.persist(inst);
                    em.flush();

                    // 🔹 LOG
                    LogHelper.saveLog(em,
                            "STUDENT_PAYMENT",
                            inst.getStudentFeeInstallmentsId(),
                            isCheque ? "CHEQUE_PENDING" : "INSERT",
                            deductAmount,
                            paymentMethod,
                            "Round payment applied (normal course)",
                            user
                    );

                    // 🔹 LEDGER
                    if (!isCheque && deductAmount > 0) {
                        LedgerHelper.saveLedger(em,
                                deductAmount,
                                "CREDIT",
                                "Round payment - normal course",
                                "STUDENT_PAYMENT",
                                inst.getStudentFeeInstallmentsId(),
                                paymentMethod,
                                "ROUND_PAYMENT",
                                user
                        );
                    }

                    if (isCheque) {
                        StudentFeeChequeDetails chq2 = new StudentFeeChequeDetails();
                        chq2.setStudentFeeInstallmentsId(inst.getStudentFeeInstallmentsId());
                        chq2.setChequeNo(chequeNo);
                        chq2.setBank(bank);
                        chq2.setBranch(branch);
                        chq2.setChequeDate(chequeDate);
                        chq2.setChequeAmount(deductAmount);
                        chq2.setChequeStatus("PENDING");
                        chq2.setStatus(1);
                        em.persist(chq2);
                    }

                    StudentFeeRoundPaymentMasterDetails detailOther = new StudentFeeRoundPaymentMasterDetails();
                    detailOther.setStudentFeeRoundPaymentMaster(master);
                    detailOther.setEnrollmentId(enrollmentId);
                    detailOther.setPaidAmount(deductAmount);
                    detailOther.setStatus(1);
                    em.persist(detailOther);
                }

                remainingAmount -= deductAmount;
            }

            LogHelper.saveLog(em,
                    "ROUND_PAYMENT",
                    master.getStudentFeeRoundPaymentMasterId(),
                    "COMPLETED",
                    paidAmount,
                    paymentMethod,
                    "Round payment fully processed",
                    user
            );

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
//            Date paymentDate, String paymentMethod,
//            String chequeNo, String bank, String branch, Date chequeDate,
//            String user) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//            tx.begin();
//
//            boolean isCheque = paymentMethod.equalsIgnoreCase("CHEQUE");
//            int remainingAmount = paidAmount;
//
//            // =====================================================
//            // CREATE ROUND MASTER
//            // =====================================================
//            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
//            master.setStudentId(studentId);
//            master.setPaymentDate(paymentDate);
//            master.setPaymentMode(paymentMethod);
//            master.setTotalPaid(paidAmount);
//            master.setRemarks("ROUND PAYMENT");
//            master.setUser(user);
//            master.setStatus(1);
//
//            em.persist(master);
//            em.flush();
//
//            // =====================================================
//            // PAY SELECTED COURSE FIRST
//            // =====================================================
//            StudentFeePayments selectedPayment = em.createQuery(
//                    "SELECT p FROM StudentFeePayments p WHERE p.student.studentId=:sid AND p.enrollment.enrollmentId=:eid AND p.status=true",
//                    StudentFeePayments.class)
//                    .setParameter("sid", studentId)
//                    .setParameter("eid", startEnrollmentId)
//                    .getSingleResult();
//
//            int usable = selectedPayment.getTotalBalance()
//                    - getPendingChequeAmountForCourse(em, startEnrollmentId);
//
//            if (usable < 0) {
//                usable = 0;
//            }
//
//            int deduct = Math.min(usable, remainingAmount);
//
//            if (!isCheque) {
//                selectedPayment.setTotalPaid(selectedPayment.getTotalPaid() + deduct);
//                selectedPayment.setTotalBalance(selectedPayment.getTotalBalance() - deduct);
//
//                if (selectedPayment.getTotalBalance() <= 0) {
//                    selectedPayment.setPaymentStatus("COMPLETE");
//                }
//
//                em.merge(selectedPayment);
//            }
//
//            // INSTALLMENT FOR SELECTED COURSE
//            StudentFeeInstallments inst1 = new StudentFeeInstallments();
//            inst1.setStudentFeePaymentsId(selectedPayment.getStudentFeePaymentsId());
//            inst1.setEnrollmentId(startEnrollmentId);
//            inst1.setAmountPaid(deduct);
//            inst1.setPaymentDate(paymentDate);
//            inst1.setPaymentMethod(paymentMethod);
//            inst1.setPaymentType("ROUND");
//            inst1.setInstallmentNo(getNextInstallmentNo(selectedPayment.getStudentFeePaymentsId()));
//            inst1.setStatus(1);
//            em.persist(inst1);
//            em.flush();
//
//            if (isCheque) {
//                StudentFeeChequeDetails chq = new StudentFeeChequeDetails();
//                chq.setStudentFeeInstallmentsId(inst1.getStudentFeeInstallmentsId());
//                chq.setChequeNo(chequeNo);
//                chq.setBank(bank);
//                chq.setBranch(branch);
//                chq.setChequeDate(chequeDate);
//                chq.setChequeAmount(deduct);
//                chq.setChequeStatus("PENDING");
//                chq.setStatus(1);
//                em.persist(chq);
//            }
//
//            // MASTER DETAILS FOR SELECTED COURSE
//            StudentFeeRoundPaymentMasterDetails detail1 = new StudentFeeRoundPaymentMasterDetails();
//            detail1.setStudentFeeRoundPaymentMaster(master);
//            detail1.setEnrollmentId(startEnrollmentId);
//            detail1.setPaidAmount(deduct);
//            detail1.setStatus(1);
//            em.persist(detail1);
//
//            remainingAmount -= deduct;
//
//            // =====================================================
//            // OTHER COURSES
//            // =====================================================
//            List<StudentFeePayments> payments = em.createQuery(
//                    "SELECT p FROM StudentFeePayments p WHERE p.student.studentId=:sid AND p.status=true AND p.totalBalance>0 AND p.enrollment.enrollmentId<>:eid ORDER BY p.createdAt ASC",
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
//                int enrollmentId = payment.getEnrollment().getEnrollmentId();
//                int usableBal = payment.getTotalBalance()
//                        - getPendingChequeAmountForCourse(em, enrollmentId);
//
//                if (usableBal <= 0) {
//                    continue;
//                }
//
//                int deductAmount = Math.min(usableBal, remainingAmount);
//                String courseType = payment.getCourseType();
//
//                // =====================================================
//                // MONTHLY COURSE (OPTIMIZED)
//                // =====================================================
//                if ("MONTHLY".equalsIgnoreCase(courseType)) {
//
//                    Object[] courseData = (Object[]) em.createNativeQuery(
//                            "SELECT c.enrol_year, c.enrol_month, c.comp_year, c.comp_month "
//                            + "FROM course_enrollment ce "
//                            + "JOIN course c ON ce.course_id = c.course_id "
//                            + "WHERE ce.enrollment_id=?"
//                    ).setParameter(1, enrollmentId).getSingleResult();
//
//                    int enrolYear = Integer.parseInt(courseData[0].toString());
//                    int enrolMonth = Integer.parseInt(courseData[1].toString());
//                    int compYear = Integer.parseInt(courseData[2].toString());
//                    int compMonth = Integer.parseInt(courseData[3].toString());
//
//                    java.time.YearMonth start = java.time.YearMonth.of(enrolYear, enrolMonth);
//                    java.time.YearMonth end = java.time.YearMonth.of(compYear, compMonth);
//                    int totalMonths = (int) java.time.temporal.ChronoUnit.MONTHS.between(start, end) + 1;
//
//                    int totalFee = payment.getTotalFee();
//                    int monthlyFee = totalMonths > 0 ? totalFee / totalMonths : totalFee;
//
//                    // GET PAID MONTHS
//                    List<StudentFeeInstallments> installments = em.createQuery(
//                            "SELECT i FROM StudentFeeInstallments i "
//                            + "WHERE i.enrollmentId=:eid AND i.status=1 AND i.monthFor IS NOT NULL ORDER BY i.monthFor ASC",
//                            StudentFeeInstallments.class)
//                            .setParameter("eid", enrollmentId)
//                            .getResultList();
//
//                    java.time.YearMonth nextMonth = start;
//
//                    // FIND NEXT UNPAID / PARTIALLY PAID MONTH
//                    for (StudentFeeInstallments inst : installments) {
//                        java.time.YearMonth instMonth = java.time.YearMonth.parse(inst.getMonthFor());
//                        int instBalance = monthlyFee - inst.getAmountPaid();
//                        if (instBalance > 0) {
//                            nextMonth = instMonth;
//                            break;
//                        }
//                        nextMonth = instMonth.plusMonths(1);
//                    }
//
//                    int remainingMonthly = deductAmount;
//
//                    while (remainingMonthly > 0 && !nextMonth.isAfter(end)) {
//
//                        // CHECK IF INSTALLMENT EXISTS
//                        StudentFeeInstallments inst = null;
//                        List<StudentFeeInstallments> existing = em.createQuery(
//                                "SELECT i FROM StudentFeeInstallments i "
//                                + "WHERE i.enrollmentId=:eid AND i.monthFor=:month",
//                                StudentFeeInstallments.class)
//                                .setParameter("eid", enrollmentId)
//                                .setParameter("month", nextMonth.toString())
//                                .getResultList();
//
//                        if (!existing.isEmpty()) {
//                            inst = existing.get(0);
//                        }
//
//                        int pay;
//                        if (inst != null) {
//                            // update existing partial month
//                            int instBalance = monthlyFee - inst.getAmountPaid();
//                            pay = Math.min(instBalance, remainingMonthly);
//                            inst.setAmountPaid(inst.getAmountPaid() + pay);
//                            em.merge(inst);
//                        } else {
//                            // new installment for this month
//                            pay = Math.min(monthlyFee, remainingMonthly);
//                            inst = new StudentFeeInstallments();
//                            inst.setStudentFeePaymentsId(payment.getStudentFeePaymentsId());
//                            inst.setEnrollmentId(enrollmentId);
//                            inst.setAmountPaid(pay);
//                            inst.setPaymentDate(paymentDate);
//                            inst.setPaymentMethod(paymentMethod);
//                            inst.setPaymentType("MONTHLY");
//                            inst.setMonthFor(nextMonth.toString());
//                            inst.setInstallmentNo(getNextInstallmentNo(payment.getStudentFeePaymentsId()));
//                            inst.setStatus(1);
//                            em.persist(inst);
//                            em.flush();
//                        }
//
//                        // CHEQUE DETAILS
//                        if (isCheque) {
//                            StudentFeeChequeDetails chq2 = new StudentFeeChequeDetails();
//                            chq2.setStudentFeeInstallmentsId(inst.getStudentFeeInstallmentsId());
//                            chq2.setChequeNo(chequeNo);
//                            chq2.setBank(bank);
//                            chq2.setBranch(branch);
//                            chq2.setChequeDate(chequeDate);
//                            chq2.setChequeAmount(pay);
//                            chq2.setChequeStatus("PENDING");
//                            chq2.setStatus(1);
//                            em.persist(chq2);
//                        }
//
//                        // MASTER DETAILS
//                        StudentFeeRoundPaymentMasterDetails detailOther = new StudentFeeRoundPaymentMasterDetails();
//                        detailOther.setStudentFeeRoundPaymentMaster(master);
//                        detailOther.setEnrollmentId(enrollmentId);
//                        detailOther.setPaidAmount(pay);
//                        detailOther.setStatus(1);
//                        em.persist(detailOther);
//
//                        remainingMonthly -= pay;
//                        nextMonth = nextMonth.plusMonths(1);
//                    }
//
//                    // UPDATE TOTAL PAID & BALANCE
//                    payment.setTotalPaid(payment.getTotalPaid() + (deductAmount - remainingMonthly));
//                    payment.setTotalBalance(payment.getTotalBalance() - (deductAmount - remainingMonthly));
//                    if (payment.getTotalBalance() <= 0) {
//                        payment.setPaymentStatus("COMPLETE");
//                    }
//                    em.merge(payment);
//
//                } else {
//                    // =====================================================
//                    // NORMAL COURSE
//                    // =====================================================
//                    if (!isCheque) {
//                        payment.setTotalPaid(payment.getTotalPaid() + deductAmount);
//                        payment.setTotalBalance(payment.getTotalBalance() - deductAmount);
//
//                        if (payment.getTotalBalance() <= 0) {
//                            payment.setPaymentStatus("COMPLETE");
//                        }
//
//                        em.merge(payment);
//                    }
//
//                    StudentFeeInstallments inst = new StudentFeeInstallments();
//                    inst.setStudentFeePaymentsId(payment.getStudentFeePaymentsId());
//                    inst.setEnrollmentId(enrollmentId);
//                    inst.setAmountPaid(deductAmount);
//                    inst.setPaymentDate(paymentDate);
//                    inst.setPaymentMethod(paymentMethod);
//                    inst.setPaymentType("ROUND");
//                    inst.setInstallmentNo(getNextInstallmentNo(payment.getStudentFeePaymentsId()));
//                    inst.setStatus(1);
//                    em.persist(inst);
//                    em.flush();
//
//                    if (isCheque) {
//                        StudentFeeChequeDetails chq2 = new StudentFeeChequeDetails();
//                        chq2.setStudentFeeInstallmentsId(inst.getStudentFeeInstallmentsId());
//                        chq2.setChequeNo(chequeNo);
//                        chq2.setBank(bank);
//                        chq2.setBranch(branch);
//                        chq2.setChequeDate(chequeDate);
//                        chq2.setChequeAmount(deductAmount);
//                        chq2.setChequeStatus("PENDING");
//                        chq2.setStatus(1);
//                        em.persist(chq2);
//                    }
//
//                    StudentFeeRoundPaymentMasterDetails detailOther = new StudentFeeRoundPaymentMasterDetails();
//                    detailOther.setStudentFeeRoundPaymentMaster(master);
//                    detailOther.setEnrollmentId(enrollmentId);
//                    detailOther.setPaidAmount(deductAmount);
//                    detailOther.setStatus(1);
//                    em.persist(detailOther);
//                }
//
//                remainingAmount -= deductAmount;
//            }
//
//            tx.commit();
//
//        } catch (Exception e) {
//            if (tx.isActive()) {
//                tx.rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }
    // ************ FULLY WORKING *************************************************************************
//    public void processRoundPayment(int studentId, int startEnrollmentId, int paidAmount,
//            Date paymentDate, String paymentMethod,
//            String chequeNo, String bank, String branch, Date chequeDate,
//            String user) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//            tx.begin();
//
//            boolean isCheque = paymentMethod.equalsIgnoreCase("CHEQUE");
//            int remainingAmount = paidAmount;
//
//            // =====================================================
//            // CREATE ROUND MASTER
//            // =====================================================
//            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
//            master.setStudentId(studentId);
//            master.setPaymentDate(paymentDate);
//            master.setPaymentMode(paymentMethod);
//            master.setTotalPaid(paidAmount);
//            master.setRemarks("ROUND PAYMENT");
//            master.setUser(user);
//            master.setStatus(1);
//
//            em.persist(master);
//            em.flush();
//
//            // =====================================================
//            // PAY SELECTED COURSE FIRST
//            // =====================================================
//            StudentFeePayments selectedPayment = em.createQuery(
//                    "SELECT p FROM StudentFeePayments p WHERE p.student.studentId=:sid AND p.enrollment.enrollmentId=:eid AND p.status=true",
//                    StudentFeePayments.class)
//                    .setParameter("sid", studentId)
//                    .setParameter("eid", startEnrollmentId)
//                    .getSingleResult();
//
//            int usable = selectedPayment.getTotalBalance()
//                    - getPendingChequeAmountForCourse(em, startEnrollmentId);
//
//            if (usable < 0) {
//                usable = 0;
//            }
//
//            int deduct = Math.min(usable, remainingAmount);
//
//            if (!isCheque) {
//                selectedPayment.setTotalPaid(selectedPayment.getTotalPaid() + deduct);
//                selectedPayment.setTotalBalance(selectedPayment.getTotalBalance() - deduct);
//
//                if (selectedPayment.getTotalBalance() <= 0) {
//                    selectedPayment.setPaymentStatus("COMPLETE");
//                }
//
//                em.merge(selectedPayment);
//            }
//
//            // INSTALLMENT FOR SELECTED COURSE
//            StudentFeeInstallments inst1 = new StudentFeeInstallments();
//            inst1.setStudentFeePaymentsId(selectedPayment.getStudentFeePaymentsId());
//            inst1.setEnrollmentId(startEnrollmentId);
//            inst1.setAmountPaid(deduct);
//            inst1.setPaymentDate(paymentDate);
//            inst1.setPaymentMethod(paymentMethod);
//            inst1.setPaymentType("ROUND");
//            inst1.setInstallmentNo(getNextInstallmentNo(selectedPayment.getStudentFeePaymentsId()));
//            inst1.setStatus(1);
//            em.persist(inst1);
//            em.flush();
//
//            if (isCheque) {
//                StudentFeeChequeDetails chq = new StudentFeeChequeDetails();
//                chq.setStudentFeeInstallmentsId(inst1.getStudentFeeInstallmentsId());
//                chq.setChequeNo(chequeNo);
//                chq.setBank(bank);
//                chq.setBranch(branch);
//                chq.setChequeDate(chequeDate);
//                chq.setChequeAmount(deduct);
//                chq.setChequeStatus("PENDING");
//                chq.setStatus(1);
//                em.persist(chq);
//            }
//
//            // MASTER DETAILS FOR SELECTED COURSE
//            StudentFeeRoundPaymentMasterDetails detail1 = new StudentFeeRoundPaymentMasterDetails();
//            detail1.setStudentFeeRoundPaymentMaster(master);
//            detail1.setEnrollmentId(startEnrollmentId);
//            detail1.setPaidAmount(deduct);
//            detail1.setStatus(1);
//            em.persist(detail1);
//
//            remainingAmount -= deduct;
//
//            // =====================================================
//            // OTHER COURSES
//            // =====================================================
//            List<StudentFeePayments> payments = em.createQuery(
//                    "SELECT p FROM StudentFeePayments p WHERE p.student.studentId=:sid AND p.status=true AND p.totalBalance>0 AND p.enrollment.enrollmentId<>:eid ORDER BY p.createdAt ASC",
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
//                int enrollmentId = payment.getEnrollment().getEnrollmentId();
//                int usableBal = payment.getTotalBalance()
//                        - getPendingChequeAmountForCourse(em, enrollmentId);
//
//                if (usableBal <= 0) {
//                    continue;
//                }
//
//                int deductAmount = Math.min(usableBal, remainingAmount);
//                String courseType = payment.getCourseType();
//
//                // =====================================================
//                // MONTHLY COURSE
//                // =====================================================
//                if ("MONTHLY".equalsIgnoreCase(courseType)) {
//
//                    Object[] courseData = (Object[]) em.createNativeQuery(
//                            "SELECT c.enrol_year, c.enrol_month, c.comp_year, c.comp_month "
//                            + "FROM course_enrollment ce "
//                            + "JOIN course c ON ce.course_id = c.course_id "
//                            + "WHERE ce.enrollment_id=?"
//                    ).setParameter(1, enrollmentId).getSingleResult();
//
//                    int enrolYear = Integer.parseInt(courseData[0].toString());
//                    int enrolMonth = Integer.parseInt(courseData[1].toString());
//                    int compYear = Integer.parseInt(courseData[2].toString());
//                    int compMonth = Integer.parseInt(courseData[3].toString());
//
//                    java.time.YearMonth start = java.time.YearMonth.of(enrolYear, enrolMonth);
//                    java.time.YearMonth end = java.time.YearMonth.of(compYear, compMonth);
//                    int totalMonths = (int) java.time.temporal.ChronoUnit.MONTHS.between(start, end) + 1;
//
//                    int totalFee = payment.getTotalFee();
//                    int monthlyFee = totalMonths > 0 ? totalFee / totalMonths : totalFee;
//
//                    // LAST PAID MONTH
//                    String lastMonth = (String) em.createNativeQuery(
//                            "SELECT MAX(month_for) FROM student_fee_installments WHERE enrollment_id=? AND status=1"
//                    ).setParameter(1, enrollmentId).getSingleResult();
//
//                    java.time.YearMonth nextMonth = (lastMonth != null)
//                            ? java.time.YearMonth.parse(lastMonth).plusMonths(1)
//                            : start;
//
//                    int remainingMonthly = deductAmount;
//
//                    while (remainingMonthly > 0 && !nextMonth.isAfter(end)) {
//
//                        int pay = Math.min(monthlyFee, remainingMonthly);
//
//                        // INSTALLMENT
//                        StudentFeeInstallments inst = new StudentFeeInstallments();
//                        inst.setStudentFeePaymentsId(payment.getStudentFeePaymentsId());
//                        inst.setEnrollmentId(enrollmentId);
//                        inst.setAmountPaid(pay);
//                        inst.setPaymentDate(paymentDate);
//                        inst.setPaymentMethod(paymentMethod);
//                        inst.setPaymentType("MONTHLY");
//                        inst.setMonthFor(nextMonth.toString());
//                        inst.setInstallmentNo(getNextInstallmentNo(payment.getStudentFeePaymentsId()));
//                        inst.setStatus(1);
//                        em.persist(inst);
//                        em.flush();
//
//                        // CHEQUE DETAILS
//                        if (isCheque) {
//                            StudentFeeChequeDetails chq2 = new StudentFeeChequeDetails();
//                            chq2.setStudentFeeInstallmentsId(inst.getStudentFeeInstallmentsId());
//                            chq2.setChequeNo(chequeNo);
//                            chq2.setBank(bank);
//                            chq2.setBranch(branch);
//                            chq2.setChequeDate(chequeDate);
//                            chq2.setChequeAmount(pay);
//                            chq2.setChequeStatus("PENDING");
//                            chq2.setStatus(1);
//                            em.persist(chq2);
//                        }
//
//                        // MASTER DETAILS
//                        StudentFeeRoundPaymentMasterDetails detailOther = new StudentFeeRoundPaymentMasterDetails();
//                        detailOther.setStudentFeeRoundPaymentMaster(master);
//                        detailOther.setEnrollmentId(enrollmentId);
//                        detailOther.setPaidAmount(pay);
//                        detailOther.setStatus(1);
//                        em.persist(detailOther);
//
//                        remainingMonthly -= pay;
//                        nextMonth = nextMonth.plusMonths(1);
//                    }
//
//                    // UPDATE TOTAL PAID & BALANCE
//                    payment.setTotalPaid(payment.getTotalPaid() + (deductAmount - remainingMonthly));
//                    payment.setTotalBalance(payment.getTotalBalance() - (deductAmount - remainingMonthly));
//                    if (payment.getTotalBalance() <= 0) {
//                        payment.setPaymentStatus("COMPLETE");
//                    }
//                    em.merge(payment);
//
//                } else {
//                    // =====================================================
//                    // NORMAL COURSE
//                    // =====================================================
//                    if (!isCheque) {
//                        payment.setTotalPaid(payment.getTotalPaid() + deductAmount);
//                        payment.setTotalBalance(payment.getTotalBalance() - deductAmount);
//
//                        if (payment.getTotalBalance() <= 0) {
//                            payment.setPaymentStatus("COMPLETE");
//                        }
//
//                        em.merge(payment);
//                    }
//
//                    StudentFeeInstallments inst = new StudentFeeInstallments();
//                    inst.setStudentFeePaymentsId(payment.getStudentFeePaymentsId());
//                    inst.setEnrollmentId(enrollmentId);
//                    inst.setAmountPaid(deductAmount);
//                    inst.setPaymentDate(paymentDate);
//                    inst.setPaymentMethod(paymentMethod);
//                    inst.setPaymentType("ROUND");
//                    inst.setInstallmentNo(getNextInstallmentNo(payment.getStudentFeePaymentsId()));
//                    inst.setStatus(1);
//                    em.persist(inst);
//                    em.flush();
//
//                    if (isCheque) {
//                        StudentFeeChequeDetails chq2 = new StudentFeeChequeDetails();
//                        chq2.setStudentFeeInstallmentsId(inst.getStudentFeeInstallmentsId());
//                        chq2.setChequeNo(chequeNo);
//                        chq2.setBank(bank);
//                        chq2.setBranch(branch);
//                        chq2.setChequeDate(chequeDate);
//                        chq2.setChequeAmount(deductAmount);
//                        chq2.setChequeStatus("PENDING");
//                        chq2.setStatus(1);
//                        em.persist(chq2);
//                    }
//
//                    StudentFeeRoundPaymentMasterDetails detailOther = new StudentFeeRoundPaymentMasterDetails();
//                    detailOther.setStudentFeeRoundPaymentMaster(master);
//                    detailOther.setEnrollmentId(enrollmentId);
//                    detailOther.setPaidAmount(deductAmount);
//                    detailOther.setStatus(1);
//                    em.persist(detailOther);
//                }
//
//                remainingAmount -= deductAmount;
//            }
//
//            tx.commit();
//
//        } catch (Exception e) {
//            if (tx.isActive()) {
//                tx.rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }
    // ********************** new OLD *************************************************************
//    public void processRoundPayment(int studentId, int startEnrollmentId, int paidAmount,
//            Date paymentDate, String paymentMethod,
//            String chequeNo, String bank, String branch, Date chequeDate,
//            String user) {
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
//            // CREATE ROUND MASTER
//            // =====================================================
//            StudentFeeRoundPaymentMaster master = new StudentFeeRoundPaymentMaster();
//            master.setStudentId(studentId);
//            master.setPaymentDate(paymentDate);
//            master.setPaymentMode(paymentMethod);
//            master.setTotalPaid(paidAmount);
//            master.setRemarks("ROUND PAYMENT");
//            master.setUser(user);
//            master.setStatus(1);
//
//            em.persist(master);
//            em.flush();
//
//            // =====================================================
//            // PAY SELECTED COURSE FIRST
//            // =====================================================
//            StudentFeePayments selectedPayment = em.createQuery(
//                    "SELECT p FROM StudentFeePayments p WHERE p.student.studentId=:sid AND p.enrollment.enrollmentId=:eid AND p.status=true",
//                    StudentFeePayments.class)
//                    .setParameter("sid", studentId)
//                    .setParameter("eid", startEnrollmentId)
//                    .getSingleResult();
//
//            int usable = selectedPayment.getTotalBalance()
//                    - getPendingChequeAmountForCourse(em, startEnrollmentId);
//
//            if (usable < 0) {
//                usable = 0;
//            }
//
//            int deduct = Math.min(usable, remainingAmount);
//
//            if (!isCheque) {
//                selectedPayment.setTotalPaid(selectedPayment.getTotalPaid() + deduct);
//                selectedPayment.setTotalBalance(selectedPayment.getTotalBalance() - deduct);
//
//                if (selectedPayment.getTotalBalance() <= 0) {
//                    selectedPayment.setPaymentStatus("COMPLETE");
//                }
//
//                em.merge(selectedPayment);
//            }
//
//            // installment
//            StudentFeeInstallments inst1 = new StudentFeeInstallments();
//            inst1.setStudentFeePaymentsId(selectedPayment.getStudentFeePaymentsId());
//            inst1.setEnrollmentId(startEnrollmentId);
//            inst1.setAmountPaid(deduct);
//            inst1.setPaymentDate(paymentDate);
//            inst1.setPaymentMethod(paymentMethod);
//            inst1.setPaymentType("ROUND");
//            inst1.setInstallmentNo(getNextInstallmentNo(selectedPayment.getStudentFeePaymentsId()));
//            inst1.setStatus(1);
//
//            em.persist(inst1);
//            em.flush();
//
//            if (isCheque) {
//                StudentFeeChequeDetails chq = new StudentFeeChequeDetails();
//                chq.setStudentFeeInstallmentsId(inst1.getStudentFeeInstallmentsId());
//                chq.setChequeNo(chequeNo);
//                chq.setBank(bank);
//                chq.setBranch(branch);
//                chq.setChequeDate(chequeDate);
//                chq.setChequeAmount(deduct);
//                chq.setChequeStatus("PENDING");
//                chq.setStatus(1);
//                em.persist(chq);
//            }
//            // save master details for selected course
//            StudentFeeRoundPaymentMasterDetails detail1 = new StudentFeeRoundPaymentMasterDetails();
//            detail1.setStudentFeeRoundPaymentMaster(master);
//            detail1.setEnrollmentId(startEnrollmentId);
//            detail1.setPaidAmount(deduct);
//            detail1.setStatus(1);
//            em.persist(detail1);
//
//            remainingAmount -= deduct;
//
//            // =====================================================
//            // STEP 1: READ FROM JTABLE (NOT DATABASE)
//            // =====================================================
//            for (int i = 0; i < Fees_Management.fm_fees_course_table.getRowCount(); i++) {
//
//                int enrollmentId = Integer.parseInt(
//                        Fees_Management.fm_fees_course_table.getValueAt(i, 10).toString()
//                );
//
//                int sid = Integer.parseInt(
//                        Fees_Management.fm_fees_course_table.getValueAt(i, 11).toString()
//                );
//
//                // ❌ skip selected course
//                if (enrollmentId == startEnrollmentId) {
//                    continue;
//                }
//
//                System.out.println("Checking Enrollment ID: " + enrollmentId);
//
//                try {
//
//                    // =====================================================
//                    // GET OLDEST PAYMENT RECORD
//                    // =====================================================
//                    StudentFeePayments payment = em.createQuery(
//                            "SELECT p FROM StudentFeePayments p WHERE p.enrollment.enrollmentId=:eid ORDER BY p.createdAt ASC",
//                            StudentFeePayments.class)
//                            .setParameter("eid", enrollmentId)
//                            .setMaxResults(1)
//                            .getSingleResult();
//
//                    System.out.println("Course Type: " + payment.getCourseType());
//
//                    // =====================================================
//                    // FETCH COURSE DETAILS
//                    // =====================================================
//                    Object[] courseData = (Object[]) em.createNativeQuery(
//                            "SELECT c.enrol_year, c.enrol_month, c.comp_year, c.comp_month "
//                            + "FROM course_enrollment ce "
//                            + "JOIN course c ON ce.course_id = c.course_id "
//                            + "WHERE ce.enrollment_id=?"
//                    )
//                            .setParameter(1, enrollmentId)
//                            .getSingleResult();
//
//                    System.out.println("Enroll Year: " + courseData[0]);
//                    System.out.println("Enroll Month: " + courseData[1]);
//                    System.out.println("Complete Year: " + courseData[2]);
//                    System.out.println("Complete Month: " + courseData[3]);
//
//                    // =====================================================
    //// STEP 2: CALCULATE TOTAL MONTHS
//// =====================================================
//                    int enrolYear = Integer.parseInt(courseData[0].toString());
//                    int enrolMonth = Integer.parseInt(courseData[1].toString());
//                    int compYear = Integer.parseInt(courseData[2].toString());
//                    int compMonth = Integer.parseInt(courseData[3].toString());
//
//// Convert to YearMonth
//                    java.time.YearMonth start = java.time.YearMonth.of(enrolYear, enrolMonth);
//                    java.time.YearMonth end = java.time.YearMonth.of(compYear, compMonth);
//
//// total months (inclusive)
//                    int totalMonths = (int) java.time.temporal.ChronoUnit.MONTHS.between(start, end) + 1;
//
//                    System.out.println("Total Months: " + totalMonths);
//
//                    // =====================================================
//// FETCH TOTAL FEE (NOT total_paid)
//// =====================================================
//                    StudentFeePayments pay = em.createQuery(
//                            "SELECT p FROM StudentFeePayments p WHERE p.student.studentId=:sid AND p.enrollment.enrollmentId=:eid",
//                            StudentFeePayments.class)
//                            .setParameter("sid", sid)
//                            .setParameter("eid", enrollmentId)
//                            .getSingleResult();
//
//                    int totalFee = pay.getTotalFee();   // ✅ IMPORTANT CHANGE
//
//                    System.out.println("Total Fee: " + totalFee);
//
//// =====================================================
//// CALCULATE MONTHLY FEE
//// =====================================================
//                    int monthlyFee = 0;
//
//                    if (totalMonths > 0) {
//                        monthlyFee = totalFee / totalMonths;
//                    }
//
//                    System.out.println("Monthly Fee: " + monthlyFee);
//
//                    // =====================================================
//// STEP 3: FIND PAID MONTHS
//// =====================================================
//// Get all paid months for this enrollment
//                    List<String> paidMonths = em.createQuery(
//                            "SELECT i.monthFor FROM StudentFeeInstallments i "
//                            + "WHERE i.enrollmentId=:eid AND i.monthFor IS NOT NULL AND i.status=1 "
//                            + "ORDER BY i.monthFor ASC",
//                            String.class)
//                            .setParameter("eid", enrollmentId)
//                            .getResultList();
//
//// Count paid months
//                    int paidMonthCount = paidMonths.size();
//
//                    System.out.println("Paid Months Count: " + paidMonthCount);
//
//// =====================================================
//// FIND LAST PAID MONTH
//// =====================================================
//                    String lastMonth = null;
//
//                    if (!paidMonths.isEmpty()) {
//                        lastMonth = paidMonths.get(paidMonths.size() - 1);
//                        System.out.println("Last Paid Month: " + lastMonth);
//                    }
//
//// =====================================================
//// FIND NEXT AVAILABLE MONTH
//// =====================================================
//                    java.time.YearMonth nextMonth;
//
//                    if (lastMonth != null) {
//
//                        // convert string (yyyy-MM) → YearMonth
//                        java.time.YearMonth last = java.time.YearMonth.parse(lastMonth);
//
//                        nextMonth = last.plusMonths(1);
//
//                    } else {
//
//                        // if no payment yet → start from course start
//                        nextMonth = java.time.YearMonth.of(enrolYear, enrolMonth);
//                    }
//
//                    System.out.println("Next Pay Month: " + nextMonth);
//
//                } catch (Exception ex) {
//                    System.out.println("Error for enrollment: " + enrollmentId);
//                    ex.printStackTrace();
//                }
//
//            }
//
//            // =====================================================
//            // OTHER COURSES
//            // =====================================================
//            List<StudentFeePayments> payments = em.createQuery(
//                    "SELECT p FROM StudentFeePayments p WHERE p.student.studentId=:sid AND p.status=true AND p.totalBalance>0 AND p.enrollment.enrollmentId<>:eid ORDER BY p.createdAt ASC",
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
//                int enrollmentId = payment.getEnrollment().getEnrollmentId();
//
//                int usableBal = payment.getTotalBalance()
//                        - getPendingChequeAmountForCourse(em, enrollmentId);
//
//                if (usableBal <= 0) {
//                    continue;
//                }
//
//                int deductAmount = Math.min(usableBal, remainingAmount);
//
//                String courseType = payment.getCourseType();
//
//                // =====================================================
//                // 🔥 MONTHLY COURSE
//                // =====================================================
//                if ("MONTHLY".equalsIgnoreCase(courseType)) {
//
//                    // ===== COUNT PAID MONTHS =====
//                    List<String> months = em.createNativeQuery(
//                            "SELECT DISTINCT month_for FROM student_fee_installments "
//                            + "WHERE enrollment_id=? AND month_for IS NOT NULL AND status=1 ORDER BY month_for"
//                    ).setParameter(1, enrollmentId).getResultList();
//
//                    int monthsCount = months.size();
//                    if (monthsCount == 0) {
//                        continue;
//                    }
//
//                    int monthlyFee = payment.getTotalPaid() / monthsCount;
//
//                    // ===== FIND LAST MONTH =====
//                    String lastMonth = (String) em.createNativeQuery(
//                            "SELECT MAX(month_for) FROM student_fee_installments WHERE enrollment_id=? AND status=1"
//                    ).setParameter(1, enrollmentId).getSingleResult();
//
//                    LocalDate nextMonth = LocalDate.parse(lastMonth + "-01").plusMonths(1);
//
//                    int remainingMonthly = deductAmount;
//
//                    while (remainingMonthly > 0) {
//
//                        int pay = Math.min(monthlyFee, remainingMonthly);
//
//                        if (!isCheque) {
//                            payment.setTotalPaid(payment.getTotalPaid() + pay);
//                            payment.setTotalBalance(payment.getTotalBalance() - pay);
//
//                            if (payment.getTotalBalance() <= 0) {
//                                payment.setPaymentStatus("COMPLETE");
//                            }
//
//                            em.merge(payment);
//                        }
//
//                        // INSTALLMENT
//                        StudentFeeInstallments inst = new StudentFeeInstallments();
//                        inst.setStudentFeePaymentsId(payment.getStudentFeePaymentsId());
//                        inst.setEnrollmentId(enrollmentId);
//                        inst.setAmountPaid(pay);
//                        inst.setPaymentDate(paymentDate);
//                        inst.setPaymentMethod(paymentMethod);
//                        inst.setPaymentType("MONTHLY");
//                        inst.setMonthFor(nextMonth.toString().substring(0, 7));
//                        inst.setInstallmentNo(getNextInstallmentNo(payment.getStudentFeePaymentsId()));
//                        inst.setStatus(1);
//
//                        em.persist(inst);
//                        em.flush();
//
//                        if (isCheque) {
//                            StudentFeeChequeDetails chq = new StudentFeeChequeDetails();
//                            chq.setStudentFeeInstallmentsId(inst.getStudentFeeInstallmentsId());
//                            chq.setChequeNo(chequeNo);
//                            chq.setBank(bank);
//                            chq.setBranch(branch);
//                            chq.setChequeDate(chequeDate);
//                            chq.setChequeAmount(pay);
//                            chq.setChequeStatus("PENDING");
//                            chq.setStatus(1);
//                            em.persist(chq);
//                        }
//
//                        // save master details for this enrollment
//                        StudentFeeRoundPaymentMasterDetails detailOther = new StudentFeeRoundPaymentMasterDetails();
//                        detailOther.setStudentFeeRoundPaymentMaster(master);
//                        detailOther.setEnrollmentId(enrollmentId);
//                        detailOther.setPaidAmount(deductAmount);
//                        detailOther.setStatus(1);
//                        em.persist(detailOther);
//
//                        remainingMonthly -= pay;
//                        nextMonth = nextMonth.plusMonths(1);
//                    }
//
//                } else {
//
//                    // =====================================================
//                    // NORMAL COURSE
//                    // =====================================================
//                    if (!isCheque) {
//                        payment.setTotalPaid(payment.getTotalPaid() + deductAmount);
//                        payment.setTotalBalance(payment.getTotalBalance() - deductAmount);
//
//                        if (payment.getTotalBalance() <= 0) {
//                            payment.setPaymentStatus("COMPLETE");
//                        }
//
//                        em.merge(payment);
//                    }
//
//                    StudentFeeInstallments inst = new StudentFeeInstallments();
//                    inst.setStudentFeePaymentsId(payment.getStudentFeePaymentsId());
//                    inst.setEnrollmentId(enrollmentId);
//                    inst.setAmountPaid(deductAmount);
//                    inst.setPaymentDate(paymentDate);
//                    inst.setPaymentMethod(paymentMethod);
//                    inst.setPaymentType("ROUND");
//                    inst.setInstallmentNo(getNextInstallmentNo(payment.getStudentFeePaymentsId()));
//                    inst.setStatus(1);
//
//                    em.persist(inst);
//                    em.flush();
//
//                    if (isCheque) {
//                        StudentFeeChequeDetails chq = new StudentFeeChequeDetails();
//                        chq.setStudentFeeInstallmentsId(inst.getStudentFeeInstallmentsId());
//                        chq.setChequeNo(chequeNo);
//                        chq.setBank(bank);
//                        chq.setBranch(branch);
//                        chq.setChequeDate(chequeDate);
//                        chq.setChequeAmount(deductAmount);
//                        chq.setChequeStatus("PENDING");
//                        chq.setStatus(1);
//                        em.persist(chq);
//                    }
//                }
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
//            e.printStackTrace();
//
//        } finally {
//            em.close();
//        }
//    }

    // **************************** OLD CODE BUT WORKING ********************************************************************
//    public void processRoundPayment(int studentId, int startEnrollmentId, int paidAmount,
//            Date paymentDate, String paymentMethod,
//            String chequeNo, String bank, String branch, Date chequeDate,
//            String user) {
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
//            em.flush();
//
//            if (isCheque) {
//                StudentFeeChequeDetails cheque = new StudentFeeChequeDetails();
//                cheque.setStudentFeeInstallmentsId(installmentFirst.getStudentFeeInstallmentsId());
//                cheque.setChequeNo(chequeNo);
//                cheque.setBank(bank);
//                cheque.setBranch(branch);
//                cheque.setChequeDate(chequeDate);
//                cheque.setChequeAmount(deductSelected);
//                cheque.setChequeStatus("PENDING");
//                cheque.setStatus(1);
//
//                em.persist(cheque);
//            }
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
//                em.flush();
//
//                if (isCheque) {
//                    StudentFeeChequeDetails cheque = new StudentFeeChequeDetails();
//                    cheque.setStudentFeeInstallmentsId(installment.getStudentFeeInstallmentsId());
//                    cheque.setChequeNo(chequeNo);
//                    cheque.setBank(bank);
//                    cheque.setBranch(branch);
//                    cheque.setChequeDate(chequeDate);
//                    cheque.setChequeAmount(deductAmount);
//                    cheque.setChequeStatus("PENDING");
//                    cheque.setStatus(1);
//
//                    em.persist(cheque);
//                }
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
            em.flush();

            // ===== LOG ENTRY =====
            LogHelper.saveLog(em,
                    "STUDENT_PAYMENT",
                    installment.getStudentFeeInstallmentsId(),
                    "INSERT",
                    amount,
                    "CHEQUE",
                    "Cheque added: " + chequeNo + " | Bank: " + bank + " | Branch: " + branch,
                    user
            );

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

    public class MonthDataDTO {

        public int startYear;
        public int startMonth;
        public int endYear;
        public int endMonth;

        public int monthlyFee; // ✅ NEW

        public Set<String> paidMonths = new HashSet<>();
        public Map<String, Integer> monthAmountMap = new HashMap<>();
        public Map<String, String> chequeStatusMap = new HashMap<>();
    }

    public MonthDataDTO getMonthData(int enrollmentId) {

        EntityManager em = HibernateConfig.getEntityManager();
        MonthDataDTO dto = new MonthDataDTO();

        try {

            // ============================
            // CHEQUE STATUS MAP
            // ============================
            List<Object[]> chequeList = em.createNativeQuery(
                    "SELECT i.month_for, c.cheque_status "
                    + "FROM student_fee_cheque_details c "
                    + "JOIN student_fee_installments i "
                    + "ON c.student_fee_installments_id = i.student_fee_installments_id "
                    + "WHERE i.enrollment_id = ? AND c.status = 1"
            )
                    .setParameter(1, enrollmentId)
                    .getResultList();

            for (Object[] row : chequeList) {
                if (row[0] != null) {
                    dto.chequeStatusMap.put(
                            row[0].toString(),
                            row[1] != null ? row[1].toString() : ""
                    );
                }
            }

            // ============================
            // COURSE RANGE
            // ============================
            Object[] course = (Object[]) em.createNativeQuery(
                    "SELECT c.enrol_year, c.enrol_month, c.comp_year, c.comp_month "
                    + "FROM course_enrollment ce "
                    + "JOIN course c ON ce.course_id = c.course_id "
                    + "WHERE ce.enrollment_id = ?"
            )
                    .setParameter(1, enrollmentId)
                    .getSingleResult();

            dto.startYear = ((Number) course[0]).intValue();
            dto.startMonth = ((Number) course[1]).intValue();
            dto.endYear = ((Number) course[2]).intValue();
            dto.endMonth = ((Number) course[3]).intValue();

            // ============================
            // 🔥 FIXED: SUM INSTALLMENTS
            // ============================
            List<Object[]> list = em.createNativeQuery(
                    "SELECT month_for, amount_paid "
                    + "FROM student_fee_installments "
                    + "WHERE enrollment_id = ? AND status = 1 AND month_for IS NOT NULL"
            )
                    .setParameter(1, enrollmentId)
                    .getResultList();

            for (Object[] row : list) {

                String monthFor = row[0].toString();
                int amount = ((Number) row[1]).intValue();

                // ✅ SUM LOGIC (IMPORTANT FIX)
                dto.monthAmountMap.put(
                        monthFor,
                        dto.monthAmountMap.getOrDefault(monthFor, 0) + amount
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return dto;
    }

    // WORKING CODE WITHOUT SUMMING same year+Month ***********************
//    public MonthDataDTO getMonthData(int enrollmentId) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        MonthDataDTO dto = new MonthDataDTO();
//
//        try {
//
//            List<Object[]> chequeList = em.createNativeQuery(
//                    "SELECT i.month_for, c.cheque_status "
//                    + "FROM student_fee_cheque_details c "
//                    + "JOIN student_fee_installments i "
//                    + "ON c.student_fee_installments_id = i.student_fee_installments_id "
//                    + "WHERE i.enrollment_id = ? AND c.status = 1"
//            )
//                    .setParameter(1, enrollmentId)
//                    .getResultList();
//
//            for (Object[] row : chequeList) {
//                if (row[0] != null) {
//                    dto.chequeStatusMap.put(
//                            row[0].toString(),
//                            row[1] != null ? row[1].toString() : ""
//                    );
//                }
//            }
//
//            // ============================
//            // 1. COURSE RANGE
//            // ============================
//            Object[] course = (Object[]) em.createNativeQuery(
//                    "SELECT c.enrol_year, c.enrol_month, c.comp_year, c.comp_month "
//                    + "FROM course_enrollment ce "
//                    + "JOIN course c ON ce.course_id = c.course_id "
//                    + "WHERE ce.enrollment_id = ?"
//            )
//                    .setParameter(1, enrollmentId)
//                    .getSingleResult();
//
//            dto.startYear = ((Number) course[0]).intValue();
//            dto.startMonth = ((Number) course[1]).intValue();
//            dto.endYear = ((Number) course[2]).intValue();
//            dto.endMonth = ((Number) course[3]).intValue();
//
//            // ============================
//            // 2. LOAD INSTALLMENTS (IMPORTANT)
//            // ============================
//            List<Object[]> list = em.createNativeQuery(
//                    "SELECT month_for, amount_paid "
//                    + "FROM student_fee_installments "
//                    + "WHERE enrollment_id = ? AND status = 1"
//            )
//                    .setParameter(1, enrollmentId)
//                    .getResultList();
//
//            for (Object[] row : list) {
//
//                String monthFor = (String) row[0];
//                int amount = ((Number) row[1]).intValue();
//
//                dto.monthAmountMap.put(monthFor, amount);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//
//        return dto;
//    }
    // ************ OLD WORKING CODE **************************
//    public class MonthDataDTO {
//
//        public int startYear;
//        public int startMonth;
//        public int endYear;
//        public int endMonth;
//
//        public int monthlyFee; // ✅ NEW
//
//        public Set<String> paidMonths = new HashSet<>();
//        public Map<String, Integer> monthAmountMap = new HashMap<>();
//    }
//
//    public MonthDataDTO getMonthData(int enrollmentId) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        MonthDataDTO dto = new MonthDataDTO();
//
//        try {
//
//            // ============================
//            // 1. COURSE RANGE
//            // ============================
//            Object[] course = (Object[]) em.createNativeQuery(
//                    "SELECT c.enrol_year, c.enrol_month, c.comp_year, c.comp_month "
//                    + "FROM course_enrollment ce "
//                    + "JOIN course c ON ce.course_id = c.course_id "
//                    + "WHERE ce.enrollment_id = ?"
//            )
//                    .setParameter(1, enrollmentId)
//                    .getSingleResult();
//
//            dto.startYear = ((Number) course[0]).intValue();
//            dto.startMonth = ((Number) course[1]).intValue();
//            dto.endYear = ((Number) course[2]).intValue();
//            dto.endMonth = ((Number) course[3]).intValue();
//
//            // ============================
//            // 2. LOAD INSTALLMENTS (IMPORTANT)
//            // ============================
//            List<Object[]> list = em.createNativeQuery(
//                    "SELECT month_for, amount_paid "
//                    + "FROM student_fee_installments "
//                    + "WHERE enrollment_id = ? AND status = 1"
//            )
//                    .setParameter(1, enrollmentId)
//                    .getResultList();
//
//            for (Object[] row : list) {
//
//                String monthFor = (String) row[0];
//                int amount = ((Number) row[1]).intValue();
//
//                dto.monthAmountMap.put(monthFor, amount);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//
//        return dto;
//    }
    public int getPendingChequeAmount(int enrollmentId) {

        EntityManager em = HibernateConfig.getEntityManager();
        int total = 0;

        try {

            Object result = em.createNativeQuery(
                    "SELECT COALESCE(SUM(c.cheque_amount),0) "
                    + "FROM student_fee_cheque_details c "
                    + "JOIN student_fee_installments i "
                    + "ON c.student_fee_installments_id = i.student_fee_installments_id "
                    + "WHERE i.enrollment_id = ? "
                    + "AND c.cheque_status = 'PENDING' "
                    + "AND c.status = 1"
            )
                    .setParameter(1, enrollmentId)
                    .getSingleResult();

            if (result != null) {
                total = ((Number) result).intValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return total;
    }
}
