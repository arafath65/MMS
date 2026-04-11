package JPA_DAO.Inventory;

import Classes.HibernateConfig;
import Classes.LogHelper;
import Entities.Inventory.Item;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class ItemDAO {

    // ---------------- SAVE ----------------
    public void save(Item item) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.persist(item);
        
        em.getTransaction().commit();
        em.close();
    }

    // ---------------- UPDATE ----------------
    public void update(Item item) {
        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();
        em.merge(item);
        em.getTransaction().commit();
        em.close();
    }

    // ---------------- FIND ALL ----------------
    public List<Item> findAll() {
        EntityManager em = HibernateConfig.getEntityManager();

        List<Item> list = em
                .createNamedQuery("Item.findAll", Item.class)
                .getResultList();

        em.close();
        return list;
    }

    // ---------------- FIND BY ID ----------------
    public Item findById(int id) {
        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<Item> query = em.createNamedQuery("Item.findById", Item.class);
        query.setParameter("id", id);

        Item item = query.getSingleResult();
        em.close();

        return item;
    }

    // ---------------- SOFT DELETE ----------------
    public void softDelete(int itemId) {

        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        Query query = em.createQuery(
                "UPDATE Item i SET i.status = 0 WHERE i.itemId = :id"
        );
        query.setParameter("id", itemId);
        query.executeUpdate();

        em.getTransaction().commit();
        em.close();
    }

    public int getItemIdByName(String itemName) {

        EntityManager em = HibernateConfig.getEntityManager();
        int itemId = -1;

        try {
            Query query = em.createQuery(
                    "SELECT i.itemId FROM Item i WHERE i.itemName = :name AND i.status = 1"
            );
            query.setParameter("name", itemName);

            itemId = (int) query.getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return itemId;
    }
}
