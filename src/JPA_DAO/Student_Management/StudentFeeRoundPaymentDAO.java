
package JPA_DAO.Student_Management;

import Classes.HibernateConfig;
import Entities.Student_Management.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.List;

public class StudentFeeRoundPaymentDAO {

    // =========================
    // SAVE ROUND PAYMENT MASTER + DETAILS
    // =========================
    public Integer saveRoundPayment(
            StudentFeeRoundPaymentMaster master,
            List<StudentFeeRoundPaymentMasterDetails> detailsList
    ) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1. SAVE MASTER
            em.persist(master);
            em.flush(); // get ID

            // 2. SAVE DETAILS
            for (StudentFeeRoundPaymentMasterDetails d : detailsList) {
                d.setStudentFeeRoundPaymentMaster(master);
                em.persist(d);
            }

            tx.commit();

            return master.getStudentFeeRoundPaymentMasterId();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return null;

        } finally {
            em.close();
        }
    }
}
