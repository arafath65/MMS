/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPA_DAO.Inventory;

import Classes.HibernateConfig;
import Entities.Inventory.Grn;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class GrnDAO {

    // SAVE
    public void save(Grn grn) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.persist(grn);
        em.getTransaction().commit();
        em.close();
    }

    // SOFT DELETE
    public void softDelete(int grnId) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        Query query = em.createQuery(
            "UPDATE Grn g SET g.status = 0 WHERE g.grnId = :id"
        );
        query.setParameter("id", grnId);
        query.executeUpdate();

        em.getTransaction().commit();
        em.close();
    }
}
