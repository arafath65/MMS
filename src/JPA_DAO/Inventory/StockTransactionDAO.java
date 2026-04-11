/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPA_DAO.Inventory;

import Classes.HibernateConfig;
import Entities.Inventory.StockTransaction;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class StockTransactionDAO {

    // SAVE
    public void save(StockTransaction st) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.persist(st);
        em.getTransaction().commit();
        em.close();
    }

    // SOFT DELETE
    public void softDelete(int id) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        Query query = em.createQuery(
            "UPDATE StockTransaction s SET s.status = 0 WHERE s.stockTransactionsId = :id"
        );
        query.setParameter("id", id);
        query.executeUpdate();

        em.getTransaction().commit();
        em.close();
    }
}
