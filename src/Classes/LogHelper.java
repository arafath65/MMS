package Classes;

import javax.persistence.EntityManager;

public class LogHelper {

    public static void saveLog(
            EntityManager em,
            String module, // STUDENT_PAYMENT, EXPENSE, EXAM, etc.
            Integer moduleId, // can be null
            String action, // INSERT, UPDATE, DELETE
            int amount, // can be null
            String paymentMethod,
            String description,
            String user
    ) {

        em.createNativeQuery(
                "INSERT INTO system_logs "
                + "(module, module_id, action, amount, payment_method, description, user, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())"
        )
                .setParameter(1, module)
                .setParameter(2, moduleId)
                .setParameter(3, action)
                .setParameter(4, amount)
                .setParameter(5, paymentMethod)
                .setParameter(6, description)
                .setParameter(7, user)
                .executeUpdate();
    }
}
