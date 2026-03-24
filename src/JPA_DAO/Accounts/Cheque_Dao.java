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
                    "SELECT " +
                    " MIN(scd.student_fee_cheque_details_id) AS cheque_id, " +   // keep one id
                    " scd.cheque_no, " +
                    " scd.bank, " +
                    " scd.branch, " +
                    " scd.cheque_date, " +
                    " SUM(scd.cheque_amount) AS total_amount, " +               // 🔥 sum amounts
                    " scd.cheque_status, " +

                    " s.admission_no, " +
                    " s.full_name, " +

                    " c.batch, " +
                    " c.course_name " +

                    "FROM student_fee_cheque_details scd " +

                    "JOIN student_fee_installments sfi " +
                    " ON scd.student_fee_installments_id = sfi.student_fee_installments_id " +

                    "JOIN student_fee_payments sfp " +
                    " ON sfi.student_fee_payments_id = sfp.student_fee_payments_id " +

                    "JOIN student s " +
                    " ON sfp.student_id = s.student_id " +

                    "JOIN course_enrollment ce " +
                    " ON sfp.enrollment_id = ce.enrollment_id " +

                    "JOIN course c " +
                    " ON ce.course_id = c.course_id " +

                    "WHERE scd.cheque_status = ? " +
                    "AND scd.status = 1 " +

                    // 🔥 GROUPING LOGIC
                    "GROUP BY " +
                    " scd.cheque_no, " +
                    " scd.bank, " +
                    " scd.branch, " +
                    " scd.cheque_date, " +
                    " scd.cheque_status, " +
                    " s.admission_no, " +
                    " s.full_name, " +
                    " c.batch, " +
                    " c.course_name " +

                    "ORDER BY scd.cheque_date ASC"
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
}