/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPA_DAO.Student_Management;

import Entities.Student_Management.StudentAdditionalFees;
import Classes.HibernateConfig;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class StudentAdditionalFeesDAO {

    // =========================
    // SAVE
    // =========================
    public int save(StudentAdditionalFees fee) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(fee);
            em.flush(); // ✅ IMPORTANT → generates ID

            int generatedId = fee.getStudentAdditionalFeesId();

            em.getTransaction().commit();

            return generatedId; // ✅ return ID

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();
        } finally {
            em.close();
        }

        return -1;
    }
//    public void save(StudentAdditionalFees fee) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//
//            em.getTransaction().begin();
//
//            em.persist(fee);
//
//            em.getTransaction().commit();
//
//        } catch (Exception e) {
//
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//
//            e.printStackTrace();
//
//        } finally {
//            em.close();
//        }
//    }

    // =========================
    // SOFT DELETE
    // =========================
    public void softDelete(int id) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            em.getTransaction().begin();

            em.createQuery(
                    "UPDATE StudentAdditionalFees s SET s.status = 0 WHERE s.studentAdditionalFeesId = :id"
            ).setParameter("id", id).executeUpdate();

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {
            em.close();
        }
    }

    public int getFeeTypeId(int itemId, String feeName) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            Query query;

            // =========================
            // CASE 1: ITEM BASED
            // =========================
            if (itemId > 0) {

                query = em.createQuery(
                        "SELECT f.feeTypeId FROM FeeTypes f "
                        + "WHERE f.itemId = :itemId AND f.status = 1"
                );

                query.setParameter("itemId", itemId);

            } // =========================
            // CASE 2: SERVICE (NO ITEM)
            // =========================
            else {

                query = em.createQuery(
                        "SELECT f.feeTypeId FROM FeeTypes f "
                        + "WHERE f.feeName = :feeName AND f.status = 1"
                );

                query.setParameter("feeName", feeName);
            }

            query.setMaxResults(1);

            List<Integer> list = query.getResultList();

            if (!list.isEmpty()) {
                return list.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return -1;
    }

    public List<Object[]> getStudentFeeDues(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            List<Object[]> list = em.createNativeQuery(
                    "SELECT saf.fee_type_id, "
                    + "ft.fee_name, "
                    + "ft.item_id, "
                    + "ft.default_amount, "
                    + "COALESCE(SUM(saf.amount),0) AS total_issued "
                    + "FROM student_additional_fees saf "
                    + "JOIN fee_types ft ON saf.fee_type_id = ft.fee_type_id "
                    + "WHERE saf.student_id = ? "
                    + "AND saf.status = 1 "
                    + "GROUP BY saf.fee_type_id, ft.fee_name, ft.item_id, ft.default_amount"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            return list;

        } finally {
            em.close();
        }
    }

    public List<Object[]> getStudentCourseDues(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            return em.createNativeQuery(
                    "SELECT sfp.enrollment_id, c.course_name, "
                    + "sfp.course_type, sfp.total_balance "
                    + "FROM student_fee_payments sfp "
                    + "JOIN course_enrollment ce ON sfp.enrollment_id = ce.enrollment_id "
                    + "JOIN course c ON ce.course_id = c.course_id "
                    + "WHERE sfp.student_id = ? "
                    + "AND sfp.payment_status = 'ACTIVE' "
                    + "AND sfp.status = 1 "
                    + "AND sfp.total_balance > 0 "
                    + "ORDER BY sfp.created_at ASC"
            )
                    .setParameter(1, studentId)
                    .getResultList();

        } finally {
            em.close();
        }
    }

    public void printAdditionalFeeSummary(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            // =========================================
            // STEP 1: SUM ISSUED AMOUNT PER FEE TYPE
            // =========================================
            List<Object[]> feeList = em.createNativeQuery(
                    "SELECT fee_type_id, SUM(amount) "
                    + "FROM student_additional_fees "
                    + "WHERE student_id = ? AND status = 1 "
                    + "GROUP BY fee_type_id"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            for (Object[] feeRow : feeList) {

                int feeTypeId = Integer.parseInt(feeRow[0].toString());
                double issuedSum = Double.parseDouble(feeRow[1].toString());

                // =========================================
                // STEP 2: GET ALL SAF IDs FOR THIS FEE TYPE
                // =========================================
                List<Object> safIds = em.createNativeQuery(
                        "SELECT student_additional_fees_id "
                        + "FROM student_additional_fees "
                        + "WHERE student_id = ? "
                        + "AND fee_type_id = ? "
                        + "AND status = 1"
                )
                        .setParameter(1, studentId)
                        .setParameter(2, feeTypeId)
                        .getResultList();

                // =========================================
                // STEP 3: SUM PAYMENTS USING THOSE IDS
                // =========================================
                double paidSum = 0.0;

                if (!safIds.isEmpty()) {

                    String ids = safIds.stream()
                            .map(Object::toString)
                            .reduce((a, b) -> a + "," + b)
                            .orElse("0");

                    Object paidObj = em.createNativeQuery(
                            "SELECT COALESCE(SUM(amount_paid),0) "
                            + "FROM student_additional_fee_payments "
                            + "WHERE student_additional_fees_id IN (" + ids + ") "
                            + "AND status = 1"
                    ).getSingleResult();

                    if (paidObj != null) {
                        paidSum = Double.parseDouble(paidObj.toString());
                    }
                }

                // =========================================
                // STEP 4: FINAL DUE
                // =========================================
                double due = issuedSum - paidSum;

                // =========================================
                // PRINT RESULTS
                // =========================================
                System.out.println("==================================");
                System.out.println("Fee Type ID: " + feeTypeId);
                System.out.println("Issued Total : " + issuedSum);
                System.out.println("Paid Total   : " + paidSum);
                System.out.println("Remaining Due: " + due);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
