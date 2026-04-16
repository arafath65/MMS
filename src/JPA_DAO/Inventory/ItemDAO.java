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

    public Object[] getItemLatestPriceAndStock(int itemId, String feeName) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            double latestPrice = 0.0;
            double currentStock = 0.0;

            // =========================
            // CASE 1: SERVICE (itemId = 0)
            // =========================
            if (itemId == 0) {

                Query feeQuery = em.createQuery(
                        "SELECT f.defaultAmount FROM FeeTypes f "
                        + "WHERE f.feeName = :name AND f.status = 1"
                );

                feeQuery.setParameter("name", feeName);
                feeQuery.setMaxResults(1);

                List<Double> feeList = feeQuery.getResultList();

                if (!feeList.isEmpty()) {
                    latestPrice = feeList.get(0);
                }

                // No stock for services
                return new Object[]{latestPrice, 0.0};
            }

            // =========================
            // CASE 2: NORMAL ITEM
            // =========================
            // ---------- LATEST PRICE ----------
            Query priceQuery = em.createQuery(
                    "SELECT gi.unitPrice FROM GrnItems gi "
                    + "WHERE gi.itemId = :itemId AND gi.status = 1 "
                    + "ORDER BY gi.grnItemsId DESC"
            );

            priceQuery.setParameter("itemId", itemId);
            priceQuery.setMaxResults(1);

            List<Double> priceList = priceQuery.getResultList();
            latestPrice = priceList.isEmpty() ? 0.0 : priceList.get(0);

            // ---------- STOCK IN ----------
            Query inQuery = em.createQuery(
                    "SELECT SUM(st.quantity) FROM StockTransaction st "
                    + "WHERE st.itemId = :itemId "
                    + "AND st.transactionType = 'IN' "
                    + "AND st.status = 1"
            );

            inQuery.setParameter("itemId", itemId);

            Double inQty = (Double) inQuery.getSingleResult();
            if (inQty == null) {
                inQty = 0.0;
            }

            // ---------- STOCK OUT ----------
            Query outQuery = em.createQuery(
                    "SELECT SUM(st.quantity) FROM StockTransaction st "
                    + "WHERE st.itemId = :itemId "
                    + "AND st.transactionType <> 'IN' "
                    + "AND st.status = 1"
            );

            outQuery.setParameter("itemId", itemId);

            Double outQty = (Double) outQuery.getSingleResult();
            if (outQty == null) {
                outQty = 0.0;
            }

            currentStock = inQty - outQty;

            return new Object[]{latestPrice, currentStock};

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return new Object[]{0.0, 0.0};
    }


}
