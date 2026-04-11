
package JPA_DAO.Student_Management;

import Classes.HibernateConfig;
import Entities.Student_Management.FeeTypes;
import javax.persistence.EntityManager;

public class FeeTypesDAO {

    // SAVE
    public void save(FeeTypes fee) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(fee);
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

    // SOFT DELETE
    public void softDelete(int feeTypeId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            FeeTypes fee = em.find(FeeTypes.class, feeTypeId);
            if (fee != null) {
                fee.setStatus(0);
            }

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
}
