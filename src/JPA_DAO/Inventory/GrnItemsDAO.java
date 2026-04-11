
package JPA_DAO.Inventory;

import Classes.HibernateConfig;
import Entities.Inventory.GrnItems;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class GrnItemsDAO {

    // SAVE
    public void save(GrnItems item) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.persist(item);
        em.getTransaction().commit();
        em.close();
    }

    // SOFT DELETE
    public void softDelete(int id) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        Query query = em.createQuery(
            "UPDATE GrnItems g SET g.status = 0 WHERE g.grnItemsId = :id"
        );
        query.setParameter("id", id);
        query.executeUpdate();

        em.getTransaction().commit();
        em.close();
    }
}
