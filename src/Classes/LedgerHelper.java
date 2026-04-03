package Classes;

import javax.persistence.EntityManager;

public class LedgerHelper {

    public static void saveLedger(
            EntityManager em,
            int amount,
            String entryType, // CREDIT / DEBIT
            String description,
            String module, // STUDENT_PAYMENT, EXPENSE, etc.
            int moduleId,
            String paymentMethod,
            String category, // MONTHLY_FEE, EXPENSE, etc.
            String user
    ) {

        em.createNativeQuery(
                "INSERT INTO ledger_entries "
                + "(entry_date, entry_type, amount, description, related_module, related_module_id, payment_method, category, user, created_at, updated_at, status) "
                + "VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 1)"
        )
                .setParameter(1, entryType)
                .setParameter(2, amount)
                .setParameter(3, description)
                .setParameter(4, module)
                .setParameter(5, moduleId)
                .setParameter(6, paymentMethod)
                .setParameter(7, category)
                .setParameter(8, user)
                .executeUpdate();
    }
}
