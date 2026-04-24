package JPA_DAO.Accounts;

import Classes.HibernateConfig;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

public class Cheque_Dao {

    public List<Object[]> getChequeListByStatus(String status) {

        EntityManager em = HibernateConfig.getEntityManager();
        List<Object[]> list = new ArrayList<>();

        try {

            list = em.createNativeQuery(
                    "SELECT "
                    + " MIN(scd.student_fee_cheque_details_id) AS cheque_id, "
                    + " scd.category, "
                    // ✅ SUB CATEGORY
                    + " CASE "
                    + "   WHEN scd.reference_type = 'ROUND' THEN 'ROUND' "
                    + "   WHEN scd.reference_type = 'ADMISSION' THEN 'ADMISSION' "
                    + " END AS sub_category, "
                    // ✅ STUDENT NAME (FIXED)
                    + " s.full_name, "
                    + " scd.cheque_no, "
                    + " scd.bank, "
                    + " scd.branch, "
                    + " scd.cheque_date, "
                    // 🔥 FIXED AMOUNT (NO DUPLICATION)
                    + " SUM(DISTINCT scd.student_fee_cheque_details_id * 0 + scd.cheque_amount) AS total_amount, "
                    + " scd.cheque_status "
                    + "FROM student_fee_cheque_details scd "
                    // 🔥 CORRECT STUDENT JOIN
                    + "LEFT JOIN student s ON s.student_id = ( "
                    + "   CASE "
                    + "       WHEN scd.reference_type = 'ADMISSION' THEN ( "
                    + "           SELECT sfp.student_id "
                    + "           FROM student_fee_payments sfp "
                    + "           WHERE sfp.enrollment_id = scd.reference_id "
                    + "           LIMIT 1 "
                    + "       ) "
                    + "       WHEN scd.reference_type = 'ROUND' THEN ( "
                    + "           SELECT rm.student_id "
                    + "           FROM student_fee_round_payment_master rm "
                    + "           WHERE rm.student_fee_round_payment_master_id = scd.reference_id "
                    + "           LIMIT 1 "
                    + "       ) "
                    + "   END "
                    + ") "
                    + "WHERE scd.cheque_status = ? "
                    + "AND scd.status = 1 "
                    + "AND scd.category = 'STUDENT' "
                    // 🔥 IMPORTANT GROUPING (CHEQUE LEVEL)
                    + "GROUP BY "
                    + " scd.cheque_no, "
                    + " scd.bank, "
                    + " scd.branch, "
                    + " scd.cheque_date, "
                    + " scd.cheque_status, "
                    + " scd.category, "
                    + " sub_category, "
                    + " s.full_name "
                    + "ORDER BY scd.cheque_date ASC"
            )
                    .setParameter(1, status)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return list;
    }

//    public List<Object[]> getChequeListByStatus(String status) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        List<Object[]> list = new ArrayList<>();
//
//        try {
//
//            list = em.createNativeQuery(
//                    "SELECT "
//                    + " MIN(scd.student_fee_cheque_details_id) AS cheque_id, "
//                    + " scd.category, "
//                    // 🔥 SUB CATEGORY FIX
//                    + " CASE "
//                    + "   WHEN scd.reference_type = 'ROUND' THEN 'ROUND' "
//                    + "   WHEN scd.reference_type = 'ADMISSION' THEN 'ADMISSION' "
//                    + "   ELSE scd.reference_type "
//                    + " END AS sub_category, "
//                    + " s.full_name, "
//                    + " scd.cheque_no, "
//                    + " scd.bank, "
//                    + " scd.branch, "
//                    + " scd.cheque_date, "
//                    + " SUM(scd.cheque_amount) AS total_amount, "
//                    + " scd.cheque_status "
//                    + "FROM student_fee_cheque_details scd "
//                    // 🔥 STUDENT FETCH (IMPORTANT)
//                    + "LEFT JOIN student_fee_payments sfp "
//                    + "  ON sfp.enrollment_id = scd.reference_id "
//                    + "LEFT JOIN student s "
//                    + "  ON s.student_id = sfp.student_id "
//                    + "WHERE scd.cheque_status = ? "
//                    + "AND scd.status = 1 "
//                    + "AND scd.category = 'STUDENT' "
//                    // 🔥 GROUPING
//                    + "GROUP BY "
//                    + " scd.category, "
//                    + " sub_category, "
//                    + " s.full_name, "
//                    + " scd.cheque_no, "
//                    + " scd.bank, "
//                    + " scd.branch, "
//                    + " scd.cheque_date, "
//                    + " scd.cheque_status "
//                    + "ORDER BY scd.cheque_date ASC"
//            )
//                    .setParameter(1, status)
//                    .getResultList();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//
//        return list;
//    }
//    public List<Object[]> getChequeListByStatus(String status) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//        List<Object[]> list = new ArrayList<>();
//
//        try {
//
//            list = em.createNativeQuery(
//                    "SELECT " +
//                    " MIN(scd.student_fee_cheque_details_id) AS cheque_id, " +   // keep one id
//                    " scd.cheque_no, " +
//                    " scd.bank, " +
//                    " scd.branch, " +
//                    " scd.cheque_date, " +
//                    " SUM(scd.cheque_amount) AS total_amount, " +               // 🔥 sum amounts
//                    " scd.cheque_status, " +
//
//                    " s.admission_no, " +
//                    " s.full_name, " +
//
//                    " c.batch, " +
//                    " c.course_name " +
//
//                    "FROM student_fee_cheque_details scd " +
//
//                    "JOIN student_fee_installments sfi " +
//                    " ON scd.student_fee_installments_id = sfi.student_fee_installments_id " +
//
//                    "JOIN student_fee_payments sfp " +
//                    " ON sfi.student_fee_payments_id = sfp.student_fee_payments_id " +
//
//                    "JOIN student s " +
//                    " ON sfp.student_id = s.student_id " +
//
//                    "JOIN course_enrollment ce " +
//                    " ON sfp.enrollment_id = ce.enrollment_id " +
//
//                    "JOIN course c " +
//                    " ON ce.course_id = c.course_id " +
//
//                    "WHERE scd.cheque_status = ? " +
//                    "AND scd.status = 1 " +
//
//                    // 🔥 GROUPING LOGIC
//                    "GROUP BY " +
//                    " scd.cheque_no, " +
//                    " scd.bank, " +
//                    " scd.branch, " +
//                    " scd.cheque_date, " +
//                    " scd.cheque_status, " +
//                    " s.admission_no, " +
//                    " s.full_name, " +
//                    " c.batch, " +
//                    " c.course_name " +
//
//                    "ORDER BY scd.cheque_date ASC"
//            )
//                    .setParameter(1, status)
//                    .getResultList();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//
//        return list;
//    }
}
