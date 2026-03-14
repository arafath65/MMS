
package Additional;

import Classes.HibernateConfig;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class LedgerDAO {

    public void saveLedgerEntry(
            Date entryDate,
            String entryType,
            int amount,
            String description,
            String relatedModule,
            Integer relatedModuleId,
            String paymentMethod,
            String category,
            String user
    ) {

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            tx.begin();

            LedgerEntries ledger = new LedgerEntries();

            ledger.setEntryDate(entryDate);
            ledger.setEntryType(entryType);
            ledger.setAmount(amount);
            ledger.setDescription(description);
            ledger.setRelatedModule(relatedModule);
            ledger.setRelatedModuleId(relatedModuleId);
            ledger.setPaymentMethod(paymentMethod);
            ledger.setCategory(category);
            ledger.setUser(user);
            ledger.setCreatedAt(new Date());
            ledger.setUpdatedAt(new Date());
            ledger.setStatus(1);

            em.persist(ledger);

            tx.commit();

        } catch (Exception e) {

            if (tx.isActive()) {
                tx.rollback();
            }

            e.printStackTrace();

        } finally {
            em.close();
        }

    }

}