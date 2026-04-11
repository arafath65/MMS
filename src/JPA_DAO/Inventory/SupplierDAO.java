/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPA_DAO.Inventory;

import Classes.HibernateConfig;
import Entities.Inventory.Supplier;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import javax.persistence.Query;

public class SupplierDAO {

    // ------------------------
    // Save
    // ------------------------
    public void save(Supplier supplier) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.persist(supplier);
        em.getTransaction().commit();
        em.close();
    }

    // ------------------------
    // Update
    // ------------------------
    public void update(Supplier supplier) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.merge(supplier);
        em.getTransaction().commit();
        em.close();
    }

    public List<Supplier> search(String name) {
        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<Supplier> query = em.createNamedQuery("Supplier.search", Supplier.class);
        query.setParameter("name", "%" + name + "%");

        List<Supplier> list = query.getResultList();
        em.close();

        return list;
    }

    // ------------------------
    // Find All
    // ------------------------
    public List<Supplier> findAll() {
        EntityManager em = HibernateConfig.getEntityManager();
        List<Supplier> list = em.createNamedQuery("Supplier.findAll", Supplier.class).getResultList();
        em.close();
        return list;
    }

    // ------------------------
    // Find By Name
    // ------------------------
    public List<Supplier> findByName(String name, Boolean status) {
        EntityManager em = HibernateConfig.getEntityManager();
        TypedQuery<Supplier> query = em.createNamedQuery("Supplier.findByName", Supplier.class);
        query.setParameter("supplierName", "%" + name + "%");
        query.setParameter("status", status);
        List<Supplier> list = query.getResultList();
        em.close();
        return list;
    }

    // ------------------------
    // Find By ID
    // ------------------------
    public Supplier findById(int id) {
        EntityManager em = HibernateConfig.getEntityManager();
        TypedQuery<Supplier> query = em.createNamedQuery("Supplier.findById", Supplier.class);
        query.setParameter("id", id);
        Supplier supplier = query.getSingleResult();
        em.close();
        return supplier;
    }
    
    public int getSupplierIdByName(String supplierName) {

        EntityManager em = HibernateConfig.getEntityManager();
        int supplierId = -1;

        try {
            Query query = em.createQuery(
                    "SELECT s.suppliersId FROM Supplier s WHERE s.supplierName = :name AND s.status = 1"
            );
            query.setParameter("name", supplierName);

            supplierId = (int) query.getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return supplierId;
    }
}
